package com.xironite.buildedit.commands.deprecate;

import com.xironite.buildedit.models.enums.ConfigSection;
import com.xironite.buildedit.services.SessionManager;
import com.xironite.buildedit.storage.configs.MessageConfig;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class CommandAbstract implements TabExecutor {

    // region Fields
    @Getter
    protected final JavaPlugin plugin;
    @Getter
    protected final SessionManager playerSessionManager;
    @Getter
    protected final MessageConfig messageConfig;
    @Getter
    protected final String name;
    @Getter
    protected final String permission;
    @Getter
    protected final String syntax;
    @Getter
    protected final String description;
    @Getter
    protected final Map<String, CommandAbstract> subCommands;
    @Getter @Setter
    protected CommandAbstract parentCommand;
    // endregion

    // region Constructors
    public CommandAbstract(JavaPlugin paramPlugin, SessionManager paramSession, MessageConfig paramMessageConfig, String paramName, String paramPermission, String paramSyntax, String paramDescription, CommandAbstract parentCommand) {
        this.plugin = paramPlugin;
        this.playerSessionManager = paramSession;
        this.messageConfig = paramMessageConfig;
        this.name = paramName;
        this.permission = paramPermission;
        this.syntax = paramSyntax.isEmpty() ?
                (parentCommand == null ? "" : parentCommand.getSyntax()) : paramSyntax;
        this.description = paramDescription.isEmpty() ?
                (parentCommand == null ? "" : parentCommand.getDescription()) : paramDescription;
        this.subCommands = new HashMap<>();
        this.setParentCommand(parentCommand);
    }

    public CommandAbstract(JavaPlugin paramPlugin, SessionManager paramPlayerSessionManager, MessageConfig paramMessageConfig, String paramName, String paramPermission, String paramSyntax, String paramDescription) {
        this(paramPlugin, paramPlayerSessionManager, paramMessageConfig, paramName, paramPermission,paramSyntax, paramDescription, null);
    }

    public CommandAbstract(JavaPlugin paramPlugin, SessionManager paramPlayerSessionManager, MessageConfig paramMessageConfig, String paramName, String paramPermission) {
        this(paramPlugin, paramPlayerSessionManager, paramMessageConfig, paramName, paramPermission,"", "", null);
    }
    // endregion

    // region Methods
    public void register() {
        if (parentCommand == null) {
            Objects.requireNonNull(plugin.getCommand(name)).setExecutor(this);
        } else {
            throw new IllegalStateException("Subcommands can not be registered. (" + name + ")");
        }
    }

    public void addSubCommand(CommandAbstract subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
        subCommand.setParentCommand(this);
    }

    public abstract boolean onExecute(CommandSender sender, Command cmd, String label, String[] args);

    protected List<String> onTabbing(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList(); // Default implementation returns empty list
    }

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        // Check permission
        if (!hasPermission(sender)) return true;

        // If we have args and the first arg matches a subcommand, delegate to that subcommand
        if (args.length > 0 && subCommands.containsKey(args[0].toLowerCase())) {
            CommandAbstract subCommand = subCommands.get(args[0].toLowerCase());
            String[] newArgs = new String[args.length - 1]; // Create new args array without the subcommand name
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
            return subCommand.onCommand(sender, command, label, newArgs);
        }

        // Otherwise execute this command
        return onExecute(sender, command, label, args);
    }

    @Override
    public final @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        // If we have args and the first arg matches a subcommand, delegate to that subcommand
        if (args.length > 1 && subCommands.containsKey(args[0].toLowerCase())) {
            CommandAbstract subCommand = subCommands.get(args[0].toLowerCase());
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
            return subCommand.onTabComplete(sender, command, label, newArgs);
        }

        // If we're completing the first argument and have subcommands
        if (args.length == 1) {
            List<String> result = new ArrayList<>();

            // Add matching subcommand names if we have any
            if (!subCommands.isEmpty()) {
                String partialArg = args[0].toLowerCase();
                List<String> subCommandNames = subCommands.values().stream()
                        .map(CommandAbstract::getName)
                        .filter(name -> name.toLowerCase().startsWith(partialArg))
                        .toList();
                result.addAll(subCommandNames);
            }

            // Also add this command's specific completions
            List<String> specificCompletions = onTabbing(sender, command, label, args);
            if (specificCompletions != null && !specificCompletions.isEmpty()) {
                result.addAll(specificCompletions);
            }

            return result;
        }

        // Otherwise get completions for this command
        return onTabbing(sender, command, label, args);
    }

    protected final Component getUsage() {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        return miniMessage.deserialize(syntax + "\n" + description);
    }

    protected final boolean hasPermission(CommandSender player) {
        if (player instanceof Player p) {
            if (!(permission != null && player.hasPermission(permission))) {
                p.sendMessage(messageConfig.getComponent(ConfigSection.ACTION_NO_PERMISSION));
                return false;
            } else return true;
        } else return true;
    }

    protected final boolean hasPermission(Player player) {
        if (!(permission != null && player.hasPermission(permission))) {
            player.sendMessage(messageConfig.getComponent(ConfigSection.ACTION_NO_PERMISSION));
            return false;
        } else return true;
    }

    protected final boolean isPlayerOnline(String player) {
        Player target = Bukkit.getPlayer(player);
        if (target == null) return false;
        return target.isOnline();
    }

    protected final boolean isPlayerOnline(@Nullable Player player) {
        if (player == null) return false;
        return player.isOnline();
    }
    // endregion
}
