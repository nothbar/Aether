package cn.demo.appq;

import android.app.Application;
import android.content.Context;

import com.github.megatronking.netbare.NetBare;
import com.github.megatronking.netbare.NetBareUtils;
import com.github.megatronking.netbare.ssl.JKS;

import cn.demo.appq.utils.L;
import me.weishu.reflection.Reflection;

public class App extends Application {

    public static final String JSK_ALIAS = "MyVPNSample";


    private static JKS mJKS = null;

    @Override
    public void onCreate() {
        super.onCreate();


        L.i("=======================================================");
        L.i("===============Application.onCreate===============");
        L.i("=======================================================");
        // 创建自签证书
        mJKS = new JKS(this, JSK_ALIAS, JSK_ALIAS.toCharArray(), JSK_ALIAS, JSK_ALIAS,
                JSK_ALIAS, JSK_ALIAS, JSK_ALIAS);
        // 初始化NetBare
        NetBare.get().attachApplication(this, true);
    }

    public static JKS getJKS() {
        return mJKS;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // On android Q, we can't access Java8EngineWrapper with reflect.
        if (NetBareUtils.isAndroidQ()) {
            Reflection.unseal(base);
        }
    }
}
