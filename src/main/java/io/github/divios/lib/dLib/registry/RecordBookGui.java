package io.github.divios.lib.dLib.registry;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.inventory.builder.inventoryPopulator;
import io.github.divios.core_lib.inventory.builder.paginatedGui;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.utils.Exceptions;
import io.github.divios.dailyShop.utils.FutureUtils;
import io.github.divios.dailyShop.utils.PrettyPrice;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.registry.util.RecordBookOptions;
import io.github.divios.lib.dLib.registry.util.RecordBookOptionsGui;
import io.github.divios.lib.dLib.registry.util.RecordUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class RecordBookGui {

    private final Player p;
    private final Runnable fallback;
    private final RecordBookOptions options;

    private RecordBookGui(Player p, Runnable fallback, RecordBookOptions options) {
        this.p = p;
        this.fallback = fallback;
        this.options = options;

        Utils.sendRawMsg(p, "Generating report, please wait");
        init();
    }

    private void init() {

        CompletableFuture<paginatedGui> gui = paginatedGui.Builder()
                .withTitle((page, max) -> "&8Log " + page + "/" + max)
                .withItems(() ->
                        DailyShop.get().getDatabaseManager().getLogEntries(2700).stream()
                                .filter(dLogEntry -> {
                                    boolean result = true;
                                    if (options.getfPlayer() != null)
                                        result = dLogEntry.getPlayer().equals(options.getfPlayer());
                                    if (options.getfShopId() != null)
                                        result &= dLogEntry.getShopID().equals(options.getfShopId());
                                    if (options.getfType() != null)
                                        result &= dLogEntry.getType().equals(options.getfType());
                                    return result;
                                })
                                .map(dLogEntry -> ItemButton.create(
                                        ItemBuilder.of(options.isDisplay() ? dLogEntry.getRawItem() : ItemBuilder.of(XMaterial.PLAYER_HEAD).applyTexture(Bukkit.getOfflinePlayer(dLogEntry.getPlayer()).getUniqueId()))
                                                .addLore(
                                                        "",
                                                        "&7Player: &e" + dLogEntry.getPlayer(),
                                                        "&7ShopId: &e" + dLogEntry.getShopID(),
                                                        "&7ID: &e" + dLogEntry.getItemID(),
                                                        "&7Quantity: &e" + dLogEntry.getQuantity(),
                                                        "&7Type: &e" + dLogEntry.getType(),
                                                        "&7Price: &e" + PrettyPrice.pretty(dLogEntry.getPrice()),
                                                        "&7TimeStamp &e" + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(dLogEntry.getTimestamp())
                                                ),
                                        e -> {
                                        }
                                ))

                                .collect(Collectors.toList())
                )
                .withNextButton(
                        ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                .setName(Lang.DAILY_ITEMS_NEXT.getAsString(p))
                                .applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf")
                        , 52
                )

                .withBackButton(
                        ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                .setName(Lang.DAILY_ITEMS_PREVIOUS.getAsString(p))
                                .applyTexture("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9")
                        , 46
                )

                .withExitButton(
                        ItemButton.create(
                                ItemBuilder.of(XMaterial.OAK_DOOR)
                                        .setName("&cExit")
                                        .setLore("&7Click to Exit")
                                , e -> fallback.run()), 49
                )
                .withPopulator(
                        inventoryPopulator.builder()
                                .ofGlass()
                                .mask("000000000")
                                .mask("000000000")
                                .mask("000000000")
                                .mask("000000000")
                                .mask("000000000")
                                .mask("111111111")
                                .scheme(0)
                                .scheme(0)
                                .scheme(0)
                                .scheme(0)
                                .scheme(0)
                                .scheme(7, 7, 7, 7, 7, 7, 7, 7, 7)
                )
                .withButtons((inventoryGUI, integer) -> {

                            inventoryGUI.addButton(
                                    ItemButton.create(
                                            ItemBuilder.of(XMaterial.PAPER)
                                                    .setName("&e&lCreate json").setLore("&7Click to create a json file", "&7with the current filtered entries")
                                            , e ->
                                                    RecordUtils.importToYaml(
                                                            DailyShop.get().getDatabaseManager().getLogEntries(Integer.MAX_VALUE).stream()
                                                                    .filter(dLogEntry -> {
                                                                        boolean result = true;
                                                                        if (options.getfPlayer() != null)
                                                                            result = dLogEntry.getPlayer().equals(options.getfPlayer());
                                                                        if (options.getfShopId() != null)
                                                                            result &= dLogEntry.getShopID().equals(options.getfShopId());
                                                                        if (options.getfType() != null)
                                                                            result &= dLogEntry.getType().equals(options.getfType());
                                                                        return result;
                                                                    })
                                                                    .map(RecordBookEntry::toState)
                                                                    .collect(Collectors.toList())
                                                    ).thenAccept(unused -> Utils.sendRawMsg(p, "Entries imported successfully"))

                                    ), 45
                            );

                            inventoryGUI.addButton(
                                    ItemButton.create(
                                            ItemBuilder.of(XMaterial.COMMAND_BLOCK)
                                                    .setName("&e&lSettings").setLore("&7Click to change", "&7the view filter")
                                            , e ->
                                                    RecordBookOptionsGui.builder()
                                                            .withPlayer(p)
                                                            .withOptions(options)
                                                            .withFallback(logOptions -> new RecordBookGui(p, fallback, logOptions))
                                                            .build()
                                    ), 50
                            );

                            inventoryGUI.addButton(
                                    ItemButton.create(
                                            ItemBuilder.of(XMaterial.REDSTONE_TORCH)
                                                    .setName("&e&lSwitch display").setLore("&7Click to change", "&7the items display")
                                            , e -> new RecordBookGui(p, fallback, options.switchDisplay())
                                    ), 48
                            );

                        }
                )
                .buildFuture();

        Exceptions.tryTo(() -> gui.get().open(p));
    }

    public static LogGuiBuilder builder() {
        return new LogGuiBuilder();
    }

    public static final class LogGuiBuilder {
        private Player p;
        private Runnable fallback;
        private RecordBookOptions options;

        private LogGuiBuilder() {
        }

        public LogGuiBuilder withPlayer(Player p) {
            this.p = p;
            return this;
        }

        public LogGuiBuilder withFallback(Runnable fallback) {
            this.fallback = fallback;
            return this;
        }

        public LogGuiBuilder withOptions(RecordBookOptions options) {
            this.options = options;
            return this;
        }

        public RecordBookGui prompt() {

            Preconditions.checkNotNull(p, "player null");

            if (options == null) options = RecordBookOptions.emptyOption();
            if (fallback == null) fallback = () -> p.closeInventory();

            return new RecordBookGui(p, fallback, options);
        }
    }
}
