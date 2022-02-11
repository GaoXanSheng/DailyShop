package io.github.divios.lib.dLib.dTransaction;

import io.github.divios.dailyShop.files.Messages;
import org.bukkit.entity.Player;

public enum TransactionError {
    noPerms(Messages.MSG_NOT_PERMS_ITEM),
    noMoney(Messages.MSG_NOT_MONEY),
    noSpace(Messages.MSG_INV_FULL),
    noStock(Messages.MSG_NOT_STOCK),
    noEnoughItems(Messages.MSG_NOT_ITEMS),
    limitReached(Messages.MSG_LIMIT),
    shopBalanceMax(Messages.MSG_BALANCE_MAX_LIMIT),
    shopBalanceMin(Messages.MSG_BALANCE_MIN_LIMIT);

    private final Messages msg;

    TransactionError(Messages msg) {
        this.msg = msg;
    }

    public void sendErrorMsg(Player p) {
        msg.send(p);
    }

}
