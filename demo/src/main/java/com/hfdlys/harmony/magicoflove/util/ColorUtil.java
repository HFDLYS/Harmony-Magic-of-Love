package com.hfdlys.harmony.magicoflove.util;

import java.awt.Color;
import java.util.Random;

public class ColorUtil {
    public static Color convertToColor(String s) {
        long seed = s.hashCode();

        Random random = new Random(seed);
        int r = random.nextInt(200) + 30;
        int g = random.nextInt(200) + 30;
        int b = random.nextInt(200) + 30;

        return new Color(r, g, b);
    }
}