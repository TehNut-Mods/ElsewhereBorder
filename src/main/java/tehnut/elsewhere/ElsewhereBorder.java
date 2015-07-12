package tehnut.elsewhere;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tehnut.elsewhere.util.EventHandler;
import tehnut.elsewhere.util.Utils;

import java.io.File;

@Mod(modid = ModInformation.ID, name = ModInformation.NAME, version = ModInformation.VERSION, dependencies = ModInformation.DEPEND, acceptableRemoteVersions = "*")
public class ElsewhereBorder {

    public static Logger logger = LogManager.getLogger(ModInformation.NAME);

    @Mod.Instance
    public static ElsewhereBorder instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.init(new File(event.getModConfigurationDirectory(), ModInformation.ID + ".cfg"));

        Utils.buildBoundList();
        Utils.buildVIPList();

        FMLCommonHandler.instance().bus().register(new EventHandler());
    }
}
