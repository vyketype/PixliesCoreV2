package net.pixlies.business.commands.market;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import net.pixlies.business.locale.MarketLang;
import net.pixlies.business.market.OrderBook;
import net.pixlies.business.market.OrderItem;
import net.pixlies.business.util.preconditions.CommandPreconditions;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Price command.
 *
 * @author vyketype
 */
@CommandAlias("price|prix")
@CommandPermission("pixlies.business.price")
@Description("Retrieve the best prices of the held item")
public class PriceCommand extends BaseCommand {
      @Default
      public void onPrice(Player player) {
            Material mat = player.getInventory().getItemInMainHand().getType();
            
            // If the material is air
            if (CommandPreconditions.isPlayerHoldingAir(player, mat))
                  return;
            
            OrderItem item = OrderItem.getFromMaterial(mat);
            
            // If the item is not on the market
            if (!CommandPreconditions.doesMarketItemExist(player, item))
                  return;
      
            assert item != null;
            OrderBook book = OrderBook.get(item);
            
            // Send price summaries
            MarketLang.PRICE_SUMMARY.send(player, "%ITEM%;" + item.getName());
            MarketLang.PRICE_BEST_BUY.send(
                    player,
                    "%PRICE%;" + book.getLowestBuyPrice(player.getUniqueId()),
                    "%AMOUNT%;" + book.getBuyOrders().size()
            );
            MarketLang.PRICE_BEST_SELL.send(
                    player,
                    "%PRICE%;" + book.getHighestSellPrice(player.getUniqueId()),
                    "%AMOUNT%;" + book.getSellOrders().size()
            );
      }
}
