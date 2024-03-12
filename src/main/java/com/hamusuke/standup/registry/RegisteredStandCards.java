package com.hamusuke.standup.registry;

import com.hamusuke.standup.stand.ability.StandCard;
import net.minecraftforge.registries.DeferredRegister;

import static com.hamusuke.standup.StandUp.MOD_ID;
import static com.hamusuke.standup.StandUp.STAND_CARD_REGISTRY_KEY;

public class RegisteredStandCards {
    public static final DeferredRegister<StandCard> STAND_CARDS = DeferredRegister.create(STAND_CARD_REGISTRY_KEY, MOD_ID);
}
