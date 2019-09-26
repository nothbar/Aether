package cn.demo.appq;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.megatronking.netbare.http.HttpIndexedInterceptor;
import com.github.megatronking.netbare.http.HttpInterceptor;
import com.github.megatronking.netbare.http.HttpInterceptorFactory;
import com.github.megatronking.netbare.http.HttpRequestChain;
import com.github.megatronking.netbare.http.HttpResponseChain;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import cn.demo.appq.utils.NN;

public class Ainterceptor extends HttpIndexedInterceptor {

    private String TAG = "sanbo.interceptor";

    public static HttpInterceptorFactory createFactory(Context context) {
        return new HttpInterceptorFactory() {
            @NonNull
            @Override
            public HttpInterceptor create() {
                return new Ainterceptor();
            }
        };
    }

    @Override
    protected void intercept(@NonNull HttpRequestChain chain, @NonNull ByteBuffer buffer, int index) throws IOException {
        try {
            if (index == 0) {
                // 一个请求可能会有多个数据包，故此方法会多次触发，这里只在收到第一个包的时候打印
                Log.i(TAG, "Request URL[" + index + "]: " + chain.request().url());
                Log.i(TAG, "Request requestHeaders[" + index + "]---" + chain.request().requestHeaders().size() + ": " + chain.request().requestHeaders().toString());


//            if (chain.request().requestStreamEnd()) {
//                String body = buffer [chain.request().requestBodyOffset()];
//            }

                byte[] bs = buffer.array();
                int offset = chain.request().requestBodyOffset();
                StringBuilder sb = new StringBuilder();
                for (int i = offset; i < bs.length - 1; i++) {
                    // 能去空
                    sb.append((char)bs[i]);
                }
                Log.i(TAG, "Request PKG[" + index + "]---" + sb.toString().trim());
                if (chain.request().url().contains("arkpaastest.analysys.cn/up")) {
                    Log.e("sanboAA", "url : " + chain.request().url());
                    Log.e("sanboAA", "header : " + chain.request().requestHeaders().size() + ": " + chain.request().requestHeaders().toString());
                    Log.e("sanboAA", "body:" + sb.toString().trim());

                    Map<String, List<String>> ks = chain.request().requestHeaders();
                    List<String> spvs = ks.get("spv");
                    List<String> reqvs = ks.get("reqv");
                    List<String> reqts = ks.get("reqt");
//                    (String spv, String reqv, String reqt, String content)
                    Log.e("sanboAA", "解析:" + NN.parserPaas(spvs.get(0),reqvs.get(0),reqts.get(0), sb.toString().trim())) ;
                }
            }
        } catch (Throwable e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        // 调用process将数据发射给下一个拦截器，否则数据将不会发给服务器
        chain.process(buffer);
    }

    @Override
    protected void intercept(@NonNull HttpResponseChain chain, @NonNull ByteBuffer buffer, int index) throws IOException {
        if (index == 0) {
            // 一个请求可能会有多个数据包，故此方法会多次触发，这里只在收到第一个包的时候打印
            Log.i(TAG, "Response code[" + index + "]: " + chain.response().code());
        }
        // 调用process将数据发射给下一个拦截器，否则数据将不会发给服务器
        chain.process(buffer);
    }
}
