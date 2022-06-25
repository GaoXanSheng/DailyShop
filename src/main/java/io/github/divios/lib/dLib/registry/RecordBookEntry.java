package io.github.divios.lib.dLib.registry;

import com.google.common.base.Preconditions;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@SuppressWarnings("unused")
public class RecordBookEntry {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    private final String p;
    private final String shopID;
    private final String ID;
    private final ItemStack rawItem;
    private final Transactions.Type type;
    private final double price;
    private final int quantity;
    private final LocalDateTime timestamp;

    private RecordBookEntry(String p,
                            String shopID,
                            String ID,
                            ItemStack rawItem,
                            Transactions.Type type,
                            double price,
                            int quantity,
                            LocalDateTime timestamp
    ) {
        this.p = p;
        this.shopID = shopID;
        this.ID = ID;
        this.rawItem = rawItem;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public String getPlayer() {
        return p;
    }

    public String getShopID() {
        return shopID;
    }

    public String getItemID() {
        return ID;
    }

    public ItemStack getRawItem() {
        return rawItem;
    }

    public Transactions.Type getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public static dLogEntryBuilder createEntry() {
        return new dLogEntryBuilder();
    }

    public dLogEntryState toState() {
        return new dLogEntryState(p,
                shopID,
                ID,
                rawItem.getType().name(),
                type.name(),
                price,
                quantity,
                FORMAT.format(timestamp)
        );
    }


    public static final class dLogEntryBuilder {
        private String p;
        private String shopID;
        private String ID;
        private ItemStack rawItem;
        private Transactions.Type type;
        private Double price;
        private Integer quantity;
        private LocalDateTime timestamp;

        private dLogEntryBuilder() {
        }

        public dLogEntryBuilder withPlayer(Player p) {
            return withPlayer(p.getDisplayName());
        }

        public dLogEntryBuilder withPlayer(String p) {
            this.p = FormatUtils.stripColor(p);
            return this;
        }

        public dLogEntryBuilder withShopID(String shopID) {
            this.shopID = shopID;
            return this;
        }

        public dLogEntryBuilder withItemID(String ID) {
            this.ID = ID;
            return this;
        }

        public dLogEntryBuilder withRawItem(ItemStack rawItem) {
            this.rawItem = rawItem;
            return this;
        }

        public dLogEntryBuilder withType(Transactions.Type type) {
            this.type = type;
            return this;
        }

        public dLogEntryBuilder withPrice(double price) {
            this.price = price;
            return this;
        }

        public dLogEntryBuilder withQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public dLogEntryBuilder withTimestamp(String timestamp) {
            return withTimestamp(LocalDateTime.parse(timestamp));
        }

        public dLogEntryBuilder withTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public RecordBookEntry create() {

            Preconditions.checkNotNull(p, "player");
            Preconditions.checkNotNull(shopID, "shopID");
            Preconditions.checkNotNull(ID, "ID");
            Preconditions.checkNotNull(rawItem, "rawItem");
            Preconditions.checkNotNull(type, "type");
            Preconditions.checkNotNull(price, "price");

            if (quantity == null) quantity = 1;
            if (timestamp == null) timestamp = LocalDateTime.now();

            return new RecordBookEntry(p, shopID, ID, rawItem, type, price, quantity, timestamp);
        }
    }

    public static final class dLogEntryState {

        private final String p;
        private final String shopID;
        private final String ID;
        private final String item;
        private final String type;
        private final double price;
        private final int quantity;
        private final String timestamp;

        public dLogEntryState(String p,
                              String shopID,
                              String ID,
                              String rawItem,
                              String type,
                              double price,
                              int quantity,
                              String timestamp
        ) {
            this.p = p;
            this.shopID = shopID;
            this.ID = ID;
            this.item = rawItem;
            this.type = type;
            this.price = price;
            this.quantity = quantity;
            this.timestamp = timestamp;
        }

        public String getP() {
            return p;
        }

        public String getShopID() {
            return shopID;
        }

        public String getID() {
            return ID;
        }

        public String getItem() {
            return item;
        }

        public String getType() {
            return type;
        }

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public RecordBookEntry build() {
            return new RecordBookEntry(p,
                    shopID,
                    ID,
                    ItemUtils.deserialize(item),
                    Transactions.Type.valueOf(type.toUpperCase()),
                    price,
                    quantity,
                    LocalDateTime.parse(timestamp)
            );
        }

    }

}
