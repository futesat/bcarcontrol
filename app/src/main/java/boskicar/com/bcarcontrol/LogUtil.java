package boskicar.com.bcarcontrol;

import android.os.Build;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class LogUtil
{
    // Static singleton variables
    private static LogUtil INSTANCE;

    public static LogUtil getInstance() {
        if (INSTANCE == null)
            INSTANCE = new LogUtil();
        return INSTANCE;
    }

    public static void Log_v(String tag, String msg, Object... varArgs) {
        try {
        if (BuildConfig.DEBUG) Log.v(StringUtils.abbreviate(tag, 23), String.format(Locale.getDefault(), msg, (Object[]) varArgs));
        } catch (Exception ignore) {
        }
    }

    public static void Log_d(String tag, String msg, Object... varArgs) {
    try {
        if (BuildConfig.DEBUG) Log.d(StringUtils.abbreviate(tag, 23), String.format(Locale.getDefault(), msg, (Object[]) varArgs));
    } catch (Exception ignore) {}
    }

    public static void Log_a(String tag, String msg, Object... varArgs) {
        try {
            Log.println(Log.ASSERT, StringUtils.abbreviate(tag, 23), String.format(Locale.getDefault(), msg, (Object[]) varArgs));
        } catch (Exception a) {
            String e=a.getMessage();
        }
    }

    public static void Log_i(String tag, String msg, Object... varArgs) {
    try {
        Log.i(StringUtils.abbreviate(tag, 23), String.format(Locale.getDefault(), msg, (Object[]) varArgs));
    } catch (Exception ignore) {}
    }

    public static void Log_w(String tag, String msg, Object... varArgs) {
    try {
        String message = String.format(Locale.getDefault(), msg, varArgs);
        Log.w(StringUtils.abbreviate(tag, 23), message);
        // TrackHelper.track().exception(new Exception("Warning")).description(message).with(App.getInstance().getPiwikTracker());
    } catch (Exception ignore) {}
    }

    public static void Log_e(String tag, String msg, Object... varArgs) {
    try {
        String message = String.format(Locale.getDefault(), msg, varArgs);
        Log.e(StringUtils.abbreviate(tag, 23), message);
        // TrackHelper.track().exception(new Exception("Error")).description(message).with(App.getInstance().getPiwikTracker());
    } catch (Exception ignore) {}
    }

    public static void Log_e(String tag, Throwable e, String msg, Object... varArgs) {
        try {
            String message = String.format(Locale.getDefault(), msg, varArgs);
            Log.e(StringUtils.abbreviate(tag, 23), message, e);
            // TrackHelper.track().exception(Log_extended).description(message).with(App.getInstance().getPiwikTracker());
        } catch (Exception ignore) {}
    }

    public static void Log_wtf(String tag, String msg, Object... varArgs) {
        try {
        Log.wtf(StringUtils.abbreviate(tag, 23), String.format(Locale.getDefault(), msg, (Object[]) varArgs));
        } catch (Exception ignore) {}
    }

    public void Log_extended(String tag, Throwable e, String msg, Object... varArgs) {
        try {
            String extra;
            if (BuildConfig.DEBUG) {
                String fullClassName = Thread.currentThread().getStackTrace()[2].getClassName();
                String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
                String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
                int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();

                extra = String.format(Locale.getDefault(), "%s.%s():%d ", className, methodName, lineNumber);
            } else {
                extra = "";
            }

            String summary_message = String.format(Locale.getDefault(), "%s SDK%d", e.getClass().getSimpleName(), Build.VERSION.SDK_INT);
            String full_message = extra + String.format(Locale.getDefault(), msg, varArgs);
            Log.e(StringUtils.abbreviate(tag, 23), full_message);

        } catch (Exception ignore) {}
    }
}
