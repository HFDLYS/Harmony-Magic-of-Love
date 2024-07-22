package com.hfdlys.harmony.magicoflove.game.controller;

import com.hfdlys.harmony.magicoflove.game.entity.Character;

public class ServerRemoteController extends Controller {
    @Override
    public void control(Character character) {
        character.move(getControlMessage().getMoveDirect());
        character.aim(getControlMessage().getAimX(), getControlMessage().getAimY());
        if(getControlMessage().isAttack()) {
            character.attack();
        }
    }
}
