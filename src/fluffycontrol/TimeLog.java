package fluffycontrol;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class TimeLog {
    private static String logName; // Store current file name temporarily
    public static Map<String, String> formattedLog = new LinkedHashMap<>();
    public static final DateTimeFormatter logFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmm");

    // Sets filename, and adds current time to a new log file.
    public static void logStart(Gson gson){
        DateTimeFormatter FileNameFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmm");
        LocalDateTime now = LocalDateTime.now();
        logName = now.format(FileNameFormatter) + ".log";

        formattedLog.put("Computer Started", now.format(logFormatter));
        writeToFile(gson, logName);
    }

    // Adds exit time to log file
    public static void logExit(Gson gson){
        LocalDateTime now = LocalDateTime.now();
        formattedLog.put("Computer Stopped", now.format(logFormatter));
        writeToFile(gson, logName);
    }

    private static void writeToFile(Gson gson, String fileName){
        String jsonOutput = gson.toJson(formattedLog);
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(jsonOutput);
            System.out.println(LogColors.GREEN + LogColors.BOLD + "Successfully written to " + fileName + LogColors.RESET);
        } catch (IOException e) {
            System.err.println("Failed to write JSON to file: " + e.getMessage());
        }
    }

    /*
    // Test
    static void main() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        logStart(gson);
        try {
            Thread.sleep(61000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        logExit(gson);
    }
    */
}
