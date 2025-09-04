/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.block.vault.VaultSharedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VaultSharedData.class)
public interface VaultSharedDataAccessor
{
	@Accessor("codec")
	static Codec<VaultSharedData> getCodec()
	{
		throw new AssertionError();
	}
}
