package utils;

import soot.ArrayType;
import soot.RefType;

public class Clues {
	/**
	 * Interfacce e classi astratte eventualmente implementate
	 */
	public static final RefType JAVAX_X509_TRUST_MANAGER = RefType.v("javax.net.ssl.X509TrustManager");
	public static final RefType JAVAX_NET_HOSTNAME_VERIFIER = RefType.v("javax.net.ssl.HostnameVerifier");
	public static final RefType APACHE_ABST_HOSTNAME_VERIFIER = RefType.v("org.apache.http.conn.ssl.AbstractVerifier");
	//TODO aggiungere le altre
	public static final RefType JAVA_SECURITY_X509_CERTIFICATE = RefType.v("java.security.cert.X509Certificate");
	public static final ArrayType JAVAX_SECURITY_X509_CERTIFICATE_ARRAY = ArrayType.v(JAVA_SECURITY_X509_CERTIFICATE, 1);
	
	public static final RefType ANDROID_WEBVIEW_CLIENT = RefType.v("android.webkit.WebViewClient");
	public static final RefType ANDROID_WEBVIEV = RefType.v("android.webkit.WebView");
	public static final RefType ANDROID_SSL_ERROR_HANDLER = RefType.v("android.webkit.SslErrorHandler");
	public static final RefType ANDROID_NET_SSL_ERROR = RefType.v("android.net.http.SslError");
	public static final RefType ANDROID_SSL_ERROR_CANCEL = RefType.v("cancel");
	
	/**
	 * Contesto Secure Socket Layer
	 */
	public static final RefType SSL_SESSION = RefType.v("javax.net.ssl.SSLSession");
	public static final RefType SSL_EXCEPTION = RefType.v("javax.net.ssl.SSLException");
	
	public static final RefType JAVA_SECURITY_CERTIFICATE_EXCEPTION = RefType.v("java.security.cert.CertificateException");
	/**
	 * Tipi primitivi di Java
	 */
	public static final RefType STRING = RefType.v("java.lang.String");
	public static final ArrayType STRING_ARRAY = ArrayType.v(STRING, 1);


	private Clues() {}
}
