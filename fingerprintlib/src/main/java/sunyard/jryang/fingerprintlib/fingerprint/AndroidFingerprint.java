package sunyard.jryang.fingerprintlib.fingerprint;

import android.content.Context;
import android.os.Build;
import android.support.v4.os.CancellationSignal;

import sunyard.jryang.fingerprintlib.aosp.FingerprintManagerCompat;
import sunyard.jryang.fingerprintlib.base.BaseFingerprint;
import sunyard.jryang.fingerprintlib.impl.FingerprintIdentifyExceptionListener;

/**
 * Author:JR
 * E-mail:jianr.yang@sunyard.com
 * Time: 2018/5/2
 * Description:Google自带api指纹解锁
 */
public class AndroidFingerprint extends BaseFingerprint {

    private CancellationSignal mCancellationSignal;
    //Android指纹解锁 Android Support Library v4
    private FingerprintManagerCompat mFingerprintManagerCompat;

    public AndroidFingerprint(Context mCtx, FingerprintIdentifyExceptionListener mIdentifyExceptionListener) {
        super(mCtx, mIdentifyExceptionListener);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;
        try {
            mFingerprintManagerCompat = FingerprintManagerCompat.from(mCtx);
            setmIsHardwareDetected(mFingerprintManagerCompat.isHardwareDetected());
            setmIsHardwareDetected(mFingerprintManagerCompat.hasEnrolledFingerprints());
        }catch (Exception e){
            mIdentifyExceptionListener.onCatchException(e);
        }
    }

    @Override
    protected void doIdentify() {

        try{
            mCancellationSignal = new CancellationSignal();
            mFingerprintManagerCompat.authenticate(null, 0, mCancellationSignal, new FingerprintManagerCompat.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errMsgId, CharSequence errString) {
                    super.onAuthenticationError(errMsgId, errString);
                    //api 中 7 表示 ErrorLockout
                    onFailed(errMsgId == 7);
                }

                @Override
                public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    onSuccess();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    onNotMatch();
                }
            },null);
        }catch (Exception e){
            onCatchException(e);
            onFailed(false);
        }
    }

    @Override
    protected void doCancelIdentify() {
        try {
            if (mCancellationSignal != null) {
                mCancellationSignal.cancel();
            }
        } catch (Throwable e) {
            onCatchException(e);
        }
    }

    @Override
    protected boolean needToCallDoIdentifyAgainAfterNotMatch() {
        return false;
    }
}
