package tehnut.elsewhere;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ConfigHandler {

    public static Configuration config;

    public static String general = "general";

    public static String[] dimBoundsDefault = { "0:2000x2000", "1:2000x2000", "-1:250x250" };
    public static String[] dimBounds;
    public static String[] vipListDefault = {};
    public static String[] vipList;
    public static String outOfBoundsMessage;
    public static boolean allowOPBypass;

    public static void init(File file) {
        config = new Configuration(file);
        syncConfig();
        config.load();
    }

    public static void syncConfig() {
        dimBounds = config.getStringList("dimBounds", general, dimBoundsDefault, "Dimension specific boundaries.\n" +
                "Syntax is DimensionID:XxZ\n" +
                "DimensionID = ID of the dimension you're specifying a bound for. IE: 0 for Overworld\n" +
                "X = Maximum X Coordinate a player can travel to. This also goes negative. IE: 1000 will allow the player to travel between 1000 and -1000 on the X axis\n" +
                "Z = Maximum Z Coordinate a player can travel to. This also goes negative. IE: 1000 will allow the player to travel between 1000 and -1000 on the Z axis\n");
        vipList = config.getStringList("vipList", general, vipListDefault, "Players who are allowed to bypass the world bounds. Uses the player's UUID, not their display name.");
        outOfBoundsMessage = config.getString("outOfBoundsMessage", general, "Out of bounds, soldier!", "Message to send players when they have crossed the border.\n");
        allowOPBypass = config.getBoolean("allowOPBypass", general, false, "Allows a server operator to bypass the world bounds.");

        config.save();
    }
}
