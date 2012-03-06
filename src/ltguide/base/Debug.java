package ltguide.base;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public final class Debug {
	public static final boolean ON = true;
	private static Logger logger;
	
	public static void init(final JavaPlugin instance) {
		logger = instance.getServer().getLogger();
	}
	
	public static void info(final String msg) {
		logger.info("# " + msg);
	}
}