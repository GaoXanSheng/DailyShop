package io.github.divios.dailyShop.lorestategy;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.XSymbols;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.utils.PriceWrapper;
import io.github.divios.jtext.wrappers.Template;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dPrice;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.priceModifiers.priceModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class shopItemsLore implements loreStrategy {

    private ItemStack itemToApplyLore;
    private Player player;
    private dShop shop;

    public shopItemsLore() {
    }

    @Override
    public ItemStack applyLore(ItemStack item, Object... data) {
        this.itemToApplyLore = item;
        player = (data.length) >= 1 ? (Player) data[0] : null;
        shop = (data.length >= 2) ? (dShop) data[1] : null;
        return addLoreToItem();
    }

    private ItemStack addLoreToItem() {
        if (itemHasStock())
            itemToApplyLore = applyStockLore();
        return applyDefaultLore();
    }

    private boolean itemHasStock() {
        return dItem.of(itemToApplyLore).hasStock() && player != null;
    }

    private ItemStack applyStockLore() {
        return ItemBuilder.of(itemToApplyLore)
                .addLore("")
                .addLore(Lang.DAILY_ITEMS_STOCK.getAsString(player) + getStockForPlayer());
    }

    private ItemStack applyDefaultLore() {
        return ItemBuilder.of(itemToApplyLore)
                .addLore(getDefaultLore());
    }

    private String getStockForPlayer() {
        if (playerHasStock())
            return String.valueOf(dItem.of(itemToApplyLore).getStock().get(player));
        else
            return getRedCross();

    }

    private List<String> getDefaultLore() {
        return Lang.SHOPS_ITEMS_LORE.getAsListString(player,
                Template.of("buyPrice", getItemBuyPrice()),
                Template.of("sellPrice", getItemSellPrice()),
                Template.of("currency", getItemEconomyName()),
                Template.of("rarity", getItemRarity())
        );
    }

    private boolean playerHasStock() {
        dItem aux;
        return (aux = dItem.of(itemToApplyLore)).getStock() != null && aux.getStock().get(player) != -1;
    }

    private String getRedCross() {
        return FormatUtils.color("&c" + XSymbols.TIMES_3.parseSymbol());
    }

    private String getItemBuyPrice() {
        if (itemHasValidBuyPrice())
            return getItemBuyPriceDoubleFormatted();
        else
            return getRedCross();
    }

    private String getItemSellPrice() {
        if (itemHasValidSellPrice())
            return getItemSellPriceDoubleFormatted();
        else
            return getRedCross();
    }

    private String getItemEconomyName() {
        return dItem.of(itemToApplyLore).getEconomy().getName();
    }

    private String getItemRarity() {
        return dItem.of(itemToApplyLore).getRarity().toString();
    }

    private boolean itemHasValidBuyPrice() {
        return dItem.of(itemToApplyLore).getDBuyPrice().isPresent()
                && dItem.of(itemToApplyLore).getDBuyPrice().get().getPrice() > 0;
    }

    private boolean itemHasValidSellPrice() {
        return dItem.of(itemToApplyLore).getDSellPrice().isPresent() &&
                dItem.of(itemToApplyLore).getDSellPrice().get().getPrice() > 0;
    }

    private String getItemBuyPriceDoubleFormatted() {
        return PriceWrapper.format(getItemBuyPriceDouble() * dItem.of(itemToApplyLore).getSetItems().orElse(1));
    }

    private String getItemSellPriceDoubleFormatted() {
        return PriceWrapper.format(getItemSellPriceDouble() * dItem.of(itemToApplyLore).getSetItems().orElse(1));
    }

    private double getItemBuyPriceDouble() {
        return dItem.of(itemToApplyLore).getDBuyPrice().orElse(new dPrice(-1)).getPriceForPlayer(player, shop, dItem.getId(itemToApplyLore), priceModifier.type.BUY);
    }

    private double getItemSellPriceDouble() {
        return dItem.of(itemToApplyLore).getDSellPrice().orElse(new dPrice(-1)).getPriceForPlayer(player, shop, dItem.getId(itemToApplyLore), priceModifier.type.SELL);
    }


}
