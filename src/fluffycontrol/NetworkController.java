package fluffycontrol;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

public class NetworkController {
    public static void turnOffNetwork(Gson gson) { runCommand("nmcli", "device", "disconnect", getInterface(gson));
    }

    public static void turnOnNetwork(Gson gson) { runCommand("nmcli", "device", "connect", getDisabledInterface(gson));
    } // Load name from config which is temporarily saved when connection is cut

    public static void runCommand(String... command){
        try {
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.printf("%s%s%nNetwork Command successful.%s%n",LogColors.BLUE, LogColors.BOLD, LogColors.RESET);
                return;
            }

            System.err.println("Command failed. Exit code: " + exitCode);

        } catch (IOException e) {
            System.err.println("Could not execute command: " + e.getMessage());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static String getInterface(Gson gson) {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface ni : Collections.list(interfaces)) {
                // Skip loopback, virtual, or inactive interfaces
                if (ni.isLoopback() || !ni.isUp()) {
                    continue;
                }

                String name = ni.getName();
                // Target Ethernet or Wi-Fi interface prefixes
                Config loadedConfig = Config.loadFile(gson, new File(Config.configFile), false);
                String type = loadedConfig.getINTERFACE_TYPE();
                if(type != null && type.equalsIgnoreCase("ethernet")){
                    if (name.startsWith("en") || name.startsWith("eth")) {
                        System.out.printf("%s%s%nEthernet: %s%n%s",LogColors.BLUE, LogColors.BOLD, name, LogColors.RESET);
                        // Save to file
                        loadedConfig.setDisabledInterface(name);
                        String jsonOutput = gson.toJson(loadedConfig);
                        Config.writeToFile(new File(Config.configFile), jsonOutput);
                        return name;
                    }
                } else if(type != null && type.equalsIgnoreCase("wi-fi")){
                   if (name.startsWith("wl")) {
                       System.out.printf("%s%s%nWi-Fi: %s%n%s",LogColors.BLUE, LogColors.BOLD, name, LogColors.RESET);
                       // Save to file
                       loadedConfig.setDisabledInterface(name);
                       String jsonOutput = gson.toJson(loadedConfig);
                       Config.writeToFile(new File(Config.configFile), jsonOutput);
                       return name;
                   }
                } else {
                    System.out.printf("%s%s%n[WARN] %s is not a valid type. Please enter Wi-Fi or Ethernet%n%s",LogColors.YELLOW, LogColors.BOLD, type,  LogColors.RESET);
                }
            }
        } catch (SocketException e) {
            System.err.println("Failed to detect network interface: " + e.getMessage());
        }
        // Fallback
        return null;
    }

    public static String getDisabledInterface(Gson gson){
        Config loadedConfig = Config.loadFile(gson, new File(Config.configFile), false);
        String name = loadedConfig.getDisabledInterface();
        if(name == null) System.out.printf("%s%s%n[WARN] Disabled Interface is null %s%n",LogColors.YELLOW, LogColors.BOLD,  LogColors.RESET);
        return name;
    }
}