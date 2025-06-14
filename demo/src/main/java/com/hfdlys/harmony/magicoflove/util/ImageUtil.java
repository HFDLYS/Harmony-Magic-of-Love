package com.hfdlys.harmony.magicoflove.util;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JTextField;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.image.AffineTransformOp;


/**
 * <p>图像工具类</p>
 * <p>提供图像处理的方法</p>
 * @author Jiasheng Wang
 * @since 2024-07-18
 */
public class ImageUtil {
    /**
     * 判断一个文件是否是有效的图片文件
     * @param file 文件
     */
     public static boolean isValidImageFile(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            if (image != null && file.getName().toLowerCase().endsWith(".png")) {
                int width = image.getWidth();
                int height = image.getHeight();
                return width == 832 && height == 1344;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 将一个按钮变为全透明
     * @param button 按钮
     */
    public static void setButtonTransparent(JButton button, boolean focusPainted) {
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(focusPainted);
    }

    /**
     * 将一个按钮变为全透明
     * @param textField 按钮
     */
    public static void setTextFieldTransparent(JTextField textField, boolean focusPainted) {
        textField.setOpaque(false);
    }

    /**
     * 顺时针旋转图片
     * （注意图片裁剪问题，旋转后图片可能会超出原有边界）
     * @param imgOld 旧图片
     * @param deg 旋转角度
     * @return 新图片
     */
    public static BufferedImage rotate(BufferedImage imgOld, int deg){                                                 //parameter same as method above
        BufferedImage imgNew = new BufferedImage(imgOld.getWidth(), imgOld.getHeight(), imgOld.getType());              //create new buffered image
        Graphics2D g = (Graphics2D) imgNew.getGraphics();                                                               //create new graphics
        g.rotate(Math.toRadians(deg), imgOld.getWidth()/2, imgOld.getHeight()/2);                                    //configure rotation
        g.drawImage(imgOld, 0, 0, null);                                                                                //draw rotated image
        return imgNew;                                                                                                  //return rotated image
    }

    /**
     * 缩放图片
     * @param imgOld 旧图片
     * @param scale 缩放比例
     * @return 新图片
     */
    public static BufferedImage scale(BufferedImage imgOld, float scale) {
        BufferedImage before = imgOld;
        int w = before.getWidth();
        int h = before.getHeight();
        BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);
        AffineTransformOp scaleOp =
                new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return scaleOp.filter(before, after);
    }

    public static Image resize(Image imgOld, int width, int height) {
        return imgOld.getScaledInstance(width, height, Image.SCALE_DEFAULT);
    }

    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        // Return the buffered image
        return bimage;
    }

    /**
     * copy a buffered image
     * @param img the image to be copied
     * @return the copied image
     */
    public static BufferedImage copyBufferedImage(BufferedImage img) {
        if(img == null) return null;
        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        // Return the buffered image
        return bimage;
    }

    /**
     * 获取一个新的BufferedImage
     * 新的BufferedImage由A和B叠放而成
     * A在下，B在上
     * @param A BufferedImage A
     * @param B BufferedImage B
     * @return BufferedImage A+B
     */
    public static BufferedImage combineTwoBufferedImage(BufferedImage A, BufferedImage B, int x, int y) {
        BufferedImage C = copyBufferedImage(A);
        C.getGraphics().drawImage(B, x, y, null);
        return C;
    }

    /**
     * 线性渐淡
     * @param A 图像A
     * @param B 图像B
     * @return C = A + B
     */
    public static BufferedImage linearDodge(BufferedImage A, BufferedImage B) {
        int width = A.getWidth();
        int height = A.getHeight();
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                Color aC = new Color(A.getRGB(i, j));
                Color bC = new Color(B.getRGB(i, j));
                int r = Math.min(255, aC.getRed() + bC.getRed());
                int g = Math.min(255, aC.getGreen() + bC.getGreen());
                int b = Math.min(255, aC.getBlue() + bC.getBlue());
                A.setRGB(r, g, b);
            }
        }
        return A;
    }
}
