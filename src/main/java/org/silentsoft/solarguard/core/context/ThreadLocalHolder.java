package org.silentsoft.solarguard.core.context;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalHolder {

    private static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    public static void initialize() {
        threadLocal.set(new HashMap<>());
    }

    public static boolean isInitialized() {
        return threadLocal.get() != null;
    }

    public static void put(String key, Object value) {
        threadLocal.get().put(key, value);
    }

    public static Object get(String key) {
        return threadLocal.get().get(key);
    }

    public static void destroy() {
        threadLocal.remove();
    }

}
