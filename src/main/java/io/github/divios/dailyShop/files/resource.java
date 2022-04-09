package io.github.divios.dailyShop.files;

import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.FileUtils;
import io.github.divios.dailyShop.utils.Timer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;

public abstract class resource {

    private static final DailyShop plugin = DailyShop.get();

    protected final String name;
    protected boolean firstTime = true;
    private boolean copyDefaults = true;

    private final File file;
    protected YamlConfiguration yaml;
    private long checkSum;

    protected resource(String name) {
        this(name, true);
    }

    protected resource(String name, boolean copyDefaults) {
        this.name = name;
        this.file = new File(plugin.getDataFolder(), name);
        this.copyDefaults = copyDefaults;
        create();
        firstTime = false;
    }

    public void create() {

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(name, false);
        }

        Timer timer = Timer.create();
        Log.info(getStartMessage());
        long checkSumAux;
        if ((checkSumAux = FileUtils.getFileCheckSum(file)) == checkSum) { // If same checkSum -> no changes
            timer = null;
            Log.info(getCanceledMessage());
            return;
        }
        checkSum = checkSumAux;

        try (FileInputStream fos = new FileInputStream(file)) {
            try (InputStreamReader osw = new InputStreamReader(fos, StandardCharsets.UTF_8)) {
                try (BufferedReader bw = new BufferedReader(osw)) {
                    yaml = YamlConfiguration.loadConfiguration(bw);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (copyDefaults) copyDefaults();

        init();
        timer.stop();
        Log.info(getFinishedMessage(timer.getTime()));
    }

    public void reload() {
        create();
    }

    protected abstract String getStartMessage();

    protected abstract String getCanceledMessage();

    protected abstract String getFinishedMessage(long time);

    protected abstract void init();

    private void copyDefaults() {
        Reader defConfigStream = null;
        defConfigStream = new InputStreamReader(plugin.getResource(name), StandardCharsets.UTF_8);
        YamlConfiguration defConfig = null;
        if (defConfigStream != null) {
            defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            yaml.setDefaults(defConfig);
            yaml.options().copyDefaults(true);
        }

        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (defConfig != null) yaml.setDefaults(defConfig);
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public YamlConfiguration getYaml() {
        return yaml;
    }
}