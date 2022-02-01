package net.pixlies.core;

import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.pixlies.core.calendar.PixliesCalendar;
import net.pixlies.core.commands.CommandManager;
import net.pixlies.core.configuration.Config;
import net.pixlies.core.database.MongoManager;
import net.pixlies.core.handlers.HandlerManager;
import net.pixlies.core.handlers.RegisterHandlerManager;
import net.pixlies.core.listeners.ListenerManager;
import net.pixlies.core.localization.Lang;
import net.pixlies.core.modules.ModuleManager;
import net.pixlies.core.runnables.RunnableManager;
import net.pixlies.core.runnables.RunnableRegisterManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    @Getter private static Main instance;

    @Getter private MongoManager database;
    @Getter private HandlerManager handlerManager;
    @Getter private ModuleManager moduleManager;
    @Getter private CommandManager commandManager;
    @Getter private RunnableManager runnableManager;
    @Getter private PixliesCalendar calendar;

    @Getter private Config config;
    @Getter private Config calendarConfig;
    @Getter private Config settings;

    private RunnableRegisterManager runnableRegisterManager;

    @Override
    public void onEnable() {
        instance = this;

        // CONFIGURATION
        config = new Config(new File(getDataFolder().getAbsolutePath() + "/config.yml"), "config.yml");
        settings = new Config(new File(getDataFolder().getAbsolutePath() + "/settings.yml", "settings.yml"));
        calendarConfig = new Config(new File(getDataFolder().getAbsolutePath() + "/calendar.yml"), "calendar.yml");

        // LANGUAGE
        saveResource("languages/LANG_GER.yml", true);
        saveResource("languages/LANG_ENG.yml", true);
        saveResource("languages/LANG_FRA.yml", true);
        saveResource("languages/LANG_ARM.yml", true);
        saveResource("languages/LANG_LUX.yml", true);
        saveResource("languages/LANG_PER.yml", true);
        Lang.init();

        // HANDLERS
        moduleManager = new ModuleManager();
        handlerManager = new HandlerManager();
        runnableManager = new RunnableManager();
        new RegisterHandlerManager().registerAllHandlers();

        // DATABASE
        database = new MongoManager().init();

        // PIXLIES CALENDAR
        String[] date = calendarConfig.getString("date", "0/0/0").split("/");
        calendar = new PixliesCalendar(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
        calendar.startRunner();

        // LISTENERS & COMMANDS
        ListenerManager.registerAllListeners();
        commandManager = new CommandManager();

        // MODULES
        moduleManager.loadModules();

        // RUNNABLES
        runnableRegisterManager = new RunnableRegisterManager();
        runnableRegisterManager.runAll();

    }

    @Override
    public void onDisable() {
        runnableRegisterManager.stopAll();
        moduleManager.unloadModules();
        instance = null;
    }

}
