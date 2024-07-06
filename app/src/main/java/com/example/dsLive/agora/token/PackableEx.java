package com.example.dsLive.agora.token;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
