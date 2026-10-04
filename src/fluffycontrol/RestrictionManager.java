package fluffycontrol;

import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static fluffycontrol.FluffyControl.username;

public class RestrictionManager {
    public static boolean isNight = false;

    public static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void restrictionSystem(Gson gson){
        scheduler.scheduleAtFixedRate(() -> {
            LocalTime now = LocalTime.now();

            Config loadedConfig = Config.loadFile(gson, new File(Config.configFile),false);
            LocalTime bedtime = loadedConfig.getBedtime();
            LocalTime wakeUpTime = loadedConfig.getWakeup();

            boolean isDayTime = isBetween(now, wakeUpTime, bedtime);

            // Nighttime. After 23:30 and before 8:00
            if (!isDayTime && !isNight) {
                System.out.printf("%nGet to bed");
                SwingUtilities.invokeLater(warnWindow::new);
                // Turn off Ethernet
                NetworkController.turnOffNetwork(gson);
                isNight = true;
            }else if (isDayTime && isNight) {
                System.out.printf("%nGood morning, %s! ", username);
                // turn on Ethernet
                NetworkController.turnOnNetwork(gson);
                isNight = false;
            }
        }, 0,30, TimeUnit.SECONDS);
    }

    // TimerCheck Helper
    public static boolean isBetween(LocalTime now, LocalTime start, LocalTime end) {
        return !now.isBefore(start) && !now.isAfter(end);
    }
}
