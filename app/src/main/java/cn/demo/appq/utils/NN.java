package cn.demo.appq.utils;

import android.text.TextUtils;
import android.util.Base64;

public class NN {

    /**
     * PAAS解密。
     * 1. 默认不加密，解压方式:  base64-->gzip解压缩
     * 1. AES_ECB，解压方式:
     * 1. AES_CBC，解压方式:
     *
     * @param spv
     * @param reqv
     * @param reqt
     * @param content
     * @return
     */
    public static String parserPaas(String spv, String reqv, String reqt, String content) {
        String result = "";
        if (TextUtils.isEmpty(content)) {
            return result;
        }
        L.i("请求SPV:" + new String(Base64.decode(spv, Base64.DEFAULT)));
        L.i("请求reqv:" + reqv);
        L.i("请求reqt:" + reqt);
        L.i("请求content:[" + content + "]");
        result = parserNoEncrypt(content);
        if (!TextUtils.isEmpty(reqv)) {
            String pwd = PaasKey.getEncryptKey(new String(Base64.decode(spv, Base64.DEFAULT)),
                    reqt);
            L.e("加密key：" + pwd);
            if ("1".equals(reqv.trim())) {
                result = parserAesEcb(pwd, result);
            } else if ("2".equals(reqv.trim())) {
                result = parserAesCbc(pwd, result);
            }
        }


        return result;
    }


    /**
     * 解析AES_CBC解密
     *
     * @param pwd
     * @param content
     * @return
     */
    private static String parserAesCbc(String pwd, String content) {
        try {
            return new String(AESEncrypt.CBCDecrypt(pwd, content));
        } catch (Throwable e) {
            L.e(e);
            return "";
        }
    }

    /**
     * 解析AES_ECB解密
     *
     * @param pwd
     * @param content
     * @return
     */
    private static String parserAesEcb(String pwd, String content) {
        try {
            return new String(AESEncrypt.ECBDecrypt(content, pwd));
        } catch (Throwable e) {
            L.e(e);
            return "";
        }
    }

    /**
     * 解析不加密的.解压方式:  base64-->gzip解压缩
     *
     * @param content
     * @return
     */
    private static String parserNoEncrypt(String content) {
        byte[] ks = Base64.decode(content, Base64.DEFAULT);
        return ZipUtils.decompressForGzip(ks);
    }


}
