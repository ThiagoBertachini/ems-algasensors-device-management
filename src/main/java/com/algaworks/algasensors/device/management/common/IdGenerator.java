package com.algaworks.algasensors.device.management.common;

import io.hypersistence.tsid.TSID;

public class IdGenerator {
    private static final TSID.Factory tsIdFactory =
            TSID.Factory.builder().build();

    private IdGenerator() {
    }

    public static TSID generateTSID() {
        return tsIdFactory.generate();
    }
}
