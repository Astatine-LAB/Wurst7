/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.RightClickListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.util.ChatUtils;

@SearchTags({"vault", "mace", "HEAVY CORE", "copper"})
public class VaultOpenerHack extends Hack implements RightClickListener
{
    private final CheckboxSetting debuggingButton = new CheckboxSetting(
        "Debugging", "Log vault info to chat.", false);

    private volatile BlockPos targetVaultPos = null;
    private volatile boolean shouldClick = false;
    private boolean hasDetectedMace = false;

    private Thread clickThread;

    public VaultOpenerHack()
    {
        super("VaultOpener");
        addSetting(debuggingButton);
        setCategory(Category.MOVEMENT);
    }

    @Override
    protected void onEnable()
    {
        EVENTS.add(RightClickListener.class, this);

        clickThread = new Thread(() -> {
            while (isEnabled())
            {
                if (!shouldClick)
                    continue;

                if (targetVaultPos == null)
                    continue;

                clickVault();
            }
        });
        clickThread.start();
    }

    @Override
    protected void onDisable()
    {
        EVENTS.remove(RightClickListener.class, this);
        if (clickThread != null && clickThread.isAlive())
        {
            clickThread.interrupt();
        }
        clickThread = null;
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
		
		if("HEAVY CORE".equalsIgnoreCase(displayItemName))
		{
            if (hasDetectedMace) {
                targetVaultPos = vault.getPos();
                shouldClick = true;
            }
        
            if(debuggingButton.isChecked()) {
    			ChatUtils.message("Has Dectected Mace? > " + hasDetectedMace);
            }
            hasDetectedMace = true;
		} 

        event.cancel();
        return;
    }

    private void clickVault()
    {
        Vec3d hitPos = Vec3d.ofCenter(targetVaultPos);
        Direction face = Direction.getFacing(MC.player.getEyePos()).getOpposite();
        BlockHitResult hit = new BlockHitResult(hitPos, face, targetVaultPos, false);
        PlayerInteractBlockC2SPacket pkt =
        new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hit, 0);
        
        MC.player.networkHandler.sendPacket(pkt);

        if (debuggingButton.isChecked())
            ChatUtils.message("click");

        targetVaultPos = null;
        shouldClick = false;
        hasDetectedMace = false;
    }
}