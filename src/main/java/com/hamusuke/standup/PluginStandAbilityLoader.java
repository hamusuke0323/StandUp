package com.hamusuke.standup;

import com.hamusuke.standup.stand.ability.StandAbility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.ServiceLoader;

public class PluginStandAbilityLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static PluginStandAbilityLoader service;
    private final ServiceLoader<StandAbility> loader;

    private PluginStandAbilityLoader() {
        this.loader = ServiceLoader.load(StandAbility.class);
    }

    public static synchronized PluginStandAbilityLoader getInstance() {
        if (service == null) {
            service = new PluginStandAbilityLoader();
        }

        return service;
    }

    public void reloadStandAbilities() {
        this.loader.reload();
    }

    public List<StandAbility> getStandAbilities() {
        return this.loader.stream().map(prov -> {
            LOGGER.info("Stand ability plugin {} loaded!", prov.type().getSimpleName());
            return prov.get();
        }).toList();
    }
}
