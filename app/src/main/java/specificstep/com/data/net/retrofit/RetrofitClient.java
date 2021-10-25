package specificstep.com.data.net.retrofit;


import androidx.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.data.source.local.Pref;

@Singleton
public class RetrofitClient {

    //public static String ROOT_URL = "http://192.168.30.117:8026/webservices/"; //zulan recharge url
    public static String ROOT_URL = "http://fast.recharge.website/webservices/"; //zulan recharge url

    private static final int REQUEST_TIME_OUT = 2 * 60;
    private static final Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(ROOT_URL)
                    .addConverterFactory(ScalarsConverterFactory.create());
    private Pref pref;

    @Inject
    public RetrofitClient(Pref pref) {
        this.pref = pref;
    }

    private static OkHttpClient.Builder getOkHttpClientBuilder() {
        return new OkHttpClient.Builder()
                .connectTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .readTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS);

    }

    public Request interceptRequest(Request request, String params)
            throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Sink sink = Okio.sink(baos);
        BufferedSink bufferedSink = Okio.buffer(sink);

        /**
         * Write old params
         * */
        request.body().writeTo(bufferedSink);


        /**
         * write to buffer additional params
         * */
        bufferedSink.writeString(params, Charset.defaultCharset());

        RequestBody newRequestBody = RequestBody.create(
                request.body().contentType(),
                bufferedSink.buffer().readUtf8()
        );

        return request.newBuilder().post(newRequestBody).build();
    }

    public RechargeService createService() {
        OkHttpClient.Builder httpClient = getOkHttpClientBuilder();

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
                StringBuilder requestStringBuilder = new StringBuilder();
                requestStringBuilder
                        .append("&mac_address=").append(FirebaseInstanceId.getInstance().getToken())
                        .append("&app=").append(Constants.APP_VERSION);

                if(pref.contains(Pref.KEY_USER_NAME)) {
                    requestStringBuilder.append("&username=").append(pref.getValue(Pref.KEY_USER_NAME, ""));
                }
                if(pref.contains(Pref.KEY_OPT_CODE)) {
                    requestStringBuilder.append("&otp_code=").append(pref.getValue(Pref.KEY_OPT_CODE, ""));
                }
                Request request = interceptRequest(original, requestStringBuilder.toString());
                return chain.proceed(request);
            }
        });
        httpClient.addInterceptor(new LoggingInterceptor());
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(httpLoggingInterceptor);

        OkHttpClient client = httpClient.build();

        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(RechargeService.class);
    }
}