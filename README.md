# FingerprintDemo
 公司要求APP有指纹登录的功能，在看文档和github上面的项目，写了这个demo。
 需求 Android 手机指纹解锁
 Android 6 之后才有指纹api
 但是很多厂商的5.0手机上也有指纹传感器，比如魅族，360奇酷等
 
 这个demo 优先考虑 Android api 其次 sanmung api 最后 flame api
 
 目前测试 360奇酷 Android 5.0 还是不能指纹解锁（很烦）

权限要求
<uses-permission android:name="android.permission.USE_FINGERPRINT"/>
<uses-permission android:name="com.fingerprints.service.ACCESS_FINGERPRINT_MANAGER"/>
<uses-permission android:name="com.samsung.android.providers.context.permission.WRITE_USE_APP_FEATURE_SURVEY"/>

混淆处理：
# MeiZuFingerprint
-keep class com.fingerprints.service.** { *; }

# SmsungFingerprint
-keep class com.samsung.android.sdk.** { *; }
