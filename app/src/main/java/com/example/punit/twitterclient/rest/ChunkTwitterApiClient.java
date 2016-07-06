package com.example.punit.twitterclient.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twitter.sdk.android.core.AuthenticatedClient;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.models.BindingValues;
import com.twitter.sdk.android.core.models.BindingValuesAdapter;
import com.twitter.sdk.android.core.models.SafeListAdapter;
import com.twitter.sdk.android.core.models.SafeMapAdapter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import javax.net.ssl.SSLSocketFactory;

import retrofit.RestAdapter;
import retrofit.android.MainThreadExecutor;
import retrofit.converter.GsonConverter;

public class ChunkTwitterApiClient {

    private static final String UPLOAD_ENDPOINT = "https://upload.twitter.com";
    final ConcurrentHashMap<Class, Object> services;
    final RestAdapter uploadAdapter;

    ChunkTwitterApiClient(TwitterAuthConfig authConfig,
                     Session session,
                     SSLSocketFactory sslSocketFactory, ExecutorService executorService) {

        if (session == null) {
            throw new IllegalArgumentException("Session must not be null.");
        }

        this.services = new ConcurrentHashMap<>();

        final Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new SafeListAdapter())
                .registerTypeAdapterFactory(new SafeMapAdapter())
                .registerTypeAdapter(BindingValues.class, new BindingValuesAdapter())
                .create();

        uploadAdapter = new RestAdapter.Builder()
                .setClient(new AuthenticatedClient(authConfig, session, sslSocketFactory))
                .setEndpoint(UPLOAD_ENDPOINT)
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setExecutors(executorService, new MainThreadExecutor())
                .build();
    }

    /**
     * Must be instantiated after {@link com.twitter.sdk.android.core.TwitterCore} has been
     * initialized via {@link io.fabric.sdk.android.Fabric#with(android.content.Context, io.fabric.sdk.android.Kit[])}.
     *
     * @param session Session to be used to create the API calls.
     *
     * @throws java.lang.IllegalArgumentException if TwitterSession argument is null
     */
    public ChunkTwitterApiClient(Session session) {
        this(TwitterCore.getInstance().getAuthConfig(), session,
                TwitterCore.getInstance().getSSLSocketFactory(),
                TwitterCore.getInstance().getFabric().getExecutorService());
    }

    public CustomService getCustomService(){
        return getService(CustomService.class);
    }
    /**
     * Converts Retrofit style interface into instance for API access
     *
     * @param cls Retrofit style interface
     * @return instance of cls
     */
    @SuppressWarnings("unchecked")
    protected <T> T getService(Class<T> cls) {
        return getAdapterService(uploadAdapter, cls);
    }

    /**
     * Converts a Retrofit style interfaces into an instance using the given RestAdapter.
     * @param adapter the retrofit RestAdapter to use to generate a service instance
     * @param cls Retrofit style service interface
     * @return instance of cls
     */
    @SuppressWarnings("unchecked")
    protected <T> T getAdapterService(RestAdapter adapter, Class<T> cls) {
        if (!services.contains(cls)) {
            services.putIfAbsent(cls, adapter.create(cls));
        }
        return (T) services.get(cls);
    }
}
