package dev.fixpot47.noteamhit;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class NoTeamHitScreen extends Screen {
    private final Screen parent;
    private final Component status;
    private final int statusColor;
    private final String initialInput;

    private EditBox nameBox;

    public NoTeamHitScreen(Screen parent) {
        this(parent, "", Component.empty(), 0xFFFFFFFF);
    }

    private NoTeamHitScreen(Screen parent, String initialInput, Component status, int statusColor) {
        super(Component.translatable("screen.noteamhit.title"));
        this.parent = parent;
        this.initialInput = initialInput;
        this.status = status;
        this.statusColor = statusColor;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = width / 2;

        nameBox = new EditBox(font, 150, 20, Component.translatable("screen.noteamhit.input"));
        nameBox.setX(centerX - 110);
        nameBox.setY(50);
        nameBox.setMaxLength(16);
        nameBox.setValue(initialInput);
        addRenderableWidget(nameBox);

        addRenderableWidget(Button.builder(Component.translatable("screen.noteamhit.add"), button -> addPlayer())
                .bounds(centerX + 44, 50, 66, 20)
                .build());

        List<String> teammates = NoTeamHitClient.CONFIG.getTeammates();
        for (int i = 0; i < teammates.size(); i++) {
            String name = teammates.get(i);
            int column = i % 2;
            int row = i / 2;
            int x = centerX - 110 + column * 112;
            int y = 92 + row * 24;

            addRenderableWidget(Button.builder(Component.literal("× " + name), button -> removePlayer(name))
                    .bounds(x, y, 108, 20)
                    .build());
        }

        addRenderableWidget(Button.builder(Component.translatable("screen.noteamhit.done"), button -> onClose())
                .bounds(centerX - 55, 196, 110, 20)
                .build());
    }

    private void addPlayer() {
        String name = nameBox.getValue().trim();
        NoTeamHitConfig.AddResult result = NoTeamHitClient.CONFIG.add(name);

        switch (result) {
            case ADDED -> reopen("", Component.translatable("screen.noteamhit.added", name), 0xFF55FF55);
            case INVALID -> reopen(name, Component.translatable("screen.noteamhit.invalid"), 0xFFFF5555);
            case DUPLICATE -> reopen(name, Component.translatable("screen.noteamhit.duplicate", name), 0xFFFFAA00);
            case FULL -> reopen(name, Component.translatable("screen.noteamhit.full"), 0xFFFF5555);
        }
    }

    private void removePlayer(String name) {
        NoTeamHitClient.CONFIG.remove(name);
        reopen("", Component.translatable("screen.noteamhit.removed", name), 0xFFAAAAAA);
    }

    private void reopen(String input, Component message, int color) {
        if (minecraft != null) {
            minecraft.gui.setScreen(new NoTeamHitScreen(parent, input, message, color));
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        graphics.centeredText(font, title, width / 2, 15, 0xFFFFFFFF);
        graphics.centeredText(font, Component.translatable("screen.noteamhit.hint"), width / 2, 30, 0xFFAAAAAA);

        int count = NoTeamHitClient.CONFIG.getTeammates().size();
        graphics.centeredText(font, Component.translatable("screen.noteamhit.count", count), width / 2, 76, 0xFFFFFFFF);

        if (count == 0) {
            graphics.centeredText(font, Component.translatable("screen.noteamhit.empty"), width / 2, 110, 0xFF888888);
        }

        if (!status.getString().isEmpty()) {
            graphics.centeredText(font, status, width / 2, 220, statusColor);
        }
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            minecraft.gui.setScreen(parent);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
