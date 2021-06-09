package com.tsu.utils;


import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;

import java.util.HashMap;
import java.util.Map;

public class EncryptUtil {

    public static final int HASH_ITERATIONS = 512;

    public static final String SHA1 = "sha-1";

    public static String sha1(String source, String salt) {
        return new SimpleHash(SHA1, source, salt, HASH_ITERATIONS).toHex();
    }


    public static String getSalt() {
        return new SecureRandomNumberGenerator().nextBytes().toString();
    }

    public static Map<String, String> encrypt(String source) {
        Map<String, String> result = new HashMap<>();
        String salt = getSalt();
        String password = sha1(source, salt);
        result.put("password", password);
        result.put("salt", salt);
        return result;
    }
}
