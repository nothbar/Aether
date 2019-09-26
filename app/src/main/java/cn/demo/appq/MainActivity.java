package cn.demo.appq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.megatronking.netbare.NetBare;
import com.github.megatronking.netbare.NetBareConfig;
import com.github.megatronking.netbare.NetBareListener;
import com.github.megatronking.netbare.http.HttpInterceptorFactory;
import com.github.megatronking.netbare.ssl.JKS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        start();
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
        Log.i("sanbo.main","onServiceStarted");
    }

    @Override
    public void onServiceStopped() {
        Log.i("sanbo.main","停止，即将重启");

        // 启动NetBare服务
        mNetBare.start(NetBareConfig.defaultHttpConfig(App.getJKS(),
                interceptorFactories()));

    }
}
