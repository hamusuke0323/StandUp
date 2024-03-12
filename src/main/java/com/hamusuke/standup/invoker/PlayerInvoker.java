package com.hamusuke.standup.invoker;

import com.hamusuke.standup.stand.stands.Stand;
import com.hamusuke.standup.stand.stands.Stand.StandOperationMode;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface PlayerInvoker {
    @Nullable
    Stand getStand();

    void standUp(Stand stand);

    void standDown();

    ItemStack getStandCard();

    void setStandCard(ItemStack card);

    default StandOperationMode getOpMode() {
        return this.isStandAlive() ? this.getStand().getMode() : StandOperationMode.AI;
    }

    default boolean isControllingStand() {
        return this.isStandAlive() && this.getOpMode() == StandOperationMode.OWNER;
    }

    default boolean isStandAlive() {
        return this.getStand() != null && this.getStand().isAlive();
    }

    static PlayerInvoker invoker(Object player) {
        return (PlayerInvoker) player;
    }
}
