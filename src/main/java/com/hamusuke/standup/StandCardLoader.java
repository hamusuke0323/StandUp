package com.hamusuke.standup;

import com.hamusuke.standup.stand.card.StandCard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.ServiceLoader;

public class StandCardLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static StandCardLoader service;
    private final ServiceLoader<StandCard> loader;

    private StandCardLoader() {
        this.loader = ServiceLoader.load(StandCard.class);
    }

    public static synchronized StandCardLoader getInstance() {
        if (service == null) {
            service = new StandCardLoader();
        }

        return service;
    }

    void reloadStandCards() {
        this.loader.reload();
    }

    List<StandCard> getStandCards() {
        return this.loader.stream().map(prov -> {
            LOGGER.info("Stand Card plugin {} loaded!", prov.type().getSimpleName());
            return prov.get();
        }).toList();
    }
}
