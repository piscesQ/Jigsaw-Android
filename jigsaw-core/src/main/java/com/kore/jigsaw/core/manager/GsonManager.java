package com.kore.jigsaw.core.manager;

import com.google.gson.Gson;

/**
 * @author koreq
 * @date 2021-04-14
 * @description
 */
public class GsonManager {

    public static Gson get() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private final static Gson INSTANCE = new Gson();
    }
}
