package io.github.divios.lib.storage.parser;

import com.cryptomorin.xseries.XMaterial;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.lib.dLib.dItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class dItemState {

    private String name;
    private List<String> lore;
    private Material material;
    private Integer quantity;
    private Map<String, Integer> enchantments = new HashMap<>();
    private dItemMetaState dailyShop_meta;
    private JsonObject nbt;

    public static dItemState of(ItemStack item) {
        return new dItemState(item);
    }

    private dItemState(ItemStack item) {

        name = FormatUtils.unColor(ItemUtils.getName(item));
        lore = ItemUtils.getLore(item);
        material = ItemUtils.getMaterial(item);
        quantity = item.getAmount();
        dailyShop_meta = dItemMetaState.of(item);

        item.getEnchantments().forEach((enchantment, integer) ->
                enchantments.put(enchantment.getName(), integer));


        NBTItem nbtItem = new NBTItem(item);
        nbt = new Gson().fromJson(nbtItem.toString(), JsonObject.class);

        if (nbt.has("rds_setItems")) {
            quantity = null;
        }

        // Remove already cached metadata
        nbt.remove("rds_UUID");
        nbt.remove("rds_rarity");
        nbt.remove("rds_sellPrice");
        nbt.remove("dailySlots");
        nbt.remove("rds_buyPrice");
        nbt.remove("rds_stock");
        nbt.remove("rds_cmds");
        nbt.remove("dailySlots");
        nbt.remove("rds_setItems");
        nbt.remove("rds_perms_buy");
        nbt.remove("rds_perms_sell");
        nbt.remove("rds_confirm_gui");
        nbt.remove("rds_bundle");
        nbt.remove("rds_econ");
        nbt.remove("rds_rawItem");
        nbt.remove("rds_econ");
        nbt.remove("display");
        nbt.remove("lore");
        nbt.remove("Enchantments");

        //Preconditions;
        if (name.isEmpty()) name = null;
        if (lore.isEmpty()) lore = null;
        if (quantity != null && quantity == 1) quantity = null;
        if (enchantments.isEmpty()) enchantments = null;
        if (nbt.size() == 0) nbt = null;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public Material getMaterial() {
        return material;
    }

    public int getQuantity() {
        return quantity;
    }

    public Map<String, Integer> getEnchantments() {
        return enchantments;
    }

    public dItemMetaState getDailyShop_meta() {
        return dailyShop_meta;
    }

    public JsonObject getNbt() {
        return nbt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setEnchantments(Map<String, Integer> enchantments) {
        this.enchantments = enchantments;
    }

    public void setNbt(JsonObject nbt) {
        this.nbt = nbt;
    }

    public void setDailyShop_meta(dItemMetaState dailyShop_meta) {
        this.dailyShop_meta = dailyShop_meta;
    }

    public dItem parseItem(UUID uuid) {

        // Preconditions
        if (name == null) name = "";
        name = FormatUtils.color(name);
        if (lore == null) lore = Collections.emptyList();
        if (quantity == null) quantity = 1;
        if (nbt == null) nbt = new JsonObject();

        NBTItem item = new NBTItem(ItemBuilder.of(XMaterial.matchXMaterial(material))
                .setName(name).setLore(lore));

        item.mergeCompound(new NBTContainer(nbt.toString()));

        dItem newItem = dItem.of(item.getItem().clone());
        newItem.setQuantity(quantity);
        newItem.setUid(uuid);

        if (enchantments != null)
            enchantments.forEach((s, integer) -> newItem.addEnchantments(Enchantment.getByName(s), integer));

        try {
            dailyShop_meta.applyValues(newItem);
        } catch (Exception e) {
            Log.info("There was an error trying to parse the item of id " + newItem.getUid());
            e.printStackTrace();
        }
        return newItem;
    }
    
}
