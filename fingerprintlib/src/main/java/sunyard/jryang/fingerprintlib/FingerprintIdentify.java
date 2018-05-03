package sunyard.jryang.fingerprintlib;

import android.content.Context;

import sunyard.jryang.fingerprintlib.base.BaseFingerprint;
import sunyard.jryang.fingerprintlib.fingerprint.AndroidFingerprint;
import sunyard.jryang.fingerprintlib.fingerprint.FlymeFingerprint;
import sunyard.jryang.fingerprintlib.fingerprint.SamsungFingerprint;
import sunyard.jryang.fingerprintlib.impl.FingerprintIdentifyExceptionListener;
import sunyard.jryang.fingerprintlib.impl.FingerprintIdentifyListener;


public class FingerprintIdentify {
    private BaseFingerprint mFingerprint;
    private BaseFingerprint mSubFingerprint;

    public FingerprintIdentify(Context context) {
        this(context, null);
    }

    public FingerprintIdentify(Context context, FingerprintIdentifyExceptionListener exceptionListener) {
        AndroidFingerprint androidFingerprint = new AndroidFingerprint(context, exceptionListener);
        if (androidFingerprint.ismIsHardwareDetected()) {
            mSubFingerprint = androidFingerprint;
            if (androidFingerprint.ismIsRegisteredFingerprint()) {
                mFingerprint = androidFingerprint;
                return;
            }
        }

        SamsungFingerprint samsungFingerprint = new SamsungFingerprint(context, exceptionListener);
        if (samsungFingerprint.ismIsHardwareDetected()) {
            mSubFingerprint = samsungFingerprint;
            if (samsungFingerprint.ismIsRegisteredFingerprint()) {
                mFingerprint = samsungFingerprint;
                return;
            }
        }

        FlymeFingerprint meiZuFingerprint = new FlymeFingerprint(context, exceptionListener);
        if (meiZuFingerprint.ismIsHardwareDetected()) {
            mSubFingerprint = meiZuFingerprint;
            if (meiZuFingerprint.ismIsRegisteredFingerprint()) {
                mFingerprint = meiZuFingerprint;
                return;
            }
        }
    }

    // DO
    public void startIdentify(int maxAvailableTimes, FingerprintIdentifyListener listener) {
        if (!isFingerprintEnable()) {
            return;
        }

        mFingerprint.startIdentify(maxAvailableTimes, listener);
    }

    public void cancelIdentify() {
        if (mFingerprint != null) {
            mFingerprint.cancelIdentify();
        }
    }

    public void resumeIdentify() {
        if (!isFingerprintEnable()) {
            return;
        }

        mFingerprint.resumeIdentify();
    }

    // GET & SET
    public boolean isFingerprintEnable() {
        return mFingerprint != null && mFingerprint.isEnable();
    }

    public boolean isHardwareEnable() {
        return isFingerprintEnable() || (mSubFingerprint != null && mSubFingerprint.ismIsHardwareDetected());
    }

    public boolean isRegisteredFingerprint() {
        return isFingerprintEnable() || (mSubFingerprint != null && mSubFingerprint.ismIsRegisteredFingerprint());
    }
}