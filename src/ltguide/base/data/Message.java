package ltguide.base.data;

import java.util.IllegalFormatException;

import ltguide.base.Base;

import org.bukkit.ChatColor;

public class Message {
	private final Base plugin;
	private final String name;
	private final boolean usesPrefix;
	private String text = "";
	private Object[] args;
	
	public Message(final Base instance, final IMessage message, final String text) {
		plugin = instance;
		
		name = message.name();
		usesPrefix = message.usesPrefix();
		this.text = text;
	}
	
	public String getText() {
		try {
			return String.format(((usesPrefix ? plugin.messages.get("PREFIX").plainText() + " " : "") + text).replaceAll("(?i)&([0-F])", "\u00A7$1"), args);
		}
		catch (final IllegalFormatException e) {
			return ChatColor.RED + "Error in " + name + " translation! (" + e.getMessage() + ")";
		}
	}
	
	private String plainText() {
		return text;
	}
	
	public String getText(final Object[] args) {
		return setArgs(args).getText();
	}
	
	private Message setArgs(final Object[] args) {
		this.args = args;
		return this;
	}
}
