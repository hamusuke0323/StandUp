package com.hamusuke.standup.stand.ability.deadly_queen;

import com.hamusuke.standup.stand.stands.DeadlyQueen;
import net.minecraft.server.level.ServerLevel;

public abstract class Bomb {
    protected final DeadlyQueen stand;
    protected final ServerLevel level;
    protected final When explodeWhen;
    protected final What whatExplodes;
    protected boolean exploded;

    protected Bomb(DeadlyQueen stand, When explodeWhen, What whatExplodes) {
        this.stand = stand;
        this.level = (ServerLevel) stand.level();
        this.explodeWhen = explodeWhen;
        this.whatExplodes = whatExplodes;
    }

    public void tick() {
    }

    public void explode() {
        this.exploded = true;

        switch (this.whatExplodes) {
            case SELF -> this.explodeSelf();
            case TOUCHING_ENTITY -> this.explodeTouchingEntity();
        }
    }

    protected abstract void explodeSelf();

    protected abstract void explodeTouchingEntity();

    public boolean isStillValid() {
        return !this.exploded;
    }

    public enum When {
        PUSH_SWITCH,
        TOUCH
    }

    public enum What {
        SELF,
        TOUCHING_ENTITY
    }
}
