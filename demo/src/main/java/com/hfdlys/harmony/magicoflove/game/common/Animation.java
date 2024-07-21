package com.hfdlys.harmony.magicoflove.game.common;

import lombok.Data;

/**
 * <p>动画类</p>
 * <p>用于处理角色动画</p>
 * @author Jiasheng Wang
 * @since 2024-07-18
 */
@Data
public class Animation {
    /**
     * 记录当前动画播放的动画帧
     */
    private int cnt;

    /**
     * 动画帧数
     */
    private int cntLength;

    /**
     * 记录当前动画播放的游戏帧
     */
    private int cnt2;

    /**
     * 动画帧长度
     */
    private int cnt2Length;

    /**
     * 记录上一次请求移动动画的时间戳
     */
    private int lastTimeStamp;

    /**
     * 记录动画
     */
    private Texture[] textures;

    /**
     * 构造一个角色动画
     * @param textures 动画信息
     */
    public Animation(Texture[] textures, int cntLength) {
        this.cnt = 0;
        this.cnt2 = 0;
        this.cntLength = cntLength;
        this.cnt2Length = (int)(120 / cntLength); // 120是游戏帧率
        this.textures = textures;
    }

    /**
     * 输入时间戳，返回角色贴图
     * @param timeStamp 时间戳
     * @return 角色贴图
     */
    public Texture play(int timeStamp) {
        if(lastTimeStamp + 1 != timeStamp) // 重置动画
            cnt = cnt2 = 0;
        else { // 判断是否要播放下一帧动画
            cnt = (cnt + (cnt2 == (cnt2Length - 1) ? 1 : 0)) % cntLength;
            cnt2 = (cnt2 + 1) % cnt2Length;
        }
        lastTimeStamp = timeStamp;
        return textures[cnt];
    }
}
