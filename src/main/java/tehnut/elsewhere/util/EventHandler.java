package tehnut.elsewhere.util;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import org.apache.commons.lang3.tuple.Triple;
import tehnut.elsewhere.ConfigHandler;

public class EventHandler {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        double coordX = event.player.posX;
        double coordY = event.player.posY;
        double coordZ = event.player.posZ;

        EntityPlayer player = event.player;
        int dimensionID = player.dimension;
        String outOfBounds = ConfigHandler.outOfBoundsMessage;

        if (!player.worldObj.isRemote) {
            for (Triple<Integer, Integer, Integer> triple : Utils.boundList) {
                if (triple.getLeft() == dimensionID && !Utils.shouldPlayerPass(player)) {
                    // Too far +X
                    if (coordX > triple.getMiddle()) {
                        player.setPositionAndUpdate((double) triple.getMiddle() - 1.5, coordY + .5, coordZ);
                        player.addChatComponentMessage(new ChatComponentText(outOfBounds));
                    }

                    // Too far -X
                    if (coordX < -triple.getMiddle()) {
                        player.setPositionAndUpdate((double) -triple.getMiddle() + 1.5, coordY + .5, coordZ);
                        player.addChatComponentMessage(new ChatComponentText(outOfBounds));
                    }

                    // Too far +Z
                    if (coordZ > triple.getRight()) {
                        player.setPositionAndUpdate(coordX, coordY + .5, (double) triple.getRight() - 1.5);
                        player.addChatComponentMessage(new ChatComponentText(outOfBounds));
                    }

                    // Too far -Z
                    if (coordZ < -triple.getRight()) {
                        player.setPositionAndUpdate(coordX, coordY + .5, (double) -triple.getRight() + 1.5);
                        player.addChatComponentMessage(new ChatComponentText(ConfigHandler.outOfBoundsMessage));
                    }
                }
            }
        }
    }
}