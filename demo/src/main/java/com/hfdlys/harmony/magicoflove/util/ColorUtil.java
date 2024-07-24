package com.hfdlys.harmony.magicoflove.util;

import java.awt.Color;
import java.util.Random;

/**
 * 颜色工具类
 * @author Jiasheng Wang
 * @since 2024-07-23
 */
public class ColorUtil {
    public static Color convertToColor(String s) {
        long seed = s.hashCode();

        Random random = new Random(seed);
        int r = 75 + random.nextInt(121);
        int g = 12 + random.nextInt(51);
        int b = 235 - random.nextInt(91);

        return new Color(r, g, b);
    }

    public static void main(String[] args) {
        System.out.println(convertToColor("ss"));
    }
}