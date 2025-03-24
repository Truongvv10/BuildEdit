package com.xironite.buildedit.commands;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class CommandAbstract implements TabExecutor {

    // region Fields
    protected final JavaPlugin plugin;
    @Getter
    protected final String name;
    @Getter
    protected final String permission;
    @Getter
    protected final String description;
    @Getter
    protected final String syntax;
    @Getter
    protected final Map<String, CommandAbstract> subCommands;
    @Getter @Setter
    protected CommandAbstract parentCommand;
    // endregion

    // region Constructors
    public CommandAbstract(JavaPlugin paramPlugin, String paramName, String paramPermission, String paramDescription, String paramSyntax, CommandAbstract parentCommand) {
        this.plugin = paramPlugin;
        this.name = paramName;
        this.permission = paramPermission;
        this.description = paramDescription;
        this.syntax = paramSyntax;
        this.parentCommand = parentCommand;
        this.subCommands = new HashMap<>();
    }

    public CommandAbstract(JavaPlugin paramPlugin, String paramName, String paramPermission, String paramDescription, String paramSyntax) {
        this(paramPlugin, paramName, paramPermission, paramDescription, paramSyntax, null);
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
        plugin.getLogger().info("Permission: " + label);
        if (permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        // If we have args and the first arg matches a subcommand, delegate to that subcommand
        plugin.getLogger().info("Args: " + String.join(" ", args));
        if (args.length > 0 && subCommands.containsKey(args[0].toLowerCase())) {
            CommandAbstract subCommand = subCommands.get(args[0].toLowerCase());
            // Create new args array without the subcommand name
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
            return subCommand.execute(sender, newArgs);
        }

        // Otherwise execute this command
        return execute(sender, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> completions = new ArrayList<>();

        // If we're completing the first argument and have subcommands, suggest them
        if (args.length == 1) {
            String partialArg = args[0].toLowerCase();
            for (Map.Entry<String, CommandAbstract> entry : subCommands.entrySet()) {
                if (entry.getKey().toLowerCase().startsWith(partialArg)) {
                    // Only add each subcommand once (avoid duplicates from aliases)
                    if (!completions.contains(entry.getValue().getName())) {
                        completions.add(entry.getValue().getName());
                    }
                }
            }
            return completions;
        }

        // If we have args and the first arg matches a subcommand, delegate tab completion
        if (args.length > 1 && subCommands.containsKey(args[0].toLowerCase())) {
            CommandAbstract subCommand = subCommands.get(args[0].toLowerCase());
            // Create new args array without the subcommand name
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
            return subCommand.getTabCompletions(sender, newArgs);
        }

        // Otherwise get completions for this command
        return getTabCompletions(sender, args);
    }

    protected List<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>(); // Default to no completions
    }

    protected boolean isSenderPlayer(@NotNull CommandSender sender) {
        return sender instanceof Player;
    }
    // endregion
}
