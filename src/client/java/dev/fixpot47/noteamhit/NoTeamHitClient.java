package dev.fixpot47.noteamhit;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

public final class NoTeamHitClient implements ClientModInitializer {
    public static final String MOD_ID = "noteamhit";
    public static final NoTeamHitConfig CONFIG = new NoTeamHitConfig();

    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(MOD_ID, "controls")
    );

    private KeyMapping openMenuKey;

    @Override
    public void onInitializeClient() {
        CONFIG.load();

        openMenuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.noteamhit.open_menu",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_O,
                CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.consumeClick()) {
                client.gui.setScreen(new NoTeamHitScreen(client.gui.screen()));
            }
        });

        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (!level.isClientSide()) {
                return InteractionResult.PASS;
            }

            if (entity instanceof Player target
                    && CONFIG.isProtected(target.getGameProfile().name())) {
                return InteractionResult.FAIL;
            }

            return InteractionResult.PASS;
        });
    }
}
