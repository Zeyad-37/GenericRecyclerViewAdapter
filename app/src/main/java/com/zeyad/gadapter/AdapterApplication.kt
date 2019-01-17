package com.zeyad.gadapter

import android.app.Application
import android.os.StrictMode
import android.util.Log
import com.zeyad.gadapter.di.myModule
import com.zeyad.gadapter.utils.Constants.URLS.API_BASE_URL
import com.zeyad.rxredux.core.eventbus.RxEventBusFactory
import com.zeyad.usecases.api.DataServiceConfig
import com.zeyad.usecases.api.DataServiceFactory
import com.zeyad.usecases.network.ProgressInterceptor
import com.zeyad.usecases.network.ProgressListener
import okhttp3.CertificatePinner
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.android.startKoin
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author by ZIaDo on 8/5/17.
 */
class AdapterApplication : Application() {

    internal val okHttpBuilder: OkHttpClient.Builder
        get() = OkHttpClient.Builder()
                .addInterceptor(object : ProgressInterceptor(object : ProgressListener {
                    override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
                        RxEventBusFactory.getInstance().send(bytesRead / contentLength)
                    }
                }) {
                    override fun isFileIO(originalResponse: Response): Boolean {
                        return true
                    }
                })
                .addInterceptor(HttpLoggingInterceptor { message -> Log.d("NetworkInfo", message) }
                        .setLevel(if (BuildConfig.DEBUG)
                            HttpLoggingInterceptor.Level.BODY
                        else
                            HttpLoggingInterceptor.Level.NONE))
                .connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                .certificatePinner(CertificatePinner.Builder()
                        .add(API_BASE_URL,
                                "sha256/6wJsqVDF8K19zxfLxV5DGRneLyzso9adVdUN/exDacw")
                        .add(API_BASE_URL,
                                "sha256/k2v657xBsOVe1PQRwOsHsw3bsGT2VzIqz5K+59sNQws=")
                        .add(API_BASE_URL,
                                "sha256/WoiWRyIOVNa9ihaBciRSC7XHjliYS9VwUGOIud4PB18=")
                        .build())
                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS,
                        ConnectionSpec.COMPATIBLE_TLS))

    internal val apiBaseUrl: String
        get() = API_BASE_URL

    override fun onCreate() {
        super.onCreate()
        //        if (LeakCanary.isInAnalyzerProcess(this)) {
        //            return;
        //        }
        //        initializeStrictMode();
        //        LeakCanary.install(this);
        //        Completable.fromAction(() -> {
        //            if (!checkAppTampering(this)) {
        //                throw new IllegalAccessException("App might be tampered with!");
        //            }
        //            initializeFlowUp();
        //            Rollbar.init(this, "c8c8b4cb1d4f4650a77ae1558865ca87", BuildConfig.DEBUG ? "debug" : "production");
        //        }).subscribeOn(Schedulers.io())
        //                   .subscribe(() -> {
        //                   }, Throwable::printStackTrace);
        //        initializeRealm();
        startKoin(this, listOf(myModule))
        DataServiceFactory(DataServiceConfig.Builder(this)
                .baseUrl(apiBaseUrl)
                .okHttpBuilder(okHttpBuilder)
                .build())
    }

    private fun initializeStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                    StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build())
            StrictMode.setVmPolicy(
                    StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build())
        }
    }

    companion object {
        private const val TIME_OUT = 15
    }
}
