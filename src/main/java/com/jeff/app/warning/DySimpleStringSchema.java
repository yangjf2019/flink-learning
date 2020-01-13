package com.jeff.app.warning;

import java.nio.charset.StandardCharsets;

import org.apache.flink.api.common.serialization.SimpleStringSchema;

/**
 * @author Created by Jeff Yang on 2020-01-13 16:56.
 * Update date:
 * Project: flink-learning
 * Package: com.jeff.app.warning
 * Describe : 序列化
 * Dependency :
 * Frequency: Calculate once a day.
 * Result of Test: test ok,test error
 * Command:
 * <p>
 * Email:  highfei2011@126.com
 * Status：Using online
 * <p>
 * Please note:
 * Must be checked once every time you submit a configuration file is correct!
 * Data is priceless! Accidentally deleted the consequences!
 */

public class DySimpleStringSchema extends SimpleStringSchema {

    private static final long serialVersionUID = 5348469005882366764L;

    // ------------------------------------------------------------------------
    //  Kafka Serialization
    // ------------------------------------------------------------------------

    @Override
    public String deserialize(byte[] message){
        if(message==null){
            return "";
        }
        return new String(message, StandardCharsets.UTF_8);
    }

}