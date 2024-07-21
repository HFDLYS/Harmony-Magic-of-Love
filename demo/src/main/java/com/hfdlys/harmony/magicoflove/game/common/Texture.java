package com.hfdlys.harmony.magicoflove.game.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

import org.apache.ibatis.io.Resources;

import com.hfdlys.harmony.magicoflove.util.ImageUtil;

import java.awt.Toolkit;
import java.awt.image.*;

/**
 * <p>
 *  纹理类
 * </p>
 * @author Jiasheng Wang
 * @since 2024-07-18
 */
public class Texture implements Serializable {
    /**
     * 图像
     */
    private BufferedImage image;
    private BufferedImage scaledImage;

    /**
     * x轴方向的长度（高、height）
     */
    private int lx;
    private int scaledLx;

    /**
     * y轴方向的长度（宽、width）
     */
    private int ly;
    private int scaledLy;

    /**
     * x轴方向偏移量
     */
    private int dx;
    private int scaledDx;

    /**
     * y轴方向偏移量
     */
    private int dy;
    private int scaledDy;

    /**
     * 整体缩放比例（默认=-1）
     */
    private float scale;

    /**
     * 构造一个纹理
     * @param image 纹理图像
     * @param lx 纹理x轴方向长度
     * @param ly 纹理y轴方向长度
     * @param dx 纹理x轴方向偏移量
     * @param dy 纹理y轴方向偏移量
     */
    public Texture(BufferedImage image, int lx, int ly, int dx, int dy) {
        this.image = image;
        this.lx = lx;
        this.ly = ly;
        this.dx = dx;
        this.dy = dy;
        this.scale = -1;
        setScale(1.0f);
    }

    /**
     * 通过文件路径构造一个纹理
     * @param imagePath 纹理图像路径
     * @param lx 纹理x轴方向长度
     * @param ly 纹理y轴方向长度
     * @param dx 纹理x轴方向偏移量
     * @param dy 纹理y轴方向偏移量
     * @throws IOException 读取图片异常
     */
    public Texture(String imagePath, int lx, int ly, int dx, int dy) throws IOException {
        this.image = ImageIO.read(Resources.getResourceAsStream(imagePath));
        this.lx = lx;
        this.ly = ly;
        this.dx = dx;
        this.dy = dy;
        this.scale = -1;
        setScale(1.0f);
    }

    /**
     * 通过byte数组构造一个纹理
     */
    public Texture(byte[] imageBytes, int lx, int ly, int dx, int dy) throws IOException {
        InputStream is = new ByteArrayInputStream(imageBytes);
        this.image = ImageIO.read(is);
        this.lx = lx;
        this.ly = ly;
        this.dx = dx;
        this.dy = dy;
        this.scale = -1;
        setScale(1.0f);
    }

    /**
     * deep copy constructor
     * @param another another constructor
     */
    public Texture(Texture another) {
        // 原值拷贝，不拷贝缩放后的内容
        this(ImageUtil.copyBufferedImage(another.image), another.lx, another.ly, another.dx, another.dy);
        this.setScale(another.scale);
    }

    /**
     * 设置缩放比例（强制刷新比例）
     * @param scale 比例
     */
    public void flushScale(float scale) {
        this.scale = scale;
        scaledLx = (int)(scale * lx);
        scaledLy = (int)(scale * ly);
        scaledDx = (int)(scale * dx);
        scaledDy = (int)(scale * dy);
        scaledImage = ImageUtil.scale(image, scale);
    }

    /**
     * 设置缩放比例
     * @param scale 比例
     * @return 如果新的比例不同，纹理被更新，则返回true；否则返回false
     */
    public boolean setScale(float scale) {
        if(this.scale == scale) return false;
        flushScale(scale);
        return true;
    }

    /**
     * 获取纹理图像（无视缩放比例）
     */
    public BufferedImage getRawImage() {
        return image;
    }

    /**
     * 获取纹理图像（考虑缩放比例）
     */
    public BufferedImage getImage() {
        if(scale < 0)
            return image;
        else {
            return scaledImage;
        }
    }

    /**
     * 返回裁剪后的贴图
     * 注意：此方法运算量消耗巨大，不可以每个游戏帧都调用此方法。
     * @param x 游戏x轴
     * @param y 游戏y轴
     * @param lx x轴长度
     * @param ly y轴长度
     * @return 裁剪后的图片
     */
    public BufferedImage getCutImage(int x, int y, int lx, int ly) {
        // 注意，参数是游戏xy轴，方法内是swing xy轴
        CropImageFilter filter = new CropImageFilter(y, x, ly, lx);
        return ImageUtil.toBufferedImage(Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(this.getImage().getSource(), filter)));
    }

    /**
     * 返回裁剪后的纹理（主要用于预处理精灵图）
     * @param x 游戏x轴
     * @param y 游戏y轴
     * @param lx x轴长度 & 裁剪后x轴长度
     * @param ly y轴长度 & 裁剪后x轴长度
     * @param dx 裁剪后x轴偏移量
     * @param dy 裁剪后y轴偏移量
     * @return 裁剪后的纹理
     */
    public Texture getCutTexture(int x, int y, int lx, int ly, int dx, int dy) {
        CropImageFilter filter = new CropImageFilter(y, x, ly, lx);
        return new Texture(ImageUtil.toBufferedImage(Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(this.getImage().getSource(), filter))), lx, ly, dx, dy);
    }

    /**
     * 获取x轴方向长度
     */
    public int getLx() {
        return scale < 0 ? lx : scaledLx;
    }

    /**
     * 获取y轴方向长度
     */
    public int getLy() {
        return scale < 0 ? ly : scaledLy;
    }

    /**
     * 获取x轴偏移量
     */
    public int getDx() {
        return scale < 0 ? dx : scaledDx;
    }

    /**
     * 获取y轴偏移量
     */
    public int getDy() {
        return scale < 0 ? dy : scaledDy;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        ImageIO.write(image, "png", out); // png is lossless
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        image = ImageIO.read(in);
        scale = -1;
    }
}
