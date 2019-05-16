## ARouter-------------------------------------------------------------------------------------------
#-keep public class com.alibaba.android.arouter.routes.**{*;}
#-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}
#
## 如果使用了 byType 的方式获取 Service，需添加下面规则，保护接口
#-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider
#
## 如果使用了 单类 注入，即不定义接口实现 IProvider，需添加下面规则，保护实现
#-keep class * implements com.alibaba.android.arouter.facade.template.IProvider
## ARouter-------------------------------------------------------------------------------------------
#
## Android Framework
#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Application
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#-keep public class * extends android.app.backup.BackupAgentHelper
#-keep public class * extends android.preference.Preference
#-keep public class com.android.vending.licensing.ILicensingService
#
#-keepclasseswithmembers class * {
#    public <recycle>(android.content.Context,android.util.AttributeSet);
#}
#
#-keepclasseswithmembers class * {
#    public <recycle>(android.content.Context,android.util.AttributeSet,int);
#}
#
#-keepclassmembers class * implements java.io.Serializable {
#   static final long serialVersionUID;
#   private static final java.io.ObjectStreamField[] serialPersistentFields;
#   !static !transient <fields>;
#   private void writeObject(java.io.ObjectOutputStream);
#   private void readObject(java.io.ObjectInputStream);
#   java.lang.Object writeReplace();
#   java.lang.Object readResolve();
#}
#
#-keep class * extends android.os.Parcelable {
#    public static final android.os.Parcelable$Creator *;
#}
#
#-keepclasseswithmembernames class * {
#    native <methods>;
#}
#
#-keepclasseswithmembers,allowshrinking class * {
#    native <methods>;
#}
#
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#
#-keepclassmembers class **.R$* {
#    public static <fields>;
#}
#
#-keepclassmembers class * extends android.content.Context {
#   public void *(android.view.View);
#   public void *(android.view.MenuItem);
#}
#
#-keepclassmembers class * extends android.app.Activity {
#   public void *(android.view.View);
#}
#
#-dontwarn com.alibaba.fastjson.**
#-keep class com.alibaba.fastjson.**{*;}
#
## 保留所有实体类-------------------------------------------------------------------------------------------
#-keep class qsos.library.base.entity.** {
#    *;
#}


# RETROFIT------------------------------------------------------------------------------------------
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
# RETROFIT------------------------------------------------------------------------------------------