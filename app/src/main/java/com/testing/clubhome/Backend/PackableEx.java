package com.testing.clubhome.Backend;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}