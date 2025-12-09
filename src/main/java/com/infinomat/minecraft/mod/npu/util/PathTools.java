package com.infinomat.minecraft.mod.npu.util;

public final class PathTools {
    public static String linkPath(String... arg){
        StringBuilder path = new StringBuilder();
        for (String s : arg){
            if (path.isEmpty()) path.append(s);
            else path.append("/").append(s);
        }
        return path.toString();
    }
}
