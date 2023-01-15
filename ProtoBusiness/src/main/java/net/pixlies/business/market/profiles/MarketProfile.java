package net.pixlies.business.market.profiles;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.pixlies.business.ProtoBusiness;
import net.pixlies.business.locale.MarketLang;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * Market profile.
 *
 * @author vyketype
 */
@Getter
@Setter
public class MarketProfile {
      private static final ProtoBusiness instance = ProtoBusiness.getInstance();
      private static final String PROFILES_PATH = instance.getDataFolder().getAbsolutePath() + "/profiles/";
      private static final Map<UUID, MarketProfile> CACHE = new HashMap<>();
      
      private final UUID uuid;
      
      @Setter(AccessLevel.PRIVATE)
      private List<UUID> blockedPlayers;
      
      private boolean restricted;
      
      @Getter(AccessLevel.NONE)
      private boolean hasJoinedBefore;
      
      private int buyOrdersMade;
      private int sellOrdersMade;
      private int tradesMade;
      private double moneySpent;
      private double moneyGained;
      private int itemsSold;
      private int itemsBought;
      
      public MarketProfile(UUID uuid) {
            this.uuid = uuid;
            blockedPlayers = new ArrayList<>();
            restricted = false;
            hasJoinedBefore = true;
            buyOrdersMade = 0;
            sellOrdersMade = 0;
            tradesMade = 0;
            moneySpent = 0;
            moneyGained = 0;
            itemsSold = 0;
            itemsBought = 0;
      }
      
      public boolean hasJoinedBefore() {
            return hasJoinedBefore;
      }
      
      public void tradeBlockPlayer(UUID uuid) {
            blockedPlayers.add(uuid);
      }
      
      public void unTradeBlockPlayer(UUID uuid) {
            blockedPlayers.remove(uuid);
      }
      
      public void sendNotification() {
            Player player = Bukkit.getPlayer(uuid);
            assert player != null;
            
            if (player.isOnline()) {
                  MarketLang.MARKET_NOTIFICATION.send(player);
                  player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100F, 1F);
            }
      }
      
      public void addBuy() {
            buyOrdersMade += 1;
      }
      
      public void addSell() {
            sellOrdersMade += 1;
      }
      
      public void addTrade() {
            tradesMade += 1;
      }
      
      public void addMoneySpent(double money) {
            moneySpent += money;
      }
      
      public void addMoneyGained(double money) {
            moneyGained += money;
      }
      
      public void addItemsSold(int items) {
            itemsSold += items;
      }
      
      public void addItemsBought(int items) {
            itemsBought += items;
      }
      
      public void save() {
            CACHE.put(uuid, this);
            String playerName = Objects.requireNonNull(Bukkit.getPlayer(uuid)).getName();
            instance.logInfo("The MarketProfile of " + playerName + " has been saved to the CACHE.");
      }
      
      public void backup() {
            String filename = uuid.toString() + ".yml";
      
            File file = new File(PROFILES_PATH + filename);
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            
            List<String> blockedList = new ArrayList<>();
            for (UUID uuid : blockedPlayers) {
                  blockedList.add(uuid.toString());
            }
      
            yaml.set("blockedPlayers", blockedList);
            yaml.set("restricted", restricted);
            yaml.set("hasJoinedBefore", hasJoinedBefore);
            yaml.set("buyOrdersMade", buyOrdersMade);
            yaml.set("sellOrdersMade", sellOrdersMade);
            yaml.set("tradesMade", tradesMade);
            yaml.set("moneySpent", moneySpent);
            yaml.set("moneyGained", moneyGained);
            yaml.set("itemsSold", itemsSold);
            yaml.set("itemsBought", itemsBought);
            
            try {
                  yaml.save(file);
            } catch (IOException ex) {
                  ex.printStackTrace();
                  instance.getLogger().log(Level.SEVERE, "Unable to save MarketProfile of " + uuid + ".");
            }
      
            String playerName = Bukkit.getOfflinePlayer(uuid).getName();
            instance.logInfo("The MarketProfile of " + playerName + " has been backed up to the files.");
      }
      
      // --------------------------------------------------------------------------------------------
      
      public static void backupAll() {
            CACHE.values().forEach(MarketProfile::backup);
      }
      
      private static MarketProfile getFromFiles(UUID uuid) {
            String filename = uuid.toString() + ".yml";
            
            File file = new File(PROFILES_PATH + filename);
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            
            List<UUID> blockedPlayers = new ArrayList<>();
            for (String string : yaml.getStringList("blockedPlayers")) {
                  blockedPlayers.add(UUID.fromString(string));
            }
            
            MarketProfile profile = new MarketProfile(uuid);
            profile.setRestricted(yaml.getBoolean("restricted"));
            profile.setHasJoinedBefore(yaml.getBoolean("hasJoinedBefore"));
            profile.setBlockedPlayers(blockedPlayers);
            profile.setBuyOrdersMade(yaml.getInt("buyOrdersMade"));
            profile.setSellOrdersMade(yaml.getInt("sellOrdersMade"));
            profile.setTradesMade(yaml.getInt("tradesMade"));
            profile.setMoneySpent(yaml.getDouble("moneySpent"));
            profile.setMoneyGained(yaml.getDouble("moneyGained"));
            profile.setItemsSold(yaml.getInt("itemsSold"));
            profile.setItemsBought(yaml.getInt("itemsBought"));
            
            profile.save();
            return profile;
      }
      
      public static MarketProfile get(UUID uuid) {
            // Get from CACHE
            if (CACHE.containsKey(uuid)) {
                  return CACHE.get(uuid);
            }
            
            // If the MarketProfile does not exist
            String filename = uuid.toString() + ".yml";
            if (!new File(PROFILES_PATH + filename).exists()) {
                  MarketProfile profile = new MarketProfile(uuid);
                  profile.save();
                  return profile;
            }
      
            // Get from files
            MarketProfile profile = getFromFiles(uuid);
            profile.save();
            return profile;
      }
}
