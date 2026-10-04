package fluffycontrol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalTime;
import java.util.Scanner;
import javax.swing.*;

/**
 * date: 2026-01-17
 * @author Sovren
 */
public class FluffyControl {
    public static String username = System.getProperty("user.name");   
    public static String version = "0.15.1-Linux";
    public static boolean running = true;
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        onLaunch();
        // Fallback CLI Mode
        while(running){
            System.out.printf("%nWhat would you like to access?");
            System.out.printf("%nInput: ");
            String profile = input.nextLine().toLowerCase();
            switch(profile){
                case "ins" -> openWebBrowserTab("https://www.youtube.com/watch?v=nBdcQGPLejk");
                case "quit" -> running = false;
                case "help" -> System.out.print(LogColors.BLUE + LogColors.BOLD +"Commands: ins, quit" + LogColors.RESET);
                default -> System.out.println(LogColors.YELLOW + LogColors.BOLD + "[WARN] Invalid input, type help for commands" + LogColors.RESET);
            }
        }

        System.out.printf("%nQuitting program...%n");

        RestrictionManager.scheduler.shutdown();
        try {
        Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.exit(0);
    }

    private static void onLaunch() {
        // Config Setup
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(Config.defaultConfig);
        Config.doesConfigExist(jsonOutput);

        Config loadedConfig = Config.loadFile(gson, new File(Config.configFile),false);
        LocalTime bedtime = loadedConfig.getBedtime();
        LocalTime wakeUpTime = loadedConfig.getWakeup();

        // Makes sure internet is reconnected on launch if it's daytime
        if(!RestrictionManager.isNight && RestrictionManager.isBetween(LocalTime.now(), wakeUpTime, bedtime)){
            System.out.printf("%s%s%nisNight set to true%s%n", LogColors.BLUE,LogColors.BOLD,LogColors.RESET);
            RestrictionManager.isNight = true;
        }

        System.out.printf(LogColors.PURPLE + LogColors.BOLD + "Welcome to Fluffy Control version %s, %s!%n",version, username + LogColors.RESET);
        // GUI Setup
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
        } catch (Exception e) {
            // Fallback to cross-platform Metal if Motif isn't supported
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}
        }
        SwingUtilities.invokeLater(() -> new MainFrame(gson));

        RestrictionManager.restrictionSystem(gson);
    }

    // <editor-fold desc="Utility Launchers">
    
    // Launches the web browser with the specified URL.
    public static void openWebBrowserTab(String url){
        try{
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException ex) {
            System.getLogger(FluffyControl.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }    
    // </editor-fold>
}
