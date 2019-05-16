##保留行号
#-keepattributes SourceFile,LineNumberTable
##包名不混合大小写
#-dontusemixedcaseclassnames
##不跳过非公共的库的类
#-dontskipnonpubliclibraryclasses
##混淆时记录日志
#-verbose
##关闭预校验
#-dontpreverify
##不优化输入的类文件
#-dontoptimize
##保护注解
#-keepattributes *Annotation*
##保持所有拥有本地方法的类名及本地方法名
#-keepclasseswithmembernames class * {
#    native <methods>;
#}
##保持自定义View的get和set相关方法
#-keepclassmembers public class * extends android.view.View {
#    void set*(***);
#    *** get*();
#}
##保持Activity中View及其子类入参的方法
#-keepclassmembers class * extends android.app.Activity {
#    public void *(android.view.View);
#}
##枚举
#-keepclassmembers enum * {
#    **[] $VALUES;
#    public *;
#}
##R文件的静态成员
#-keepclassmembers class **.R$* {
#    public static <fields>;
#}
#-dontwarn android.support.**
##keep相关注解
#-keep class android.support.annotation.Keep
#-keep @android.support.annotation.Keep class * {*;}
#-keepclasseswithmembers class * {
#    @android.support.annotation.Keep <methods>;
#}
#-keepclasseswithmembers class * {
#    @android.support.annotation.Keep <fields>;
#}
#-keepclasseswithmembers class * {
#    @android.support.annotation.Keep <recycle>(...);
#}
#
##OKHTTP############################################################################################
#-dontwarn com.squareup.okhttp3.**
#-keep class com.squareup.okhttp3.** { *;}
#-dontwarn okio.**
#-dontwarn okio.**
#-dontwarn javax.annotation.Nullable
#-dontwarn javax.annotation.ParametersAreNonnullByDefault
##腾讯TBS############################################################################################
#-keep class com.tencent.smtt.export.external.**{
#    *;
#}
#
#-keep class com.tencent.tbs.video.interfaces.IUserStateChangedListener {
#	*;
#}
#
#-keep class com.tencent.smtt.sdk.CacheManager {
#	public *;
#}
#
#-keep class com.tencent.smtt.sdk.CookieManager {
#	public *;
#}
#
#-keep class com.tencent.smtt.sdk.WebHistoryItem {
#	public *;
#}
#
#-keep class com.tencent.smtt.sdk.WebViewDatabase {
#	public *;
#}
#
#-keep class com.tencent.smtt.sdk.WebBackForwardList {
#	public *;
#}
#
#-keep public class com.tencent.smtt.sdk.WebView {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.WebView$HitTestResult {
#	public static final <fields>;
#	public java.lang.String getExtra();
#	public int getType();
#}
#
#-keep public class com.tencent.smtt.sdk.WebView$WebViewTransport {
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.WebView$PictureListener {
#	public <fields>;
#	public <methods>;
#}
#
#
#-keepattributes InnerClasses
#
#-keep public enum com.tencent.smtt.sdk.WebSettings$** {
#    *;
#}
#
#-keep public enum com.tencent.smtt.sdk.QbSdk$** {
#    *;
#}
#
#-keep public class com.tencent.smtt.sdk.WebSettings {
#    public *;
#}
#
#
#-keepattributes Signature
#-keep public class com.tencent.smtt.sdk.ValueCallback {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.WebViewClient {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.DownloadListener {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.WebChromeClient {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.WebChromeClient$FileChooserParams {
#	public <fields>;
#	public <methods>;
#}
#
#-keep class com.tencent.smtt.sdk.SystemWebChromeClient{
#	public *;
#}
## 1. extension interfaces should be apparent
#-keep public class com.tencent.smtt.export.external.extension.interfaces.* {
#	public protected *;
#}
#
## 2. interfaces should be apparent
#-keep public class com.tencent.smtt.export.external.interfaces.* {
#	public protected *;
#}
#
#-keep public class com.tencent.smtt.sdk.WebViewCallbackClient {
#	public protected *;
#}
#
#-keep public class com.tencent.smtt.sdk.WebStorage$QuotaUpdater {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.WebIconDatabase {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.WebStorage {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.DownloadListener {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.QbSdk {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.QbSdk$PreInitCallback {
#	public <fields>;
#	public <methods>;
#}
#-keep public class com.tencent.smtt.sdk.CookieSyncManager {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.Tbs* {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.utils.LogFileUtils {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.utils.TbsLog {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.utils.TbsLogClient {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.CookieSyncManager {
#	public <fields>;
#	public <methods>;
#}
#
## Added for game demos
#-keep public class com.tencent.smtt.sdk.TBSGamePlayer {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.TBSGamePlayerClient* {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.TBSGamePlayerClientExtension {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.TBSGamePlayerService* {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.utils.Apn {
#	public <fields>;
#	public <methods>;
#}
#-keep class com.tencent.smtt.** {
#	*;
#}
## end
#
#
#-keep public class com.tencent.smtt.export.external.extension.proxy.ProxyWebViewClientExtension {
#	public <fields>;
#	public <methods>;
#}
#
#-keep class MTT.ThirdAppInfoNew {
#	*;
#}
#
#-keep class com.tencent.mtt.MttTraceEvent {
#	*;
#}
#
## Game related
#-keep public class com.tencent.smtt.gamesdk.* {
#	public protected *;
#}
#
#-keep public class com.tencent.smtt.sdk.TBSGameBooter {
#        public <fields>;
#        public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.TBSGameBaseActivity {
#	public protected *;
#}
#
#-keep public class com.tencent.smtt.sdk.TBSGameBaseActivityProxy {
#	public protected *;
#}
#
#-keep public class com.tencent.smtt.gamesdk.internal.TBSGameServiceClient {
#	public *;
#}
##腾讯TBS############################################################################################
