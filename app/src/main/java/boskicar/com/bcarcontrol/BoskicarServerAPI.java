package boskicar.com.bcarcontrol;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BoskicarServerAPI
{
    @Headers("Content-type: application/json")
    @GET("/status/{complete}")
    Observable<GeneralStatus> getStatus(@Path("complete") boolean complete);

    @Headers("Content-type: application/json")
    @GET("/logs")
    Observable<ResponseBody> logs();

    @Headers("Content-type: application/json")
    @POST("/reboot")
    Observable<ResponseBody> reboot();

    @Headers("Content-type: application/json")
    @POST("/shutdown")
    Observable<ResponseBody> shutdown();

    @Headers("Content-type: application/json")
    @POST("/stop")
    Observable<ResponseBody> stop();

    @Headers("Content-type: application/json")
    @POST("/joystick/{angle}/{strength}")
    Observable<ResponseBody> joystick(@Path("angle") int angle, @Path("strength") int strength);

    @Headers("Content-type: application/json")
    @POST( "/throttle/{fbOrderSpeed}/{lrStrength}")
    Observable<ResponseBody> throttle(@Path("fbOrderSpeed") int fbOrderSpeed, @Path("lrStrength") int lrStrength);

    @Headers("Content-type: application/json")
    @POST("/mobilecontrol/{status}")
    Observable<ResponseBody> setMobileControl(@Path("status") GeneralStatus.Status status);

    @Headers("Content-type: application/json")
    @POST("/lights/{status}")
    Observable<ResponseBody> setLights(@Path("status") GeneralStatus.Status status);

    @Headers("Content-type: application/json")
    @POST("/fans/{status}")
    Observable<ResponseBody> setFans(@Path("status") GeneralStatus.Status status);
}
