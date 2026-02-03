package betterfoliage;

import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.render.generator.GeneratorPack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.simple.SimpleLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

import java.io.File;
import java.io.PrintStream;
import java.util.Properties;

@Mod(modid = BetterFoliage.MODID,
	 version = BetterFoliage.VERSION,
	 name = BetterFoliage.NAME,
	 dependencies = "required-after:fermiumbooter",
	 acceptableRemoteVersions = "*",
	 clientSideOnly = true
)
public class BetterFoliage {
	public static final String MODID = "betterfoliage";
	public static final String VERSION = "2.5.0";
	public static final String NAME = "RLFoliage";
	
	public static Logger LOGGER;
	public static Logger LOGGER_DETAIL;
	
	@Mod.Instance(BetterFoliage.MODID)
	public static BetterFoliage INSTANCE;
	
	public BetterFoliage() {
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			LOGGER = LogManager.getLogger("RLFoliage");
			File logFile = new File(".", "logs/betterfoliage.log");
			try {
				logFile.getParentFile().mkdirs();
				if(!logFile.exists()) {
					logFile.createNewFile();
				}
				LOGGER_DETAIL = new SimpleLogger(
						"RLFoliageDetailed",
						Level.DEBUG,
						false, false, true, false,
						"yyyy-MM-dd HH:mm:ss",
						null,
						new PropertiesUtil(new Properties()),
						new PrintStream(logFile)
				);
			}
			catch(Exception ex) {
				LOGGER.log(Level.ERROR, "RLFoliage failed to create custom logger", ex);
				LOGGER_DETAIL = LOGGER;
			}
			
			GeneratorPack.GENERATOR_PACK.inject();
			LOGGER.log(Level.INFO, "RLFoliage Generated Resources Pack injected");
			LOGGER_DETAIL.log(Level.INFO, "RLFoliage Generated Resources Pack injected");
		}
	}
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			ForgeConfigHandler.refreshConfig();
			BetterFoliageClient.init();
			LOGGER.log(Level.INFO, "RLFoliage Client initialized");
			LOGGER_DETAIL.log(Level.INFO, "RLFoliage Client initialized");
		}
	}
}