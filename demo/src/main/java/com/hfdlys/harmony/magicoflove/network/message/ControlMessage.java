package com.hfdlys.harmony.magicoflove.network.message;


import lombok.Data;

/**
 * <p>控制信息</p>
 * <p>客户端发送给服务器的控制信息</p>
 * @author Jiasheng Wang
 * @since 2024-07-18
 */

@Data
public class ControlMessage {
    /**
     * 实体ID
     */
    private int id;
    /**
     * 移动方向
     */
    private int moveDirect;
    /**
     * 瞄准位置的x轴
     */
    private int aimX;

    /**
     * 瞄准位置的y轴
     */
    private int aimY;

    /**
     * 攻击指令
     */
    private boolean attack;

    /**
     * 冲刺指令
     */
    private boolean rush;

    /**
     * 深拷贝
     */
    @Override
    public ControlMessage clone() {
        ControlMessage control = new ControlMessage();
        control.setId(this.id);
        control.setMoveDirect(this.moveDirect);
        control.setAimX(this.aimX);
        control.setAimY(this.aimY);
        control.setAttack(this.attack);
        control.setRush(this.rush);
        return control;
    }
}
