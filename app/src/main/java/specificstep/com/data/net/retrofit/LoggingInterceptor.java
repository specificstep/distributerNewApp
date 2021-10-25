package specificstep.com.data.net.retrofit;


import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class LoggingInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request request = original.newBuilder()
                .addHeader("x-request-id", UUID.randomUUID().toString())
                .method(original.method(), original.body()).build();

        long t1 = System.nanoTime();
        Log.d(getClass().getSimpleName(), String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()));

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        Log.d(getClass().getSimpleName(), String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));

        return response;
    }
}