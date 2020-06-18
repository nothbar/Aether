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
import java.util.List;

import cn.demo.appq.entity.ReqEntity;
import cn.demo.appq.greendao.ReqEntityDao;
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
        List<ReqEntity> reqEntities = DBManager.getInstance()
                .getReqEntityDao()
                .queryBuilder()
                .where(ReqEntityDao.Properties.SessionId.eq(request.id()))
                .list();
        if (reqEntities != null && reqEntities.size() > 0) {
            ReqEntity entity = reqEntities.get(0);
            String reqContent = entity.getReqContent();
            if (reqContent == null) {
                entity.setRespContent(IOUtils.byteBuffer2String(buffer.asReadOnlyBuffer()));
            } else {
                entity.setRespContent(reqContent + IOUtils.byteBuffer2String(buffer.asReadOnlyBuffer()));
            }
            entity.setIndex(index);
            DBManager.getInstance().getReqEntityDao().update(entity);
        } else {
            ReqEntity reqEntity = new ReqEntity(
                    null,
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
                    request.time(),
                    request.uid(),
                    buffer.limit(),
                    request.streamId(),
                    request.requestHeaders().toString(),
                    "",
                    "",
                    IOUtils.byteBuffer2String(buffer.asReadOnlyBuffer()),
                    null,
                    null,
                    null,
                    null);
            DBManager.getInstance().getReqEntityDao().insert(reqEntity);
        }
        chain.process(buffer);
    }


    @Override
    protected void intercept(@NonNull HttpResponseChain chain, @NonNull ByteBuffer buffer, int index) throws IOException {
        HttpResponse response = chain.response();
        List<ReqEntity> reqEntities = DBManager.getInstance()
                .getReqEntityDao()
                .queryBuilder()
                .where(ReqEntityDao.Properties.SessionId.eq(response.id()))
                .list();

        if (reqEntities != null && reqEntities.size() > 0) {
            ReqEntity entity = reqEntities.get(0);
            entity.setRespCode(response.code());
            String s = entity.getRespContent();
            if (s == null) {
                entity.setRespContent(IOUtils.byteBuffer2String(buffer.asReadOnlyBuffer()));
            } else {
                entity.setRespContent(s + IOUtils.byteBuffer2String(buffer.asReadOnlyBuffer()));
            }
            entity.setRespMessage(response.message());
            entity.setIsWebSocket(response.isWebSocket());
            DBManager.getInstance().getReqEntityDao().update(entity);
        }

        chain.process(buffer);
    }
}
