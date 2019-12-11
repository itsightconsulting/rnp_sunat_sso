package pe.gob.osce.rnp.seg.cfg;

import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


public abstract class SSLClientFactory {

    private static HttpClientBuilder closeableClientBuilder = null;

    public enum HttpClientType{
        HttpClient,
    }


    public static synchronized ClientHttpRequestFactory getClientHttpRequestFactory(HttpClientType httpClientType){

        ClientHttpRequestFactory requestFactory = null;

        SSLContext sslContext = SSLClientFactory.getSSlContext();

        if(null == sslContext){
            return null;
        }

        switch (httpClientType) {

            case HttpClient:
                closeableClientBuilder = HttpClientBuilder.create();

                //Add the SSLContext and trustmanager
                closeableClientBuilder.setSSLContext(getSSlContext());
                //add the hostname verifier
                closeableClientBuilder.setSSLHostnameVerifier(gethostnameVerifier());

                requestFactory = new HttpComponentsClientHttpRequestFactory(closeableClientBuilder.build());

                break;
            default:
                break;
        }

        return requestFactory;

    }


    private static SSLContext getSSlContext(){
        final TrustManager[] trustAllCerts = new TrustManager[]{getTrustManager()};

        SSLContext sslContext = null;
        try {

            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }



        return sslContext;

    }

    private static X509TrustManager getTrustManager(){

        final X509TrustManager trustManager = new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] cArrr = new X509Certificate[0];
                return cArrr;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                // TODO Auto-generated method stub

            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                // TODO Auto-generated method stub

            }
        };

        return trustManager;
    }

    private static HostnameVerifier gethostnameVerifier(){

        HostnameVerifier hostnameVerifier = new HostnameVerifier() {

            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        };

        return hostnameVerifier;

    }

}
