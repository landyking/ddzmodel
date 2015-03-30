package com.jfreer.game.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * User: landy
 * Date: 15/3/30
 * Time: 上午10:12
 */
public class JsonUtils {
    private static final ObjectMapper json = new ObjectMapper();

    public static String toJson(Object object) throws JsonProcessingException {
        return json.writeValueAsString(object);
    }
}
