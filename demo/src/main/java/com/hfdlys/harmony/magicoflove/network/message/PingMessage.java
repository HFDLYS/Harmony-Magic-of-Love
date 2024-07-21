package com.hfdlys.harmony.magicoflove.network.message;

import lombok.Data;

@Data
public class PingMessage {
    /**
     * ping（延迟）
     */
    private long timestamp;
}
