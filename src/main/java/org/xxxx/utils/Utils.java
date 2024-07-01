package org.xxxx.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

    public static String getMD5(String input) {
        try {
            // 创建一个MD5算法实例
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 计算MD5哈希值
            byte[] messageDigest = md.digest(input.getBytes());

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getClassByte(String className, ClassLoader loader){
        try {
            byte[] classfileBuffer;
            InputStream in = loader.getResourceAsStream(className + ".class");
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while (true) {
                if (!((nRead = in.read(data, 0, data.length)) != -1)) break;
                buffer.write(data, 0, nRead);
            }
            classfileBuffer = buffer.toByteArray();
            return classfileBuffer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
