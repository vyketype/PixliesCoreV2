package net.pixlies.business.commands.market;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import net.pixlies.business.ProtoBusinesss;
import net.pixlies.business.handlers.impl.MarketHandler;
import net.pixlies.business.locale.MarketLang;
import net.pixlies.business.market.Challenge;
import org.bukkit.entity.Player;

/**
 * Challenge command.
 *
 * @author vyketype
 */
@CommandAlias("challenge|challenges")
@CommandPermission("pixlies.business.challenge")
@Description("Get a list of all market challenges")
public class ChallengeCommand extends BaseCommand {
      private static final ProtoBusinesss instance = ProtoBusinesss.getInstance();
      private final MarketHandler marketHandler = instance.getHandlerManager().getHandler(MarketHandler.class);
      
      @Default
      public void onChallenge(Player player) {
            MarketLang.CHALLENGE_LIST.send(player);
            for (Challenge c : Challenge.values()) {
                  if (marketHandler.getChallenges().containsEntry(player.getUniqueId().toString(), c)) {
                        MarketLang.CHALLENGE_COMPLETE_FORMAT.send(player, "%CHALLENGE%;" + c.getMessage(player));
                  } else {
                        MarketLang.CHALLENGE_INCOMPLETE_FORMAT.send(player, "%CHALLENGE%;" + c.getMessage(player));
                  }
            }
      }
}
