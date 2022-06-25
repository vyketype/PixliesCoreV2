package net.pixlies.business.commands;

import co.aikar.commands.BaseCommand;
import com.google.common.collect.ImmutableList;
import net.pixlies.business.commands.impl.*;
import net.pixlies.core.Main;

public class CommandManager {

    private final ImmutableList<BaseCommand> commands = ImmutableList.of(
            new MarketCommand(),
            new PriceCommand(),
            new TariffCommand(),
            new ChallengeCommand()
    );

    public void registerAllCommands() {
        commands.forEach(command -> Main.getInstance().getCommandManager().register(command, true));
    }

    public void unregisterAllCommands() {
        commands.forEach(command -> Main.getInstance().getCommandManager().unregister(command));
    }

}
