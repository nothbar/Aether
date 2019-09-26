package cn.demo.appq.utils;

import android.text.TextUtils;

import java.security.MessageDigest;

public class PaasKey {


    /**
     * 获取KEY
     *
     * @param spv  Android|appId|sdkVersion|policyVersion|appVersion
     * @param reqt 请求时间
     * @return
     */
    public static String getEncryptKey(String spv, String reqt) {
        if (TextUtils.isEmpty(spv)) {
            return "";
        }
        L.i("spv:" + spv);


        L.i("spv---->" + TextUtils.isEmpty(spv) + "-----" + spv.contains("|")
                + "======" + spv.contains("\\|"));
        String appid = "", sdkVersion = "";
        if (spv.contains("|")) {
            String[] as = spv.split("\\|");
            if (as.length == 5) {
                appid = as[1];
                sdkVersion = as[2];
            }
        }
        return getEncryptKey("Android", appid, sdkVersion, reqt);
    }

    /**
     * 获取加密key
     */
    private static String getEncryptKey(String platform, String appId, String sdkVersionName,
                                        String time) {
        try {
            String originalKey = platform + appId + sdkVersionName + time;
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            String md5Code = toHex(md5.digest(originalKey.getBytes("utf-8")));
            String encodeKey = Base64Utils.encode(md5Code.getBytes());
            String[] versionCode = sdkVersionName.split("\\.");
            int length = versionCode.length;
            String key = "";
            if (length > 2) {
                int valuePosition = Integer.parseInt(versionCode[length - 1]);
                int startPosition = Integer.parseInt(versionCode[length - 2]);
                key = getEncryptCode(encodeKey, startPosition, valuePosition);
            }
            return key;
        } catch (Throwable e) {
        }
        return null;
    }


    /**
     * 加密秘钥 生成
     *
     * @param startPosition 取数的起始位置 偶数从前往后，奇数从后往前
     * @param valuePosition 取数位置，取奇数位还是偶数位 奇数取奇数位值，偶数取值偶数位值
     */
    private static String getEncryptCode(String rowKey, int startPosition, int valuePosition) {
        try {
            if (rowKey != null) {
                if (startPosition % 2 != 0) {
                    rowKey = new StringBuilder(rowKey).reverse().toString();
                }
                StringBuffer sbKey = new StringBuffer();
                if (valuePosition % 2 != 0) {
                    for (int i = 0; i < rowKey.length(); i++) {
                        sbKey.append(rowKey.charAt(i));
                        i++;
                    }
                } else {
                    for (int i = 0; i < rowKey.length(); i++) {
                        i++;
                        sbKey.append(rowKey.charAt(i));
                    }
                }
                if (sbKey.length() < 16) {
                    for (int i = rowKey.length() - 1; 0 < i; i--) {
                        sbKey.append(rowKey.charAt(i));
                        if (sbKey.length() == 16) {
                            return sbKey.toString();
                        }
                    }
                } else {
                    return sbKey.substring(0, 16);
                }
                return String.valueOf(sbKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * to String
     */
    public static String toHex(byte[] bytes) {
        final char[] hexDigits = "0123456789ABCDEF".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            ret.append(hexDigits[(bytes[i] >> 4) & 0x0f]);
            ret.append(hexDigits[bytes[i] & 0x0f]);
        }
        return ret.toString();
    }

}
