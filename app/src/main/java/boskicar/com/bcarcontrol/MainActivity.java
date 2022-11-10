package boskicar.com.bcarcontrol;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import io.github.controlwear.virtual.joystick.android.JoystickView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = "BCAR";

    public static final String BASE_URL_PRE = "http://";

    public static final String BASE_URL_SUF = ":3333";

    private BoskicarServerAPI boskicarServerAPI;

    private JoystickView joystickFBView;

    private JoystickView joystickLRView;

    private Switch switchMobileControl;

    private Switch switchLights;

    private Switch switchFans;

    private DateTimeFormatter DATET_FORMAT =  DateTimeFormatter.ofPattern("HH:mm:ss");

    private Button buttonShutdown;

    private Button buttonReboot;

    private EditText ipText;

    private TextView statusText;

    private ImageView buttonIPChange;

    private StatusHandler statusHandler;

    private HandlerThread handlerThread;

    private int fbOrderSpeed;

    private LocalDateTime lastFBOrderDate = LocalDateTime.now().minusYears(1);

    private LocalDateTime lastLROrderDate = LocalDateTime.now().minusYears(1);

    private int lrStrength;

    private static class StatusHandler extends Handler
    {
        private static final int STATUS_TIMER = 3001;

        private static final int DELAY = 1500;

        private WeakReference<MainActivity> activity;

        StatusHandler(Looper looper, MainActivity activity) {
            super(looper);
            this.activity = new WeakReference<>(activity);
            this.checkDelayed();
        }

        public void checkDelayed()
        {
            this.removeMessages(STATUS_TIMER);
            this.sendEmptyMessageDelayed(STATUS_TIMER, DELAY);
        }

        @Override
        public void handleMessage(Message msg)
        {
            try {
                activity.get().updateConfigStatus(false);
            }
            catch (Exception e)
            {
                LogUtil.Log_e(TAG, "handleMessage exception: "+e.getMessage());
            }
            finally {
                this.checkDelayed();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        final MainActivity context = this;

        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        setContentView(R.layout.activity_main);

        joystickFBView = (JoystickView) findViewById(R.id.joystickfb);
        joystickFBView.setOnMoveListener(new JoystickView.OnMoveListener() {

            @Override
            public void onMove(int angle, int strength)
            {
                LogUtil.Log_i(TAG, "onJoystickFBMove angle=[%d] strength=[%d]",angle, strength);
                fbOrderSpeed = (angle == 270 ? -strength : strength);
                lastFBOrderDate = LocalDateTime.now();
                onThrottleMove();
            }
        });

        joystickLRView = (JoystickView) findViewById(R.id.joystickrl);
        joystickLRView.setOnMoveListener(new JoystickView.OnMoveListener() {

            @Override
            public void onMove(int angle, int strength)
            {
                LogUtil.Log_i(TAG, "onJoystickLRMove angle=[%d] strength=[%d]",angle, strength);
                lrStrength = (angle == 180 ? -strength : strength);
                lastLROrderDate = LocalDateTime.now();
                onThrottleMove();
            }
        });

        statusText = findViewById(R.id.statusText);

        switchMobileControl = findViewById(R.id.switchMobileControl);
        switchMobileControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boskicarServerAPI.setMobileControl(isChecked ? GeneralStatus.Status.ON : GeneralStatus.Status.OFF).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposingObserver<ResponseBody>()
                        {
                            @Override
                            public void onComplete() {
                                updateConfigStatus();
                            }

                            @Override
                            public void onError(Throwable e) {
                                LogUtil.Log_w(TAG, "setMobileControl onError: "+e.getMessage());
                                updateConfigStatus();
                            }
                        });
            }
        });

        switchLights = findViewById(R.id.switchLights);
        switchLights.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boskicarServerAPI.setLights(isChecked ? GeneralStatus.Status.ON : GeneralStatus.Status.OFF).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposingObserver<ResponseBody>()
                        {
                            @Override
                            public void onComplete() {
                                updateConfigStatus();
                            }

                            @Override
                            public void onError(Throwable e) {
                                LogUtil.Log_w(TAG, "setLights onError: "+e.getMessage());
                                updateConfigStatus();
                            }
                        });
            }
        });

        switchFans = findViewById(R.id.switchFans);
        switchFans.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boskicarServerAPI.setFans(isChecked ? GeneralStatus.Status.ON : GeneralStatus.Status.OFF).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposingObserver<ResponseBody>()
                        {
                            @Override
                            public void onComplete() {
                                updateConfigStatus();
                            }

                            @Override
                            public void onError(Throwable e) {
                                LogUtil.Log_w(TAG, "setFans onError: "+e.getMessage());
                                updateConfigStatus();
                            }
                        });
            }
        });

        buttonIPChange = findViewById(R.id.buttonIPChange);
        buttonIPChange.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateServerAPI();
            }
        });

        buttonReboot = findViewById(R.id.buttonReboot);
        buttonReboot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boskicarServerAPI.reboot().subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposingObserver<ResponseBody>()
                        {
                            @Override
                            public void onComplete() {
                                Toast.makeText(context, "Rebooting the car... ", Toast.LENGTH_SHORT);
                            }

                            @Override
                            public void onError(Throwable e) {
                                LogUtil.Log_w(TAG, "reboot onError: "+e.getMessage());
                            }
                        });
            }
        });

        buttonShutdown = findViewById(R.id.buttonShutdown);
        buttonShutdown.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boskicarServerAPI.shutdown().subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposingObserver<ResponseBody>()
                        {
                            @Override
                            public void onComplete() {
                                Toast.makeText(context, "Shutting down the car... ", Toast.LENGTH_SHORT);
                            }

                            @Override
                            public void onError(Throwable e) {
                                LogUtil.Log_w(TAG, "shutdown onError: "+e.getMessage());
                            }
                        });
            }
        });

        updateServerAPI();
        updateConfigStatus();


        handlerThread = new HandlerThread("statusHandler");
        handlerThread.start();
        statusHandler = new StatusHandler(handlerThread.getLooper(), this);
    }

    private void onThrottleMove()
    {
        try
        {
            if(ChronoUnit.MILLIS.between(lastFBOrderDate, LocalDateTime.now()) > 600)
            {
                fbOrderSpeed = 0;
            }

            if(ChronoUnit.MILLIS.between(lastLROrderDate, LocalDateTime.now()) > 600)
            {
                lrStrength = 0;
            }

            LogUtil.Log_i(TAG, "onThrottleMove fbOrderSpeed=[%d] lrStrength=[%d]", fbOrderSpeed, lrStrength);

            boskicarServerAPI.throttle(fbOrderSpeed, lrStrength)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposingObserver<ResponseBody>()
                    {
                        @Override
                        public void onError(Throwable e) {
                            LogUtil.Log_w(TAG, "onJoystickLRMove onError:"+e.getMessage());
                        }
                    });
        }
        catch (Exception e)
        {
            LogUtil.Log_e(TAG, e, "onJoystickLRMove exception: " + e.getMessage());
        }
    }

    private void updateServerAPI()
    {
        try {


            ipText = findViewById(R.id.ipText);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL_PRE + ipText.getText().toString() + BASE_URL_SUF)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient
                            .Builder()
                            .connectTimeout(350, TimeUnit.MILLISECONDS)
                            .readTimeout(350, TimeUnit.MILLISECONDS)
                            .writeTimeout(350, TimeUnit.MILLISECONDS)
                            .build())
                    .build();

            boskicarServerAPI = retrofit.create(BoskicarServerAPI.class);
        }
        catch (Exception e)
        {
            LogUtil.Log_w(TAG, "updateServerAPI exception: "+e.getMessage());
        }
        finally {
            if(statusHandler != null) {
                statusHandler.checkDelayed();
            }
        }
    }

    private void updateConfigStatus()
    {
        this.updateConfigStatus(true);
    }

    protected void updateConfigStatus(boolean complete)
    {
        final Context context = this;

        boskicarServerAPI.getStatus(complete).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposingObserver<GeneralStatus>()
                {
                    @Override
                    public void onNext(GeneralStatus status) {

                        LogUtil.Log_i(TAG, "getStatus onNext complete="+complete);

                        statusText.setText("OK="+LocalDateTime.now().format(DATET_FORMAT));

                        if(complete) {
                            switchMobileControl.setChecked(GeneralStatus.Status.ON.equals(status.getMobileControl()));
                            switchLights.setChecked(GeneralStatus.Status.ON.equals(status.getLights()));
                            switchFans.setChecked(GeneralStatus.Status.ON.equals(status.getFans()));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        statusText.setText("KO="+LocalDateTime.now().format(DATET_FORMAT));

                        LogUtil.Log_w(TAG, "getStatus onError: "+e.getMessage());

                        if(complete)
                        {
                            Toast.makeText(context, "getStatus Error: " + e.getMessage(), Toast.LENGTH_SHORT);
                        }
                    }
                });

        if(statusHandler != null) {
            statusHandler.checkDelayed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(handlerThread != null) {
            if (!this.handlerThread.isAlive()) {
                this.handlerThread.start();
            }
            if(statusHandler != null) {
                this.statusHandler.checkDelayed();
            }
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
        if(handlerThread != null) {
            this.handlerThread.quit();
        }
    }
}
