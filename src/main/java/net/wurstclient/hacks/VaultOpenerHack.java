/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.RightClickListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.util.ChatUtils;

@SearchTags({"vault", "mace"})
public class VaultOpenerHack extends Hack implements RightClickListener
{
	
	private final CheckboxSetting debuggingButton = new CheckboxSetting(
		"Debugging", "description.wurst.setting.MaceDmg.Debugging", false);
	
	public VaultOpenerHack()
	{
		super("VaultOpenner");
		
		addSetting(debuggingButton);
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(RightClickListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(RightClickListener.class, this);
	}
	
	@Override
	public void onRightClick(RightClickEvent event)
	{
		if(!(MC.crosshairTarget instanceof BlockHitResult hit))
		{
			event.cancel();
			return;
		}
		
		if(!(MC.world.getBlockEntity(
			hit.getBlockPos()) instanceof VaultBlockEntity vault))
		{
			event.cancel();
			return;
		}
		
		String displayItemName =
			vault.getSharedData().getDisplayItem().getName().getString();
		
		if(debuggingButton.isChecked())
		{
			ChatUtils.message("Vault display item > " + displayItemName);
			
		}
		
		if(!"HEAVY CORE".equalsIgnoreCase(displayItemName))
		{
			event.cancel();
			return;
		}
	}
}
