package com.beetron.outmall.utils;

import com.beetron.outmall.models.OrderFixInfo;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/26.
 * Time: 12:57.
 */
public class OrderFixSerializer implements JsonDeserializer<OrderFixInfo> {
    @Override
    public OrderFixInfo deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return null;
    }
}
