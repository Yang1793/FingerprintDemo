package sunyard.jryang.fingerprintlib.impl;

/**
 * Author:JR
 * E-mail:jianr.yang@sunyard.com
 * Time: 2018/5/2
 * Description:指纹识别错误监听
 */
public interface FingerprintIdentifyExceptionListener {

    void onCatchException(Throwable exception);
}
