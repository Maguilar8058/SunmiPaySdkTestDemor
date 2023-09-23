package com.sm.sdk.demo;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.DisplayMetrics;

import com.sm.sdk.demo.utils.LogUtil;
import com.sm.sdk.demo.utils.Utility;
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2;
import com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2;
import com.sunmi.pay.hardware.aidlv2.print.PrinterOptV2;
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2;
import com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2;
import com.sunmi.pay.hardware.aidlv2.system.BasicOptV2;
import com.sunmi.pay.hardware.aidlv2.tax.TaxOptV2;
import com.sunmi.pay.hardware.aidlv2.test.TestOptV2;

import com.sunmi.scanner.IScanInterface;

import java.util.Locale;

import sunmi.paylib.SunmiPayKernel;

public class MyApplication extends Application {
    public static MyApplication app;

    public BasicOptV2 basicOptV2;           // 获取基础操作模块
    public ReadCardOptV2 readCardOptV2;     // 获取读卡模块
    public PinPadOptV2 pinPadOptV2;         // 获取PinPad操作模块
    public SecurityOptV2 securityOptV2;     // 获取安全操作模块
    public EMVOptV2 emvOptV2;               // 获取EMV操作模块
    public TaxOptV2 taxOptV2;               // 获取税控操作模块
    public ETCOptV2 etcOptV2;               // 获取ETC操作模块
    public PrinterOptV2 printerOptV2;       // 获取打印操作模块
    public TestOptV2 testOptV2;             // 获取测试操作模块

    public IScanInterface scanInterface;
    private boolean connectPaySDK;//是否已连接PaySDK

    public static void initLocaleLanguage() {
        Resources resources = app.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        int showLanguage = CacheHelper.getCurrentLanguage();
        if (showLanguage == Constant.LANGUAGE_AUTO) {
            LogUtil.e(Constant.TAG, config.locale.getCountry() + "---这是系统语言");
            config.locale = Resources.getSystem().getConfiguration().locale;
        } else if (showLanguage == Constant.LANGUAGE_ZH_CN) {
            LogUtil.e(Constant.TAG, "这是中文");
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else if (showLanguage == Constant.LANGUAGE_EN_US) {
            LogUtil.e(Constant.TAG, "这是英文");
            config.locale = Locale.ENGLISH;
        } else if (showLanguage == Constant.LANGUAGE_JA_JP) {
            LogUtil.e(Constant.TAG, "这是日文");
            config.locale = Locale.JAPAN;
        }
        resources.updateConfiguration(config, dm);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtil.e(Constant.TAG, "onConfigurationChanged");
    }

    public boolean isConnectPaySDK() {
        return connectPaySDK;
    }

    /** bind PaySDK service */
    public void bindPaySDKService() {
        final SunmiPayKernel payKernel = SunmiPayKernel.getInstance();
        payKernel.initPaySDK(this, new SunmiPayKernel.ConnectCallback() {
            @Override
            public void onConnectPaySDK() {
                LogUtil.e(Constant.TAG, "onConnectPaySDK...");
                emvOptV2 = payKernel.mEMVOptV2;
                basicOptV2 = payKernel.mBasicOptV2;
                pinPadOptV2 = payKernel.mPinPadOptV2;
                readCardOptV2 = payKernel.mReadCardOptV2;
                securityOptV2 = payKernel.mSecurityOptV2;
                taxOptV2 = payKernel.mTaxOptV2;
                etcOptV2 = payKernel.mETCOptV2;
                printerOptV2 = payKernel.mPrinterOptV2;
                testOptV2 = payKernel.mTestOptV2;
                connectPaySDK = true;
            }

            @Override
            public void onDisconnectPaySDK() {
                LogUtil.e(Constant.TAG, "onDisconnectPaySDK...");
                connectPaySDK = false;
                emvOptV2 = null;
                basicOptV2 = null;
                pinPadOptV2 = null;
                readCardOptV2 = null;
                securityOptV2 = null;
                taxOptV2 = null;
                etcOptV2 = null;
                printerOptV2 = null;
                Utility.showToast(R.string.connect_fail);
            }
        });
    }


}
