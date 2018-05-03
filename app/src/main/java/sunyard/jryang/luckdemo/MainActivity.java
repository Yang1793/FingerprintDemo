package sunyard.jryang.luckdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.jryang.luckdemo.R;

import sunyard.jryang.fingerprintlib.FingerprintIdentify;
import sunyard.jryang.fingerprintlib.impl.FingerprintIdentifyExceptionListener;
import sunyard.jryang.fingerprintlib.impl.FingerprintIdentifyListener;
import sunyard.jryang.fingerprintlib.widget.FingerprintDialog;
import sunyard.jryang.luckdemo.constants.Constants;

public class MainActivity extends AppCompatActivity {

    private String TAG = "FingerprintIdentify_t";

    private FingerprintDialog dialog;

    private SharedPreferences sharedPreferences;

    private FingerprintIdentify mFingerprintIdentify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFingerprintIdentify = new FingerprintIdentify(this, new FingerprintIdentifyExceptionListener() {
            @Override
            public void onCatchException(Throwable exception) {
                Log.d(TAG, exception.toString());
                //三星jar包 指纹认证成功后，会一直报java.lang.IllegalStateException: No Identify request.
                if (dialog != null)
                    dialog.dismiss();
            }
        });

        sharedPreferences = getSharedPreferences(Constants.SP.SP_NAME, MODE_PRIVATE);

        dialog = new FingerprintDialog(MainActivity.this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initView() {
        ((CheckBox) findViewById(R.id.the_fingerprint_check_box)).setChecked(sharedPreferences.getBoolean(Constants.SP.SP_FINGER, false));
        ((CheckBox) findViewById(R.id.the_fingerprint_check_box)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor edit = sharedPreferences.edit();
                if (isChecked) {
                    edit.putBoolean(Constants.SP.SP_FINGER, isChecked);
                } else {
                    edit.putBoolean(Constants.SP.SP_FINGER, isChecked);
                }
                edit.commit();
            }
        });

        findViewById(R.id.fingerprint_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });

        if (sharedPreferences.getBoolean(Constants.SP.SP_FINGER, false)) {
            test();
        }
    }

    private void test() {
        Log.d(TAG, "isFingerprintEnable:" + mFingerprintIdentify.isFingerprintEnable());
        if (!mFingerprintIdentify.isFingerprintEnable()) {
            Toast.makeText(this, "无指纹传感器 或 没有录入指纹 或 该设备版本低于Android L ", Toast.LENGTH_SHORT).show();
            return;
        }

        dialog.show();
        mFingerprintIdentify.startIdentify(4, new FingerprintIdentifyListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess");
                dialog.fingerprintSuccess();
            }

            @Override
            public void onFailed(boolean isDeviceLocked) {
                Log.d(TAG, "onFailed:" + isDeviceLocked);
                dialog.fingerprintFailed(true);
            }

            @Override
            public void onNotMatch(int identifyTimes) {
                Log.d(TAG, "onNotMatch:" + identifyTimes);
                dialog.fingerprintFailed(false);
            }

            @Override
            public void onStartFailedBydEeviceLocked() {
                Log.d(TAG, "onStartFailedBydEeviceLocked:");
                dialog.fingerprintFailed(true);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFingerprintIdentify.cancelIdentify();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFingerprintIdentify.cancelIdentify();
    }
}
