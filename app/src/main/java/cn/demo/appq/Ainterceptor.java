package cn.demo.appq;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.github.megatronking.netbare.http.HttpIndexedInterceptor;
import com.github.megatronking.netbare.http.HttpInterceptor;
import com.github.megatronking.netbare.http.HttpInterceptorFactory;
import com.github.megatronking.netbare.http.HttpRequest;
import com.github.megatronking.netbare.http.HttpRequestChain;
import com.github.megatronking.netbare.http.HttpResponse;
import com.github.megatronking.netbare.http.HttpResponseChain;

import java.io.IOException;
import java.nio.ByteBuffer;

import cn.demo.appq.entity.ReqEntity;
import cn.demo.appq.utils.DBManager;
import cn.demo.appq.utils.IOUtils;

public class Ainterceptor extends HttpIndexedInterceptor {


    private static final String TAG = "Ainterceptor";

    public static HttpInterceptorFactory createFactory(Context context) {
        return new HttpInterceptorFactory() {
            @NonNull
            @Override
            public HttpInterceptor create() {
                return new Ainterceptor();
            }
        };
    }

    private SparseArray<ReqEntity> uid_appname_cache = new SparseArray<>();

    @Override
    protected void intercept(@NonNull HttpRequestChain chain, @NonNull ByteBuffer buffer, int index) throws IOException {
        HttpRequest request = chain.request();
        ReqEntity reqEntity = new ReqEntity(
                null, //id 自增
                request.id(),
                App.getProcessNameByUid(request.uid()),
                request.url(),
                request.host(),
                request.port(),
                index,
                request.ip(),
                request.protocol().toString(),
                request.httpProtocol().toString(),
                request.method().name(),
                request.path(),
                request.isHttps(),
                false,
                request.time(),
                request.uid(),
                buffer.limit(),
                request.streamId(),
                request.requestHeaders().toString(),
                "",
               "",
                IOUtils.byteBuffer2String(buffer));
        DBManager.getInstance().getReqEntityDao().insert(reqEntity);
        chain.process(buffer);
    }


    @Override
    protected void intercept(@NonNull HttpResponseChain chain, @NonNull ByteBuffer buffer, int index) throws IOException {
        HttpResponse response = chain.response();
        ReqEntity reqEntity = new ReqEntity(
                null, //id 自增
                response.id(),
                App.getProcessNameByUid(response.uid()),
                response.url(),
                response.host(),
                response.port(),
                index,
                response.ip(),
                response.protocol().toString(),
                response.httpProtocol().toString(),
                response.method().name(),
                response.path(),
                response.isHttps(),
                true,
                response.time(),
                response.uid(),
                buffer.limit(),
                response.streamId(),
                response.requestHeaders().toString(),
                "",
                "",
                IOUtils.byteBuffer2String(buffer));
        DBManager.getInstance().getReqEntityDao().insert(reqEntity);
        chain.process(buffer);
    }
}
