package com.hfdlys.harmony.magicoflove.network.message.EntityRegister;



import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Data
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "class"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = EntityRegisterMessage.class, name = "EntityRegisterMessage"),
    @JsonSubTypes.Type(value = ProjectileRegisterMessage.class, name = "ProjectileRegisterMessage"),
    @JsonSubTypes.Type(value = CharacterRegisterMessage.class, name = "CharacterRegisterMessage"),
    @JsonSubTypes.Type(value = ObstacleRegisterMessage.class, name = "ObstacleRegisterMessage"),
})
public class EntityRegisterMessage {
    /**
     * ID
     */
    private int id;

    /**
     * 类型
     */
    private int type;

    public EntityRegisterMessage() {
    }

    public EntityRegisterMessage(int type) {
        this.type = type;
    }


}
