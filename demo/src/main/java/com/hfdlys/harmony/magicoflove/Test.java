package com.hfdlys.harmony.magicoflove;

import com.hfdlys.harmony.magicoflove.util.RSAUtil;
import com.hfdlys.harmony.magicoflove.util.SecurityUtil;

public class Test {
    public static void main(String[] args) throws Exception {
        System.out.println(SecurityUtil.hashPassword("qweasdzxc"));
        System.out.println(RSAUtil.getInstance().encrypt("qweasdzxc"));
    }
}
