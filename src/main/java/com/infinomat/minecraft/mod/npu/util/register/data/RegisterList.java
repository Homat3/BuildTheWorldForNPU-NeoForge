package com.infinomat.minecraft.mod.npu.util.register.data;

import java.util.List;

public final class RegisterList {
    private String id;
    private List<TabInfo> group;

    public String getId() {
        return id;
    }
    public List<TabInfo> getGroups() {
        return group;
    }

    public static class TabInfo{
        public String template;
        public List<String> items;
    }
}
