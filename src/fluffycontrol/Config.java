package fluffycontrol;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * date: 2026-09-26
 * @author Sovren
 */
public class Config {
    // Test config options may not be in final version
    private boolean autoStart;
    private LocalTime bedtime;
    private LocalTime wakeup;
    private String INTERFACE_TYPE; // or "eth0", "wlan0", etc. // Wi-Fi, Ethernet
    private String disabledInterface;
    private String countdownMessage;
    private LocalDate countdownEnd;

    public Config(boolean autoStart, LocalTime bedtime, LocalTime wakeup, String INTERFACE_TYPE, String disabledInterface, String countdownMessage, LocalDate countdownEnd) {
        this.autoStart = autoStart;
        this.bedtime = bedtime;
        this.wakeup = wakeup;
        this.INTERFACE_TYPE = INTERFACE_TYPE;
        this.disabledInterface = disabledInterface;
        this.countdownMessage = countdownMessage;
        this.countdownEnd = countdownEnd;
    }

    // Setters
    public void setAutoStart(boolean autoStart) { this.autoStart = autoStart; }

    public void setBedtime(LocalTime bedtime) {
        this.bedtime = bedtime;
    }

    public void setWakeup(LocalTime wakeup) {
        this.wakeup = wakeup;
    }

    public void setINTERFACE_TYPE(String INTERFACE_TYPE) {
        this.INTERFACE_TYPE = INTERFACE_TYPE;
    }

    public void setDisabledInterface(String disabledInterface) { this.disabledInterface = disabledInterface; }

    public void setCountdownEnd(LocalDate countdownEnd) {
        this.countdownEnd = countdownEnd;
    }

    public void setCountdownMessage(String countdownMessage) {
        this.countdownMessage = countdownMessage;
    }

    // Getters
    public boolean isAutoStart() { return autoStart; }

    public LocalTime getBedtime() {
        return bedtime;
    }

    public LocalTime getWakeup() {
        return wakeup;
    }

    public String getINTERFACE_TYPE() {
        return INTERFACE_TYPE;
    }

    public String getDisabledInterface() { return disabledInterface; }

    public LocalDate getCountdownEnd() {
        return countdownEnd;
    }

    public String getCountdownMessage() {
        return countdownMessage;
    }

    @Override
    public String toString() {
        return "AutoStart: " + autoStart + " bedtime: " + bedtime + " wakeup: " + wakeup + " INTERFACE_TYPE: " + INTERFACE_TYPE + "disabledInterface: " + disabledInterface + " countdownMessage: " + countdownMessage + " countdownEnd: " + countdownEnd;
    }

    public static String configFile = "config.json";

    public static Config loadFile(Gson gson, File fileName, boolean debug) {
        Config config = null;
        try (FileReader reader = new FileReader(fileName)) {
            config = gson.fromJson(reader, Config.class);
            if(debug) {
                System.out.println(LogColors.GREEN + LogColors.BOLD + "Successfully read config file" + LogColors.RESET);
                System.out.println("DEBUG: " + config);
            }
        } catch (IOException e) {
            System.err.println("Failed to read configuration file: " + e.getMessage());
        }
        return config;
    }

    public static void writeToFile(File fileName, String jsonOutput) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(jsonOutput);
            System.out.println(LogColors.GREEN + LogColors.BOLD + "Successfully written JSON object to " + fileName + LogColors.RESET);
        } catch (IOException e) {
            System.err.println("Failed to write JSON to file: " + e.getMessage());
        }
    }

    public static Config defaultConfig = new Config(
            false,
            LocalTime.of(23, 30),
            LocalTime.of(9, 0),
            "Ethernet",
            "",
            "Black Friday",
            LocalDate.of(2026,11,27)
    );

    // Check if config file exists and load file
    public static void doesConfigExist(String jsonOutput) {
        File fileName = new File(Config.configFile);
        if (fileName.isFile()) {
            System.out.println(LogColors.BLUE + LogColors.BOLD + "Config file found. Loading configuration..." + LogColors.RESET);
            //System.out.println("Default Config: " + Config.defaultConfig);
        } else {
            // Write defaults
            System.out.println(LogColors.YELLOW + LogColors.BOLD + "[WARN] Config file not found. Creating new one..." + LogColors.RESET);
            Config.writeToFile(fileName, jsonOutput);
        }
    }
}
