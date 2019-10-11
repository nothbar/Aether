package cn.demo.appq.utils;


import android.text.TextUtils;

import com.github.megatronking.netbare.L;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @Copyright © 2018 EGuan Inc. All rights reserved.
 * @Description: TODO
 * @Version: 1.0
 * @Create: 2018/5/10 16:23
 * @Author: sanbo
 */
public class ZipUtils {
    private static char[] base64EncodeChars = new char[]{
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '+', '/'};
    private static byte[] base64DecodeChars = new byte[]{
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,
            -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
            -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
            41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1};


    /**
     * Gzip 压缩数据
     */
    public static byte[] compressForGzip(String unGzipStr) throws IOException {
        if (TextUtils.isEmpty(unGzipStr)) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(baos);
        gzip.write(unGzipStr.getBytes());
        gzip.close();
        byte[] encode = baos.toByteArray();
        baos.flush();
        baos.close();
        return encode;
    }

    /**
     * Gzip解压数据
     */
    public static String decompressForGzip(byte[] gzipStr) {
        ByteArrayOutputStream out = null;
        ByteArrayInputStream in = null;
        GZIPInputStream gzip = null;
        try {
            if (gzipStr == null || gzipStr.length <= 0) {
                return null;
            }
            out = new ByteArrayOutputStream();
            in = new ByteArrayInputStream(gzipStr);
            // android 对GZIPInputStream有调整，严格模式下会报错
            gzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n = 0;
            while ((n = gzip.read(buffer, 0, buffer.length)) > 0) {
                out.write(buffer, 0, n);
            }
            return out.toString();
        } catch (Throwable throwable) {
        } finally {
            if (gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
//    /**
//     * Gzip 压缩数据
//     *
//     * @param unGzipStr
//     * @return
//     */
//    public static String compressForGzip(String unGzipStr) {
//        L.i("加密前：" + unGzipStr);
//        if (TextUtils.isEmpty(unGzipStr)) {
//            return null;
//        }
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            GZIPOutputStream gzip = new GZIPOutputStream(baos);
//            gzip.write(unGzipStr.getBytes());
//            gzip.close();
//            byte[] encode = baos.toByteArray();
//            baos.flush();
//            baos.close();
//            String s = encode(encode);
//            L.i("加密后：" + s);
//
//            String ss = decompressForGzip(s);
//            L.i("=====>：" + ss);
//            return s;
//        } catch (Throwable e) {
//
//        }
//        return null;
//    }

    /**
     * Gzip解压数据
     *
     * @param gzipStr
     * @return
     */
    public static String decompressForGzip(String gzipStr) {
        if (TextUtils.isEmpty(gzipStr)) {
            return null;
        }
        try {
            byte[] t = decode(gzipStr);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(t);
            GZIPInputStream gzip = new GZIPInputStream(in);
            byte[] buffer = new byte[8192];
            int n = 0;
            while ((n = gzip.read(buffer, 0, buffer.length)) > 0) {
                out.write(buffer, 0, n);
            }
            gzip.close();
            in.close();
            out.close();
            return out.toString();
        } catch (Throwable e) {
            L.e(e);
        }
        return null;
    }

    //编码
    public static String encode(byte[] data) {
        StringBuffer sb = new StringBuffer();
        int len = data.length;
        int i = 0;
        int b1, b2, b3;
        while (i < len) {
            b1 = data[i++] & 0xff;
            if (i == len) {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
                sb.append("==");
                break;
            }
            b2 = data[i++] & 0xff;
            if (i == len) {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
                sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
                sb.append("=");
                break;
            }
            b3 = data[i++] & 0xff;
            sb.append(base64EncodeChars[b1 >>> 2]);
            sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
            sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);
            sb.append(base64EncodeChars[b3 & 0x3f]);
        }
        return sb.toString();
    }

    //解码
    public static byte[] decode(String str) {
        byte[] b = null;
        try {
            StringBuffer sb = new StringBuffer();
            byte[] data = str.getBytes("US-ASCII");
            int len = data.length;
            int i = 0;
            int b1, b2, b3, b4;
            while (i < len) {
                /* b1 */
                do {
                    b1 = base64DecodeChars[data[i++]];
                } while (i < len && b1 == -1);
                if (b1 == -1) {
                    break;
                }
                /* b2 */
                do {
                    b2 = base64DecodeChars[data[i++]];
                } while (i < len && b2 == -1);
                if (b2 == -1) {
                    break;
                }
                sb.append((char) ((b1 << 2) | ((b2 & 0x30) >>> 4)));
                /* b3 */
                do {
                    b3 = data[i++];
                    if (b3 == 61) {
                        return sb.toString().getBytes("iso8859-1");
                    }
                    b3 = base64DecodeChars[b3];
                } while (i < len && b3 == -1);
                if (b3 == -1) {
                    break;
                }
                sb.append((char) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));
                /* b4 */
                do {
                    b4 = data[i++];
                    if (b4 == 61) {
                        return sb.toString().getBytes("iso8859-1");
                    }
                    b4 = base64DecodeChars[b4];
                } while (i < len && b4 == -1);
                if (b4 == -1) {
                    break;
                }
                sb.append((char) (((b3 & 0x03) << 6) | b4));
            }

            b = sb.toString().getBytes("iso8859-1");
        } catch (Throwable e) {

            return null;
        }
        return b;
    }
}
