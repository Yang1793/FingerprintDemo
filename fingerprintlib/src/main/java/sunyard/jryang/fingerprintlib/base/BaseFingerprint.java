package sunyard.jryang.fingerprintlib.base;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import sunyard.jryang.fingerprintlib.impl.FingerprintIdentifyExceptionListener;
import sunyard.jryang.fingerprintlib.impl.FingerprintIdentifyListener;

/**
 * Author:JR
 * E-mail:jianr.yang@sunyard.com
 * Time: 2018/5/2
 * Description:指纹识别基础类
 */
public abstract class BaseFingerprint {

    protected Context mCtx;

    private Handler mHandler;
    private FingerprintIdentifyListener mIdentifyListener;
    private FingerprintIdentifyExceptionListener mIdentifyExceptionListener;

    //失败的次数
    private int mNumOfFailures = 0;

    //最高验证次数
    private int mMaxIdentifyTimes = 3;

    //该设备是否拥有指纹硬件传感器
    private boolean mIsHardwareDetected = false;

    //该设备上是否录入过指纹
    private boolean mIsRegisteredFingerprint = false;

    //是否开始验证
    private boolean calledStart = false;

    //是否取消验证
    private boolean isCanceled = false;

    public BaseFingerprint(Context mCtx, FingerprintIdentifyExceptionListener mIdentifyExceptionListener) {
        this.mCtx = mCtx;
        this.mIdentifyExceptionListener = mIdentifyExceptionListener;
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 开始验证
     * @param maxTimes 验证次数
     * @param mIdentifyListener 验证监听
     */
    public void startIdentify(int maxTimes, FingerprintIdentifyListener mIdentifyListener){
        this.mIdentifyListener = mIdentifyListener;
        mNumOfFailures = 0;
        mMaxIdentifyTimes = maxTimes;
        calledStart = true;
        isCanceled = false;
        doIdentify();
    }

    /**
     * 重新验证
     */
    public void resumeIdentify(){
        if (calledStart && mIdentifyListener != null && mNumOfFailures < mMaxIdentifyTimes) {
            isCanceled = false;
            doIdentify();
        }
    }

    /**
     * 取消验证
     */
    public void cancelIdentify(){
        isCanceled = true;
        doCancelIdentify();
    }

    /**
     * 做验证操作
     */
    protected abstract void doIdentify();

    /**
     * 做取消验证操作
     */
    protected abstract void doCancelIdentify();

    protected void onSuccess(){
        if (isCanceled)
            return;
        mNumOfFailures = mMaxIdentifyTimes;

        if (mIdentifyListener != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mIdentifyListener.onSuccess();
                }
            });
        }
        cancelIdentify();
    }

    protected void onNotMatch(){
        if (isCanceled)
            return;

        if (++mNumOfFailures < mMaxIdentifyTimes) {
            if (mIdentifyListener != null) {
                final int temp = mMaxIdentifyTimes - mNumOfFailures;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mIdentifyListener.onNotMatch(temp);
                    }
                });
            }

            if (needToCallDoIdentifyAgainAfterNotMatch()) {
                doIdentify();
            }
            return;
        }

        onFailed(false);
    }

    protected void onFailed(final boolean isDevicesLocked){

        if (isCanceled)
            return;

        final boolean isStarFailedByDevicesLocked = isDevicesLocked && mNumOfFailures == 0;

        mNumOfFailures = mMaxIdentifyTimes;

        if (mIdentifyListener != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isStarFailedByDevicesLocked)
                        mIdentifyListener.onStartFailedBydEeviceLocked();
                    else
                        mIdentifyListener.onFailed(isDevicesLocked);
                }
            });
        }
    }

    protected void onCatchException(Throwable e){
        if (mIdentifyExceptionListener != null && e != null) {
            mIdentifyExceptionListener.onCatchException(e);
        }
    }

    public boolean isEnable(){
        return mIsHardwareDetected && mIsRegisteredFingerprint;
    }

    public boolean ismIsHardwareDetected() {
        return mIsHardwareDetected;
    }

    public void setmIsHardwareDetected(boolean mIsHardwareDetected) {
        this.mIsHardwareDetected = mIsHardwareDetected;
    }

    public boolean ismIsRegisteredFingerprint() {
        return mIsRegisteredFingerprint;
    }

    public void setmIsRegisteredFingerprint(boolean mIsRegisteredFingerprint) {
        this.mIsRegisteredFingerprint = mIsRegisteredFingerprint;
    }

    protected void runOnUiThread(Runnable runnable){
        mHandler.post(runnable);
    }

    protected boolean needToCallDoIdentifyAgainAfterNotMatch() {
        return true;
    }
}
