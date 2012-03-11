package ltguide.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ltguide.base.data.Command;
import ltguide.base.data.ICommand;
import ltguide.base.data.Message;
import ltguide.base.exceptions.CommandException;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Base extends JavaPlugin {
	public static final int bufferSize = 4 * 1024;
	private static Permission permission = null;
	private static Economy economy = null;
	
	public LinkedHashMap<String, Command> commands = new LinkedHashMap<String, Command>();
	public HashMap<String, Message> messages = new HashMap<String, Message>();
	private boolean debug = true;
	private Logger logger;
	private long startTime;
	public Command command;
	
	@Override
	public void onEnable() {
		logger = getLogger();
	}
	
	public void useEconomy() {
		economy = getProvider(Economy.class);
	}
	
	public void usePermission() {
		permission = getProvider(Permission.class);
	}
	
	public <T> T getProvider(final Class<T> clazz) {
		final RegisteredServiceProvider<T> rsp = getServer().getServicesManager().getRegistration(clazz);
		return rsp == null ? null : rsp.getProvider();
	}
	
	public void setDebug(final boolean _debug) {
		debug = _debug;
	}
	
	public void info(final String msg) {
		log(Level.INFO, msg);
	}
	
	public void warning(final String msg) {
		log(Level.WARNING, msg);
	}
	
	public void severe(final String msg) {
		log(Level.SEVERE, msg);
	}
	
	private void log(final Level level, final String msg) {
		logger.log(level, ChatColor.stripColor(msg));
	}
	
	public void logException(final Exception e, final String msg) {
		severe("---------------------------------------");
		if (!"".equals(msg)) severe("# " + msg);
		
		severe(e.toString());
		for (final StackTraceElement stack : e.getStackTrace())
			severe("\t" + stack.toString());
		
		severe("---------------------------------------");
	}
	
	public void configWarning(final ConfigurationSection cs, final String key, final Object value) {
		warning(" $ invalid setting: " + cs.getCurrentPath() + "." + key + " (" + value + ")");
	}
	
	public void debug(final String msg) {
		if (debug || Debug.ON) info(msg);
	}
	
	public void send(final CommandSender sender, final Message message) {
		send(sender, message.getText());
	}
	
	public void send(final CommandSender sender, final String msg) {
		if (sender instanceof Player) {
			sender.sendMessage(msg);
			info("->" + sender.getName() + " " + msg);
		}
		else log(Level.INFO, msg);
	}
	
	public void broadcast(final CommandSender sender) {
		broadcast(sender, command);
	}
	
	public void broadcast(final CommandSender sender, final Command command) {
		broadcast(sender, command.getBroadcast(), command.getMessage(sender.getName()));
	}
	
	public void broadcast(final CommandSender sender, final String permission, final String msg) {
		if (permission == null || "".equals(permission)) send(sender, msg);
		else getServer().broadcast(msg, permission);
	}
	
	public long startTime() {
		return startTime = System.nanoTime();
	}
	
	public String stopTime() {
		return stopTime(startTime);
	}
	
	public String stopTime(final long startTime) {
		return String.format("%.2fms", (System.nanoTime() - startTime) * 1e-6);
	}
	
	public String joinString(final Object[] objects, final String separator, final int first, final int last) {
		final StringBuilder sb = new StringBuilder(objects[first].toString());
		for (int i = first + 1; i < last; i++)
			sb.append(separator + objects[i].toString());
		
		return sb.toString();
	}
	
	public String joinPlayers(final List<Player> players) {
		final List<String> strings = new ArrayList<String>();
		for (final Player player : players)
			strings.add(player.getName());
		
		return joinString(strings.toArray(), ", ", 0, strings.size());
	}
	
	public static Economy getEconomy() {
		return economy;
	}
	
	public boolean hasAccount(final CommandSender sender) {
		return economy != null ? economy.hasAccount(sender.getName()) : false;
	}
	
	public boolean hasPermission(final CommandSender sender, final String arg) {
		if (permission != null) return permission.has(sender, arg);
		return sender.hasPermission(arg);
	}
	
	public Message getMessage(final String name) {
		return messages.get(name);
	}
	
	public String getMessage(final String name, final Object... args) {
		return messages.get(name).getText(args);
	}
	
	public Command getCmd(final String name) {
		return commands.get(name);
	}
	
	public void initCommand(final ICommand command, final CommandSender sender, final String label, final String[] args) throws CommandException {
		this.command = getCmd(command.name()).init(sender, label, args);
	}
	
	public boolean sendCommands(final CommandSender sender, final String label) {
		for (final Command command : commands.values())
			command.sendInfo(sender, label);
		
		return true;
	}
	/*public Command getCmd(final String cmd, final String subCmd) {
		return commands.get(cmd).get(subCmd);
	}
	
	public void initSubCommand(final ICommand command, final CommandSender sender, final String label, final String[] args) throws CommandException {
		this.command = getCmd("", command.name()).init(sender, label, args);
	}
	
	public boolean sendSubCommands(final CommandSender sender, final org.bukkit.command.Command c, final String label) {
		for (final Command command : commands.get(c.getName().toUpperCase()).values())
			command.sendInfo(sender, label);
		
		return true;
	}*/
}
