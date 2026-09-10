package dev.fixpot47.noteamhit;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NoTeamHitConfig {
    public static final int MAX_TEAMMATES = 8;

    private static final String USERNAME_REGEX = "[A-Za-z0-9_]{1,16}";
    private static final Pattern TEAMMATES_ARRAY = Pattern.compile("\\\"teammates\\\"\\s*:\\s*\\[(.*?)]", Pattern.DOTALL);
    private static final Pattern QUOTED_USERNAME = Pattern.compile("\\\"([A-Za-z0-9_]{1,16})\\\"");

    private final Path path = FabricLoader.getInstance().getConfigDir().resolve("noteamhit.json");
    private final List<String> teammates = new ArrayList<>();

    public synchronized void load() {
        teammates.clear();
        if (!Files.exists(path)) return;

        try {
            String json = Files.readString(path, StandardCharsets.UTF_8);
            Matcher array = TEAMMATES_ARRAY.matcher(json);
            if (!array.find()) return;

            Matcher names = QUOTED_USERNAME.matcher(array.group(1));
            while (names.find() && teammates.size() < MAX_TEAMMATES) {
                String name = names.group(1);
                if (!containsIgnoreCase(name)) teammates.add(name);
            }
        } catch (IOException exception) {
            System.err.println("[No Team Hit] Could not read config: " + exception.getMessage());
        }
    }

    public synchronized List<String> getTeammates() {
        return Collections.unmodifiableList(new ArrayList<>(teammates));
    }

    public synchronized boolean isProtected(String name) {
        return name != null && containsIgnoreCase(name);
    }

    public synchronized AddResult add(String rawName) {
        String name = rawName == null ? "" : rawName.trim();
        if (!isValidUsername(name)) return AddResult.INVALID;
        if (containsIgnoreCase(name)) return AddResult.DUPLICATE;
        if (teammates.size() >= MAX_TEAMMATES) return AddResult.FULL;

        teammates.add(name);
        save();
        return AddResult.ADDED;
    }

    public synchronized boolean remove(String name) {
        for (int i = 0; i < teammates.size(); i++) {
            if (teammates.get(i).equalsIgnoreCase(name)) {
                teammates.remove(i);
                save();
                return true;
            }
        }
        return false;
    }

    private boolean containsIgnoreCase(String name) {
        for (String teammate : teammates) {
            if (teammate.equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    private boolean isValidUsername(String name) {
        return name.matches(USERNAME_REGEX);
    }

    private void save() {
        try {
            Files.createDirectories(path.getParent());
            StringBuilder json = new StringBuilder();
            json.append("{\n  \"teammates\": [");
            for (int i = 0; i < teammates.size(); i++) {
                if (i > 0) json.append(',');
                json.append("\n    \"").append(teammates.get(i)).append("\"");
            }
            if (!teammates.isEmpty()) json.append('\n');
            json.append("  ]\n}\n");
            Files.writeString(path, json.toString(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            System.err.println("[No Team Hit] Could not save config: " + exception.getMessage());
        }
    }

    public enum AddResult {
        ADDED,
        INVALID,
        DUPLICATE,
        FULL
    }
}
