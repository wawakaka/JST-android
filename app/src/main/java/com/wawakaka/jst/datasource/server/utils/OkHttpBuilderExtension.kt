package com.wawakaka.jst.datasource.server.utils

import android.os.Build
import com.wawakaka.jst.base.utils.LogUtils
import com.wawakaka.jst.datasource.server.model.Tls12SocketFactory
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import java.security.KeyStore
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


/**
 * Created by elsennovraditya on 3/11/17.
 */
fun OkHttpClient.Builder.enableTlsOnPreLollipop(tlsVersion: String): OkHttpClient.Builder {
    if (Build.VERSION.SDK_INT in Build.VERSION_CODES.JELLY_BEAN..Build.VERSION_CODES.LOLLIPOP) {
        try {
            val sslContext = SSLContext.getInstance(tlsVersion)
            sslContext.init(null, null, null)
            val trustManagers = getDefaultTrustManagers()
            val x509TrusManagers = getX509TrustManager(trustManagers)
            this.sslSocketFactory(Tls12SocketFactory(sslContext.socketFactory), x509TrusManagers)

            val connectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.forJavaName(tlsVersion))
                .build()

            val specs = mutableListOf<ConnectionSpec>()
            specs.add(connectionSpec)
            specs.add(ConnectionSpec.COMPATIBLE_TLS)
            specs.add(ConnectionSpec.CLEARTEXT)

            this.connectionSpecs(specs)
        } catch (e: Exception) {
            LogUtils.error("OkHttpTLSCompat", "Error while setting TLS", e)
        }
    }

    return this
}

private fun getDefaultTrustManagers(): Array<out TrustManager> {
    val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    trustManagerFactory.init(null as KeyStore?)
    return trustManagerFactory.trustManagers
}

internal fun OkHttpClient.Builder.getX509TrustManager(trustManagers: Array<out TrustManager>): X509TrustManager {
    if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
        throw IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers))
    }
    return trustManagers[0] as X509TrustManager
}