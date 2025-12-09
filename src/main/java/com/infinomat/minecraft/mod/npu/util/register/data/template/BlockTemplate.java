package com.infinomat.minecraft.mod.npu.util.register.data.template;

import com.infinomat.minecraft.mod.npu.util.PathTools;
import com.infinomat.minecraft.mod.npu.util.Reference;

public class BlockTemplate {
    public String id;
    public String StructureType;
    public String loadMethod;
    public boolean double_enable;

    public String Material;
    public boolean noOcclusion;
    public boolean noCollision;
    public boolean noLootTable;
    public boolean noParticlesOnBreak;

    public boolean customModel = false;
    public String render = "default";

    public String getId(){
        return id;
    }
    public String getModelPath (String id){
        if (customModel) return PathTools.linkPath(Reference.PATH.get(Reference.PathType.MODEL), "block", String.valueOf(id.charAt(0)), id + "_model" + ".json");
        else return PathTools.linkPath(Reference.PATH.get(Reference.PathType.MODEL), "block", String.valueOf(id.charAt(0)), id + ".json");
    }
    public String getModelPath (String id, String attachment){
        if (customModel) return PathTools.linkPath(Reference.PATH.get(Reference.PathType.MODEL), "block", String.valueOf(id.charAt(0)), id + "_" + attachment + "_model" + ".json");
        return PathTools.linkPath(Reference.PATH.get(Reference.PathType.MODEL), "block", String.valueOf(id.charAt(0)), id + "_" + attachment + ".json");
    }
    public String getModelPath (String id, String attachment1, String attachment2){
        if (customModel) return PathTools.linkPath(Reference.PATH.get(Reference.PathType.MODEL), "block", String.valueOf(id.charAt(0)), id + "_" + attachment1 + "_" + attachment2 + "_model" + ".json");
        return PathTools.linkPath(Reference.PATH.get(Reference.PathType.MODEL), "block", String.valueOf(id.charAt(0)), id + "_" + attachment1 + "_" + attachment2 + ".json");
    }
}
