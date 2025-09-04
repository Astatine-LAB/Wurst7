/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.util.ChatUtils;

@SearchTags({"vault", "mace", "HEAVY CORE", "copper"})
public class VaultOpenerHack extends Hack
{
	private final CheckboxSetting debuggingButton =
		new CheckboxSetting("Debugging", "Check up Logic", false);
	
    private String targetItemName = "HEAVY CORE";

	public VaultOpenerHack()
	{
		super("VaultOpener");
		addSetting(debuggingButton);
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	protected void onEnable()
	{
	}

	@Override
	protected void onDisable()
	{
	}

    public void detectedTargetItem(BlockPos pos, String displayItemName)
    {
        if(debuggingButton.isChecked())
            ChatUtils.message("displayItemName -> " + displayItemName);

        if (targetItemName.equalsIgnoreCase(displayItemName))
            MC.execute(() -> clickEvent(pos));
    }

    private void clickEvent(BlockPos pos) {
        Vec3d hitPos = Vec3d.ofCenter(pos);
        Direction face =
                Direction.getFacing(MC.player.getEyePos()).getOpposite();
        BlockHitResult hit =
                new BlockHitResult(hitPos, face, pos, false);
        PlayerInteractBlockC2SPacket pkt =
                new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hit, 0);

        MC.player.networkHandler.sendPacket(pkt);
    }
}
