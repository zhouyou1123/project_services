package com.sioeye.youle.run.order.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AppSignKey {
    private final static String charsetName = "utf-8";
    private final static String md5 = "MD5";

    public static String HEXAndMd5(String plainText) throws NoSuchAlgorithmException {
        try {
            MessageDigest md = MessageDigest.getInstance(md5);
            try {
                md.update(plainText.getBytes(charsetName));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer(200);
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset] & 0xff;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            throw e;
        }
    }
}
