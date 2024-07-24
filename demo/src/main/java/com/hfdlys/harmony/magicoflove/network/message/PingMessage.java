package com.hfdlys.harmony.magicoflove.network.message;

import lombok.Data;

/**
 * ping信息
 * @author Jiasheng Wang
 * @since 2024-07-18
 */
@Data
public class PingMessage {
    /**
     * ping（延迟）
     */
    private long timestamp;
}
