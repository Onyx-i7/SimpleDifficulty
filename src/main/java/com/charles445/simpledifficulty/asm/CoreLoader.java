package com.charles445.simpledifficulty.asm;

import com.charles445.simpledifficulty.asm.helper.ObfHelper;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.Name("SimpleDifficulty ASM")
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions({"com.charles445.simpledifficulty.asm"})
public class CoreLoader implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"com.charles445.simpledifficulty.asm.SimpleDifficultyASM"};
    }

    @Nullable
    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        if (data.containsKey("runtimeDeobfuscationEnabled")) {
            Boolean isObfuscated = (Boolean) data.get("runtimeDeobfuscationEnabled");
            ObfHelper.setObfuscated(isObfuscated != null && isObfuscated);
        }
        ObfHelper.setRunsAfterDeobfRemapper(true);
    }

    @Nullable
    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
