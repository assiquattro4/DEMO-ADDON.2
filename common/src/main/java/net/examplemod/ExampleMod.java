package net.examplemod;

import net.examplemod.registry.ModBlocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod {
    public static final String MOD_ID = "examplemod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        // AGGIUNGI QUESTA RIGA QUI SOTTO:
        // Questo comando dice al gioco di caricare il nostro Redstone Track all'avvio
        ModBlocks.register();

        LOGGER.info("Redstone Track Addon caricato con successo!");
    }
}
