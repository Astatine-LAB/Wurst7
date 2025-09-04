package net.wurstclient.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.block.vault.VaultSharedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VaultSharedData.class)
public interface VaultSharedDataAccessor {
    @Accessor("codec")
    static Codec<VaultSharedData> getCodec() {
        throw new AssertionError();
    }
}