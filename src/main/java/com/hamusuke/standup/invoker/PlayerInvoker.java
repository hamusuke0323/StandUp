package com.hamusuke.standup.invoker;

import com.hamusuke.standup.stand.Stand;
import com.hamusuke.standup.stand.Stand.StandOperationMode;
import org.jetbrains.annotations.Nullable;

public interface PlayerInvoker {
    @Nullable
    Stand getStand();

    void standUp(Stand stand);

    void standDown();

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
