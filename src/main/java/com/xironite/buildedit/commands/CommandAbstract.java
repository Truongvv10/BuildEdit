package com.xironite.buildedit.commands;

import com.xironite.buildedit.services.PlayerSessionManager;
import com.xironite.buildedit.storage.configs.MessageConfig;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
    protected final PlayerSessionManager playerSessionManager;
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
    public CommandAbstract(JavaPlugin paramPlugin, PlayerSessionManager paramSession, MessageConfig paramMessageConfig, String paramName, String paramPermission, String paramSyntax, String paramDescription, CommandAbstract parentCommand) {
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

    public CommandAbstract(JavaPlugin paramPlugin, PlayerSessionManager paramPlayerSessionManager, MessageConfig paramMessageConfig, String paramName, String paramPermission, String paramSyntax, String paramDescription) {
        this(paramPlugin, paramPlayerSessionManager, paramMessageConfig, paramName, paramPermission,paramSyntax, paramDescription, null);
    }

    public CommandAbstract(JavaPlugin paramPlugin, PlayerSessionManager paramPlayerSessionManager, MessageConfig paramMessageConfig, String paramName, String paramPermission) {
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

    public abstract boolean execute(CommandSender sender, String[] args);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        // Check permission
        if (permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        // If we have args and the first arg matches a subcommand, delegate to that subcommand
        if (args.length > 0 && subCommands.containsKey(args[0].toLowerCase())) {
            CommandAbstract subCommand = subCommands.get(args[0].toLowerCase());
            // Create new args array without the subcommand name
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
            return subCommand.onCommand(sender, command, label, newArgs);
        }

        // Otherwise execute this command
        return execute(sender, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        // If we have args and the first arg matches a subcommand, delegate to that subcommand
        if (args.length > 1 && subCommands.containsKey(args[0].toLowerCase())) {
            CommandAbstract subCommand = subCommands.get(args[0].toLowerCase());
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
            return subCommand.onTabComplete(sender, command, label, newArgs);
        }

        // If we're completing the first argument and have subcommands
        if (args.length == 1 && !subCommands.isEmpty()) {
            String partialArg = args[0].toLowerCase();
            return subCommands.values().stream()
                    .map(CommandAbstract::getName)
                    .distinct()
                    .filter(name -> name.toLowerCase().startsWith(partialArg))
                    .toList();
        }

        // Otherwise get completions for this command
        return getTabCompletions(sender, args);
    }

    protected List<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>(); // Default to no completions
    }

    protected Component getUsage() {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        return miniMessage.deserialize(syntax + "\n" + description);
    }
    // endregion
}
