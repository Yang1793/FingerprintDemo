package sunyard.jryang.fingerprintlib.fingerprint;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.fingerprints.service.FingerprintManager;

import sunyard.jryang.fingerprintlib.base.BaseFingerprint;
import sunyard.jryang.fingerprintlib.impl.FingerprintIdentifyExceptionListener;

/**
 * Author:JR
 * E-mail:jianr.yang@sunyard.com
 * Time: 2018/5/2
 * Description:魅族手机的指纹识别  flyme开放平台地址：<code>http://open-wiki.flyme.cn/index.php?title=指纹识别API</code>
 */
public class FlymeFingerprint extends BaseFingerprint {

    private FingerprintManager mMeiZuFingerprintManager;

    public FlymeFingerprint(Context mCtx, FingerprintIdentifyExceptionListener mIdentifyExceptionListener) {
        super(mCtx, mIdentifyExceptionListener);

        try {
            mMeiZuFingerprintManager = FingerprintManager.open();
            if (mMeiZuFingerprintManager != null) {
                setmIsHardwareDetected(isMeiZuDevice(Build.MANUFACTURER));
                int[] fingerprintIds = mMeiZuFingerprintManager.getIds();
                setmIsRegisteredFingerprint(fingerprintIds != null && fingerprintIds.length > 0);
            }
        } catch (Throwable e) {
            onCatchException(e);
        }
        releaseMBack();
    }

    @Override
    protected void doIdentify() {
        try {
            mMeiZuFingerprintManager = FingerprintManager.open();
            mMeiZuFingerprintManager.startIdentify(new FingerprintManager.IdentifyCallback() {
                @Override
                public void onIdentified(int i, boolean b) {
                    onSuccess();
                }

                @Override
                public void onNoMatch() {
                    onNotMatch();
                }
            }, mMeiZuFingerprintManager.getIds());
        } catch (Throwable e) {
            onCatchException(e);
            onFailed(false);
        }
    }

    @Override
    protected void doCancelIdentify() {
        releaseMBack();
    }

    private void releaseMBack() {
        try {
            if (mMeiZuFingerprintManager != null) {
                mMeiZuFingerprintManager.release();
            }
        } catch (Throwable e) {
            onCatchException(e);
        }
    }


    private boolean isMeiZuDevice(String manufacturer) {
        return !TextUtils.isEmpty(manufacturer) && manufacturer.toUpperCase().contains("MEIZU");
    }
}
