package tehnut.elsewhere.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import tehnut.elsewhere.ConfigHandler;
import tehnut.elsewhere.ElsewhereBorder;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<Triple<Integer, Integer, Integer>> boundList = new ArrayList<Triple<Integer, Integer, Integer>>();

    public static void buildBoundList() {
        for (String bound : ConfigHandler.dimBounds) {
            String[] dimSplit = bound.split(":");
            String[] boundSplit = dimSplit[1].split("x");

            int dimensionID = Integer.parseInt(dimSplit[0]);
            int maxX = Integer.parseInt(boundSplit[0]);
            int maxZ = Integer.parseInt(boundSplit[1]);

//            ElsewhereBorder.logger.info(bound + "---" + dimSplit[0] + "---" + dimSplit[1] + "---" + boundSplit[0] + "---" + boundSplit[1]);

            boundList.add(new MutableTriple<Integer, Integer, Integer>(dimensionID, maxX, maxZ));
        }
    }

    public static boolean shouldPlayerPass(EntityPlayer player) {
//        return isPlayerOP(player) && ConfigHandler.ignoreOP;
        return false;
    }

    private static boolean isPlayerOP(EntityPlayer player) {
        return MinecraftServer.getServer().getConfigurationManager().func_152596_g(player.getGameProfile());
    }
}
