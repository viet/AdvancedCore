package com.Ben12345rocks.AdvancedCore.CommandAPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Ben12345rocks.AdvancedCore.AdvancedCoreHook;
import com.Ben12345rocks.AdvancedCore.UserManager.UserManager;
import com.Ben12345rocks.AdvancedCore.Util.Misc.ArrayUtils;
import com.Ben12345rocks.AdvancedCore.Util.Misc.PlayerUtils;
import com.Ben12345rocks.AdvancedCore.Util.Misc.StringUtils;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandHandler.
 */
public abstract class CommandHandler {

	AdvancedCoreHook plugin = AdvancedCoreHook.getInstance();

	@Getter
	@Setter
	private String[] args;

	@Getter
	@Setter
	private String perm;

	@Getter
	@Setter
	private String helpMessage;

	@Getter
	@Setter
	private boolean allowConsole = true;

	@Getter
	@Setter
	private boolean advancedCoreCommand = false;

	private boolean ignoreNumberCheck = false;

	public CommandHandler() {
	}

	public CommandHandler withArgs(String... args) {
		this.args = args;
		return this;
	}

	public CommandHandler withPerm(String perm) {
		this.perm = perm;
		return this;
	}

	public CommandHandler withHelpMessage(String helpMessage) {
		this.helpMessage = helpMessage;
		return this;
	}

	public CommandHandler noConsole() {
		this.allowConsole = false;
		return this;
	}

	/**
	 * Instantiates a new command handler.
	 *
	 * @param args
	 *            the args
	 * @param perm
	 *            the perm
	 */
	public CommandHandler(String[] args, String perm) {
		this.args = args;
		this.perm = perm;
		helpMessage = "Unknown Help Message";
	}

	/**
	 * Instantiates a new command handler.
	 *
	 * @param args
	 *            the args
	 * @param perm
	 *            the perm
	 * @param helpMessage
	 *            the help message
	 */
	public CommandHandler(String[] args, String perm, String helpMessage) {
		this.args = args;
		this.perm = perm;
		this.helpMessage = helpMessage;
	}

	/**
	 * Instantiates a new command handler.
	 *
	 * @param args
	 *            the args
	 * @param perm
	 *            the perm
	 * @param helpMessage
	 *            the help message
	 * @param allowConsole
	 *            the allow console
	 */
	public CommandHandler(String[] args, String perm, String helpMessage, boolean allowConsole) {
		this.args = args;
		this.perm = perm;
		this.helpMessage = helpMessage;
		this.allowConsole = allowConsole;
	}

	/**
	 * Adds the tab complete option.
	 *
	 * @param toReplace
	 *            the to replace
	 * @param options
	 *            the options
	 */
	@Deprecated
	public void addTabCompleteOption(String toReplace, ArrayList<String> options) {
		TabCompleteHandler.getInstance().addTabCompleteOption(toReplace, options);
	}

	/**
	 * Adds the tab complete option.
	 *
	 * @param toReplace
	 *            the to replace
	 * @param options
	 *            the options
	 */
	@Deprecated
	public void addTabCompleteOption(String toReplace, String... options) {
		addTabCompleteOption(toReplace, ArrayUtils.getInstance().convert(options));
	}

	/**
	 * Args match.
	 *
	 * @param arg
	 *            the arg
	 * @param i
	 *            the i
	 * @return true, if successful
	 */
	public boolean argsMatch(String arg, int i) {
		if (i < args.length) {
			String[] cmdArgs = args[i].split("&");
			for (String cmdArg : cmdArgs) {
				if (arg.equalsIgnoreCase(cmdArg)) {
					return true;
				}

				for (String str : TabCompleteHandler.getInstance().getTabCompleteReplaces()) {
					if (str.equalsIgnoreCase(cmdArg)) {
						return true;
					}
				}
			}
			// plugin.debug("Tab: "
			// + Utils.getInstance().makeStringList(
			// Utils.getInstance().convert(
			// tabCompleteOptions.keySet())) + " "
			// + args[i]);
			for (String str : TabCompleteHandler.getInstance().getTabCompleteReplaces()) {
				if (str.equalsIgnoreCase(args[i])) {
					return true;
				}
			}
			return false;
		} else if (args[args.length - 1].equalsIgnoreCase("(list)")) {
			return true;
		}
		return false;
	}

	/**
	 * Execute.
	 *
	 * @param sender
	 *            the sender
	 * @param args
	 *            the args
	 */
	public abstract void execute(CommandSender sender, String[] args);

	/**
	 * Gets the help line.
	 *
	 * @param command
	 *            the command
	 * @return the help line
	 */
	public TextComponent getHelpLine(String command) {
		String line = plugin.getOptions().getHelpLine();

		String commandText = getHelpLineCommand(command);
		line = line.replace("%Command%", commandText);
		if (getHelpMessage() != "") {
			line = line.replace("%HelpMessage%", getHelpMessage());
		}
		TextComponent txt = StringUtils.getInstance().stringToComp(line);
		txt.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandText));
		txt.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder(getHelpMessage()).color(ChatColor.AQUA).create()));
		return txt;

	}

	/**
	 * Gets the help line command.
	 *
	 * @param command
	 *            the command
	 * @return the help line command
	 */
	public String getHelpLineCommand(String command) {
		String commandText = command;
		for (String arg1 : args) {
			int count = 1;
			for (String arg : arg1.split("&")) {
				if (count == 1) {
					commandText += " " + arg;
				} else {
					commandText += "/" + arg;
				}
				count++;
			}
		}
		return commandText;
	}

	public ArrayList<String> getTabCompleteOptions(CommandSender sender, String[] args, int argNum,
			ConcurrentHashMap<String, ArrayList<String>> tabCompleteOptions) {
		Set<String> cmds = new HashSet<String>();
		if (hasPerm(sender)) {
			CommandHandler commandHandler = this;

			String[] cmdArgs = commandHandler.getArgs();
			if (cmdArgs.length > argNum) {
				boolean argsMatch = true;
				for (int i = 0; i < argNum; i++) {
					if (args.length >= i) {
						if (!commandHandler.argsMatch(args[i], i)) {
							argsMatch = false;
						}
					}
				}

				if (argsMatch) {
					String[] cmdArgsList = cmdArgs[argNum].split("&");

					for (String arg : cmdArgsList) {
						// plugin.debug(arg);
						boolean add = true;
						for (Entry<String, ArrayList<String>> entry : tabCompleteOptions.entrySet()) {
							if (arg.equalsIgnoreCase(entry.getKey())) {
								add = false;
								cmds.addAll(entry.getValue());
							}
						}
						if (!cmds.contains(arg) && add) {
							cmds.add(arg);
						}
					}

				}

			}
		}

		ArrayList<String> options = ArrayUtils.getInstance().convert(cmds);

		Collections.sort(options, String.CASE_INSENSITIVE_ORDER);

		return options;
	}

	public boolean hasArg(String arg) {
		for (String str : getArgs()) {
			if (str.equalsIgnoreCase(arg)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasPerm(CommandSender sender) {
		/*
		 * boolean hasPerm = false;
		 * if (!perm.equals("")) { for (String perm : this.perm.split("\\|")) { if
		 * (sender.hasPermission(perm)) { hasPerm = true; } } } else { hasPerm = true; }
		 * return hasPerm;
		 */
		return PlayerUtils.getInstance().hasEitherPermission(sender, getPerm());
	}

	public CommandHandler ignoreNumberCheck() {
		ignoreNumberCheck = true;
		return this;
	}

	public boolean isPlayer(CommandSender sender) {
		return sender instanceof Player;
	}

	/**
	 * Run command.
	 *
	 * @param sender
	 *            the sender
	 * @param args
	 *            the args
	 * @return true, if successful
	 */
	public boolean runCommand(CommandSender sender, String[] args) {
		if (args.length >= this.args.length) {
			if (this.args.length != args.length && !hasArg("(list)")) {
				return false;
			}
			for (int i = 0; i < args.length && i < this.args.length; i++) {
				if (!argsMatch(args[i], i)) {
					return false;
				}
				if (this.args[i].equalsIgnoreCase("(number)")) {
					if (!ignoreNumberCheck && !StringUtils.getInstance().isInt(args[i])) {
						sender.sendMessage(StringUtils.getInstance()
								.colorize(plugin.getOptions().getFormatNotNumber().replace("%arg%", args[i])));
						return true;
					}
				} else if (this.args[i].equalsIgnoreCase("Player")) {
					if (args[i].equalsIgnoreCase("@p")) {
						args[i] = sender.getName();
					} else if (args[i].equalsIgnoreCase("@r")) {
						args[i] = PlayerUtils.getInstance().getRandomOnlinePlayer().getName();
					}
				}
			}
			if (!(sender instanceof Player) && !allowConsole) {
				sender.sendMessage(StringUtils.getInstance().colorize("&cMust be a player to do this"));
				return true;
			}

			if (!hasPerm(sender)) {
				sender.sendMessage(StringUtils.getInstance().colorize(plugin.getOptions().getFormatNoPerms()));
				plugin.getPlugin().getLogger().log(Level.INFO,
						sender.getName() + " was denied access to command, required permission: " + perm);
				return true;
			}

			Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin.getPlugin(), new Runnable() {

				@Override
				public void run() {
					execute(sender, args);
				}
			});

			return true;
		}
		return false;
	}

	public void sendMessage(CommandSender sender, ArrayList<String> msg) {
		sender.sendMessage(ArrayUtils.getInstance().convert(ArrayUtils.getInstance().colorize(msg)));
	}

	public void sendMessage(CommandSender sender, String msg) {
		sender.sendMessage(StringUtils.getInstance().colorize(msg));
	}

	public void sendMessageJson(CommandSender sender, ArrayList<TextComponent> comp) {
		if (isPlayer(sender)) {
			Player player = (Player) sender;
			UserManager.getInstance().getUser(player).sendJson(comp);
		} else {
			sender.sendMessage(ArrayUtils.getInstance().convert(ArrayUtils.getInstance().comptoString(comp)));
		}
	}

	public void sendMessageJson(CommandSender sender, TextComponent comp) {
		if (isPlayer(sender)) {
			Player player = (Player) sender;
			UserManager.getInstance().getUser(player).sendJson(comp);
		} else {
			sender.sendMessage(StringUtils.getInstance().compToString(comp));
		}
	}

}