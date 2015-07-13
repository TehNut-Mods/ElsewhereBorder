package tehnut.elsewhere.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import org.apache.commons.lang3.tuple.Pair;

import tehnut.elsewhere.ConfigHandler;

import com.google.common.collect.Maps;

public class Utils {

    public static Map<Integer, Pair<Integer, Integer>> bounds = Maps.newHashMap();
    public static List<String> vipList = new ArrayList<String>();

    public static void buildBoundList() {
        for (String bound : ConfigHandler.dimBounds) {
            String[] dimSplit = bound.split(":");
            String[] boundSplit = dimSplit[1].split("x");

            int dimensionID = Integer.parseInt(dimSplit[0]);
            int maxX = Integer.parseInt(boundSplit[0]);
            int maxZ = Integer.parseInt(boundSplit[1]);

            bounds.put(dimensionID, Pair.of(maxX, maxZ));
        }
    }

    public static void buildVIPList() {
        for (String vip : ConfigHandler.vipList)
            vipList.add(vip);
    }

    public static boolean shouldPlayerPass(EntityPlayer player) {
        return (isPlayerOP(player) && ConfigHandler.allowOPBypass) || vipList.contains(player.getUniqueID().toString());
    }

    private static boolean isPlayerOP(EntityPlayer player) {
        return MinecraftServer.getServer().getConfigurationManager().func_152596_g(player.getGameProfile());
    }
}
