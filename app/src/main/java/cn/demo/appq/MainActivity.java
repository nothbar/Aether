package cn.demo.appq;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import com.github.megatronking.netbare.NetBare;
import com.github.megatronking.netbare.NetBareConfig;
import com.github.megatronking.netbare.NetBareListener;
import com.github.megatronking.netbare.http.HttpInterceptorFactory;
import com.github.megatronking.netbare.ssl.JKS;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.demo.appq.utils.L;

public class MainActivity extends Activity implements NetBareListener {

    private NetBare mNetBare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNetBare = NetBare.get();
        // 监听NetBare服务的启动和停止
        mNetBare.registerNetBareListener(this);
    }


    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnStart:
                start();
                break;
            case R.id.btnTT:
                test();
                break;
            default:
                start();
                break;
        }

    }

    private void test() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String simOperator = tm.getSimOperator();
        L.i("simOperator: " + simOperator);
        if (checkPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            //获取imsi(MCC+MNC+MIN)
            String imsi = tm.getSubscriberId();
            L.i("imsi: " + imsi);
        }

//        注册网络的MCC + MNC
        String networkOperator = tm.getNetworkOperator();
        L.i("networkOperator: " + networkOperator);

        String networkOperatorName = tm.getNetworkOperatorName();
        L.i("networkOperatorName: " + networkOperatorName);

        Configuration c = getResources().getConfiguration();
        L.i("Configuration  mcc: " + c.mcc + "--mnc:" + c.mnc);


    }

    /**
     * 判断权限是否已经授权
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                return rest == PackageManager.PERMISSION_GRANTED;
            } catch (Throwable e) {
                result = false;
            }
        } else {
            result = true;
        }
        return result;
    }

    private void start() {

        if (!mNetBare.isActive()) {
            prepareNetBare();
        }
    }

    private int REQUEST_CODE_PREPARE = 1;

    private void prepareNetBare() {
        // 安装自签证书
        if (!JKS.isInstalled(this, App.JSK_ALIAS)) {
            try {
                JKS.install(this, App.JSK_ALIAS, App.JSK_ALIAS);
            } catch (IOException e) {
                // 安装失败
            }
            return;
        }
        // 配置VPN
        Intent intent = NetBare.get().prepare();
        if (intent != null) {
            startActivityForResult(intent, REQUEST_CODE_PREPARE);
            return;
        }
        // 启动NetBare服务
        mNetBare.start(NetBareConfig.defaultHttpConfig(App.getJKS(),
                interceptorFactories()));
    }

    private List<HttpInterceptorFactory> interceptorFactories() {

        List<HttpInterceptorFactory> hfs = new ArrayList<>();
        hfs.add(Ainterceptor.createFactory(this));
        return hfs;
    }

    @Override
    public void onServiceStarted() {
        Log.i("sanbo.main", "onServiceStarted");
    }

    @Override
    public void onServiceStopped() {
        Log.i("sanbo.main", "停止，即将重启");

        // 启动NetBare服务
        mNetBare.start(NetBareConfig.defaultHttpConfig(App.getJKS(),
                interceptorFactories()));

    }
}
