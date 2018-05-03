package sunyard.jryang.fingerprintlib.impl;

/**
 * Author:JR
 * E-mail:jianr.yang@sunyard.com
 * Time: 2018/5/2
 * Description:指纹识别监听
 */
public interface FingerprintIdentifyListener {

    void onSuccess();

    void onFailed(boolean isDeviceLocked);

    void onNotMatch(int identifyTimes);

    void onStartFailedBydEeviceLocked();
}
