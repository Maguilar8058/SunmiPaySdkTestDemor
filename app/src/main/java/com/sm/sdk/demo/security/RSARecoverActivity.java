package com.sm.sdk.demo.security;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sm.sdk.demo.BaseAppCompatActivity;
import com.sm.sdk.demo.MyApplication;
import com.sm.sdk.demo.R;
import com.sm.sdk.demo.utils.ByteUtil;
import com.sm.sdk.demo.utils.LogUtil;
import com.sunmi.pay.hardware.aidl.AidlConstants.Security;

import java.util.Arrays;

public class RSARecoverActivity extends BaseAppCompatActivity {
    private EditText mEditData;
    private EditText mEditKeyIndex;
    private TextView mTvInfo;
    private int paddingMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsa_recover);
        initToolbarBringBack(R.string.hsm_rsa_recover);
        initView();
    }

    private void initView() {
        RadioGroup rgPaddingMode = findViewById(R.id.rg_rsa_padding_mode);
        rgPaddingMode.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_no_padding:
                    paddingMode = Security.NOTHING_PADDING;
                    break;
                case R.id.rb_pkcs1_padding:
                    paddingMode = Security.PKCS1_PADDING;
                    break;
                case R.id.rb_pkcs7_padding:
                    paddingMode = Security.PKCS7_PADDING;
                    break;
                case R.id.rb_pkcs5_padding:
                    paddingMode = Security.PKCS5_PADDING;
                    break;
                case R.id.rb_pkcs1_oaep_padding:
                    paddingMode = Security.PKCS1_OAEP_PADDING;
                    break;
                case R.id.rb_pkcs1_v1_5_sha512_padding:
                    paddingMode = Security.PKCS1_V1_5_SHA512;
                    break;
            }
        });
        rgPaddingMode.check(R.id.rb_no_padding);
        mEditData = findViewById(R.id.source_data);
        mEditKeyIndex = findViewById(R.id.key_index);
        mTvInfo = findViewById(R.id.tv_info);
        findViewById(R.id.mb_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mb_ok:
                rsaRecover();
                break;
        }
    }

    private void rsaRecover() {
        try {
            String dataStr = mEditData.getText().toString();
            String keyIndexStr = mEditKeyIndex.getText().toString();

            int keyIndex;
            try {
                keyIndex = Integer.parseInt(keyIndexStr);
                if (keyIndex < 0 || keyIndex > 19) {
                    showToast(R.string.security_rsa_key_hint);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast(R.string.security_rsa_key_hint);
                return;
            }
            if (dataStr.trim().length() == 0) {
                showToast(R.string.security_source_data_hint);
                return;
            }
            if (dataStr.length() % 2 != 0) {
                showToast(R.string.security_source_data_hint);
                return;
            }
            byte[] dataIn = ByteUtil.hexStr2Bytes(dataStr);
            byte[] dataOut = new byte[2048];
            addStartTimeWithClear("rsaEncryptOrDecryptData()");
            int len = MyApplication.app.securityOptV2.rsaEncryptOrDecryptData(keyIndex, paddingMode, dataIn, dataOut); //sp签名
            addEndTime("rsaEncryptOrDecryptData()");
            showSpendTime();
            if (len < 0) {
                String msg = "rsaEncryptOrDecryptData() failed, code:" + len;
                showToast(msg);
                LogUtil.e(TAG, msg);
            } else {
                byte[] valid = Arrays.copyOf(dataOut, len);
                String hexStr = ByteUtil.bytes2HexStr(valid);
                LogUtil.e(TAG, "rsaRecover() output:" + hexStr);
                mTvInfo.setText(hexStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}