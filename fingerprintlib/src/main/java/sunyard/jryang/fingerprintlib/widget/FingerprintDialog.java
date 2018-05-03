package sunyard.jryang.fingerprintlib.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jryang.fingerprintlib.R;


/**
 * Author:JR
 * E-mail:jianr.yang@sunyard.com
 * Time: 2018/5/3
 * Description:
 */
public class FingerprintDialog extends AppCompatDialog {

    private Context context;

    private final int CODE_IDENTIFY_SUCCESS = 0x1001;
    private final int CODE_IDENTIFY_FAILED = 0x1002;

    private View contentView;
    private ImageView mIv;
    private TextView mPromptTv;
    private Button mCancelBtn;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CODE_IDENTIFY_SUCCESS:
                    dismiss();
                    break;

                case CODE_IDENTIFY_FAILED:
                    mIv.setImageResource(R.drawable.ic_fingerprint_black_24dp);
                    mPromptTv.setTextColor(context.getResources().getColor(R.color.title_color));
                    mPromptTv.setText(context.getText(R.string.item_prompt));
                    break;
            }
        }
    };

    public FingerprintDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public FingerprintDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        init(context);
    }

    private void init( Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        contentView = inflater.inflate(R.layout.fingerprint_pop_window, null);
        mIv = contentView.findViewById(R.id.item_img);
        mPromptTv = contentView.findViewById(R.id.item_prompt);
        mCancelBtn = contentView.findViewById(R.id.item_cancle);

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        setContentView(contentView);
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = WindowManager.LayoutParams.WRAP_CONTENT;
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(attributes);
        window.setBackgroundDrawable(new ColorDrawable(0));
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void show() {
        super.show();
        mIv.setImageResource(R.drawable.ic_fingerprint_black_24dp);
        mPromptTv.setTextColor(context.getResources().getColor(R.color.title_color));
        mPromptTv.setText(context.getText(R.string.item_prompt));
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void fingerprintSuccess(){
        mIv.setImageResource(R.drawable.ic_check_box_black_24dp);
        mPromptTv.setText(context.getText(R.string.item_prompt_success));
        Message msg = new Message();
        msg.what =  CODE_IDENTIFY_SUCCESS;
        mHandler.sendMessageDelayed(msg, 700);
    }

    public void fingerprintFailed(boolean isLastTimes){
        mIv.setImageResource(R.drawable.ic_btn_failed_24dp);
        mPromptTv.setTextColor(context.getResources().getColor(R.color.cancle_color));
        mPromptTv.setText(context.getText(R.string.item_prompt_failed));
        Message msg = new Message();
        if (isLastTimes){
            msg.what =  CODE_IDENTIFY_SUCCESS;
            mHandler.sendMessageDelayed(msg, 700);
        }else {
            msg.what =  CODE_IDENTIFY_FAILED;
            mHandler.sendMessageDelayed(msg, 700);
        }
    }
}
