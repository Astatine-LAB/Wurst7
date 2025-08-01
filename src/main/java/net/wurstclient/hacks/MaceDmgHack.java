/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.PlayerAttacksEntityListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.util.ChatUtils;

import java.util.HashSet;
import java.util.Set;

@SearchTags({"mace dmg", "MaceDamage", "mace damage", "mace"})
public final class MaceDmgHack extends Hack
	implements PlayerAttacksEntityListener
{
	private final SliderSetting sqrtValue = new SliderSetting("Mace Sqrt Value",
		"description.wurst.setting.maceDmg.SqrtValue", 300, 1, 2000, 1,
		SliderSetting.ValueDisplay.DECIMAL);
	
	private final CheckboxSetting debuggingButton = new CheckboxSetting(
		"Debugging", "description.wurst.setting.MaceDmg.Debugging", false);
	
	private Set<Block> ignoreBlocks = new HashSet<>();
	
	public MaceDmgHack()
	{
		super("MaceDMG");
		setCategory(Category.COMBAT);
		
		addSetting(sqrtValue);
		addSetting(debuggingButton);
		
		ignoreBlocks = Set.of(Blocks.AIR, Blocks.VOID_AIR, Blocks.WATER,
			Blocks.WATER_CAULDRON, Blocks.LAVA, Blocks.LAVA_CAULDRON,
			Blocks.TALL_GRASS, Blocks.COBWEB, Blocks.KELP, Blocks.KELP_PLANT);
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(PlayerAttacksEntityListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(PlayerAttacksEntityListener.class, this);
	}
	
	@Override
	public void onPlayerAttacksEntity(Entity target)
	{
		if(!MC.player.getMainHandStack().isOf(Items.MACE))
			return;
		
		int higher = getMaximumHeight(MC.player);
		if(debuggingButton.isChecked())
		{
			ChatUtils.message(String.valueOf(higher));
			ChatUtils.message(String.valueOf(sqrtValue.getValue()));
		}
		
		if(higher <= 2)
			return;
		else
			sqrtValue.setValue(calculateSqrtValue(higher));
		
		for(int i = 0; i < 4; i++)
			sendFakeY(0);
		sendFakeY(Math.sqrt(sqrtValue.getValue()));
		sendFakeY(0);
	}
	
	private int calculateSqrtValue(int n)
	{
		if(n < 3)
			return 0;
		
		int m = (n - 3) / 5;
		int s = (n - 3) % 5;
		int[] c = {1, 4, 10, 17, 27};
		
		int a_n = 25 * m * (m - 1) + m * (27 + 10 * (s + 1)) + c[s];
		return a_n;
	}
	
	private int getMaximumHeight(ClientPlayerEntity player)
	{
		int MAX_HEIGHT = 2000;
		int maxAvailableHeight = 0;
		
		for(int i = (int)player.getY() + 1; i < MAX_HEIGHT; i++)
		{
			BlockPos blockPos =
				BlockPos.ofFloored(player.getX(), i, player.getZ());
			Block block = MinecraftClient.getInstance().world
				.getBlockState(blockPos).getBlock();
			
			if(ignoreBlocks.contains(block))
				maxAvailableHeight++;
			
			else
				break;
		}
		
		return maxAvailableHeight - 1;
	}
	
	private void sendFakeY(double offset)
	{
		ClientPlayNetworkHandler netHandler = MC.player.networkHandler;
		netHandler.sendPacket(
			new PositionAndOnGround(MC.player.getX(), MC.player.getY() + offset,
				MC.player.getZ(), false, MC.player.horizontalCollision));
	}
}
