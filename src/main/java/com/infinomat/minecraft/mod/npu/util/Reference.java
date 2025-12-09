package com.infinomat.minecraft.mod.npu.util;

import java.util.Map;

public class Reference {
    // 不可变动值
    public static final String MOD_ID = "npu";
    public static final String MOD_NAME = "Build The World For NPU";
    public static final String VERSION = "3.0.1";

    public static final Map<PathType, String> PATH = Map.of(
            PathType.LOADER, "register/npu",
            PathType.BLOCK, "block",
            PathType.ITEM, "item",
            PathType.CREATIVEMODETAB, "item_group",
            PathType.MODEL, "assets/npu/models"
    );

    public enum PathType{
        LOADER, BLOCK, ITEM, MODEL, CREATIVEMODETAB
    }
}
