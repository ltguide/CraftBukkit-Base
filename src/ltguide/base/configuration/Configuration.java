package ltguide.base.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ltguide.base.Base;
import ltguide.base.Debug;
import ltguide.base.data.Command;
import ltguide.base.data.ICommand;
import ltguide.base.data.IMessage;
import ltguide.base.data.Message;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.error.YAMLException;

public class Configuration extends YamlConfiguration {
	private final File file;
	protected final Base plugin;
	protected int[] oldVersion;
	
	public Configuration(final Base instance) {
		this(instance, "config.yml");
	}
	
	public Configuration(final Base instance, final String config) {
		plugin = instance;
		file = new File(plugin.getDataFolder(), config);
	}
	
	protected void loadConfig() {
		try {
			load(file);
		}
		catch (final FileNotFoundException e) {}
		catch (final IOException e) {
			plugin.logException(e, "cannot load " + file);
		}
		catch (final InvalidConfigurationException e) {
			if (e.getCause() instanceof YAMLException) plugin.severe("Config file " + file + " isn't valid! " + e.getCause());
			else if (e.getCause() == null || e.getCause() instanceof ClassCastException) plugin.severe("Config file " + file + " isn't valid!");
			else plugin.logException(e, "cannot load " + file + ": " + e.getCause().getClass());
		}
		
		final InputStream inStream = plugin.getResource(file.getName());
		if (inStream != null) setDefaults(YamlConfiguration.loadConfiguration(inStream));
	}
	
	public boolean upgradeConfig() {
		if (Debug.ON) Debug.info("upgradeConfig()");
		
		String old = "<none>";
		final String current = plugin.getDescription().getVersion();
		
		if (isSet("version-nomodify")) old = getString("version-nomodify");
		else {
			plugin.debug("writing default configuration to " + file);
			options().copyDefaults(true);
		}
		
		if (current.equals(old)) return false;
		
		oldVersion = getVersionInt(old);
		set("version-nomodify", current);
		
		return true;
	}
	
	public void saveConfig() {
		try {
			save(file);
		}
		catch (final IOException e) {
			plugin.logException(e, "could not save " + file);
		}
	}
	
	public void setDefaults(final IMessage[] messages, final ICommand[] commands) {
		options().copyDefaults(true);
		plugin.setDebug(getBoolean("debug"));
		
		for (final IMessage message : messages)
			plugin.messages.put(message.name(), new Message(plugin, message, getString("messages." + message.name().toLowerCase())));
		
		for (final ICommand command : commands) {
			final String path = "commands." + command.name().toLowerCase();
			
			plugin.commands.put(command.name(), new Command(plugin, command, getString(path + ".description"), getString(path + ".broadcast")));
		}
	}
	
	protected int[] getVersionInt(final String version) {
		final String[] split = version.split("\\.");
		final int[] num = new int[split.length];
		
		for (int i = 0; i < split.length; i++)
			try {
				num[i] = Integer.parseInt(split[i]);
			}
			catch (final NumberFormatException e) {
				num[i] = 0;
			}
		
		return num;
	}
	
	protected boolean versionCompare(final int[] old, final int[] current) {
		if (old.length != current.length) return false;
		
		for (int i = 0; i < current.length; i++)
			if (old[i] > current[i]) return false;
		
		return true;
	}
}
