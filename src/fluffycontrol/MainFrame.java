package fluffycontrol;

import com.google.gson.Gson;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * date: 2026-02-17
 * @author Sovren
 */
public class MainFrame extends JFrame {
    private static final Random rand = new Random();
    private Point mouseClickPoint; // Store initial mouse position on click
    public static final Font menuFont = new Font("Monospaced", Font.BOLD, 12);

    public static final Color backgroundColor = new Color(0,128,128);
    public static final  Color panelColor = new Color(174, 178, 188);
    public static final Color phosphorGreen = new Color(0, 255, 65);

    public static ConfigWindow theConfigWindow = null;

    public MainFrame(Gson gson){
        setTitle("Fluffy Control Panel v" + FluffyControl.version);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720 , 500);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setResizable(false);
        // Icon
        URL resource = MainFrame.class.getResource("/res/FluffyControlIcon.png");
        if (resource != null) {
            ImageIcon iconImage = new ImageIcon(resource);
            setIconImage(iconImage.getImage());
        } else {
            System.err.println("Could not find IconImage");
        }

        JPanel rootPanel = new JPanel(new BorderLayout(10, 10));
        rootPanel.setBackground(backgroundColor);
        rootPanel.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        setContentPane(rootPanel);

        //<editor-fold desc="Top Menu Bar">
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(panelColor);
        menuBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        JLabel label = new JLabel("  " + "FluffyControl");
        label.setFont(menuFont);
        menuBar.add(label);

        menuBar.add(Box.createHorizontalGlue());

        // Window buttons
        JButton hideBtn = new JButton("-");
        hideBtn.setFont(menuFont);
        hideBtn.setFocusPainted(false);
        hideBtn.addActionListener(e -> MainFrame.this.setExtendedState(MainFrame.this.getExtendedState() | Frame.ICONIFIED));
        menuBar.add(hideBtn);

        JButton closeBtn = new JButton("X");
        closeBtn.setFont(menuFont);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> {
            System.out.printf("%nQuitting program...%n");
            System.exit(0);
        });
        menuBar.add(closeBtn);

        setJMenuBar(menuBar);

        // Drag behavior
        MouseAdapter dragAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Save mouse position relative to the window frame
                mouseClickPoint = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                // Calculate current screen coordinates minus click offset
                Point currentScreenLocation = e.getLocationOnScreen();
                setLocation(
                        currentScreenLocation.x - mouseClickPoint.x,
                        currentScreenLocation.y - mouseClickPoint.y
                );
            }
        };
        menuBar.addMouseListener(dragAdapter);
        menuBar.addMouseMotionListener(dragAdapter);
        // </editor-fold>

        //<editor-fold desc="Experimental Features">
        JPanel mainPanel = new JPanel(new BorderLayout(10,10));
        mainPanel.setBackground(panelColor);

        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createBevelBorder(BevelBorder.RAISED),
                BorderFactory.createEmptyBorder(10,10,10,10)
        ));

        // Section Box
        JPanel formGroup = new JPanel(new GridLayout(3, 2, 8, 8));
        formGroup.setBackground(panelColor);
        TitledBorder groupBorder = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
                " [ Experimental Features ] "
        );
        groupBorder.setTitleFont(new Font("Monospaced", Font.BOLD, 12));
        formGroup.setBorder(groupBorder);

        // Objects Here:
        // Countdown
        JLabel countdownLabel = createLabel(" Days until ?: ");
        Config loadedConfig = Config.loadFile(gson, new File(Config.configFile),false);
        // Check if config file failed to load
        if(loadedConfig != null) {
            String message = loadedConfig.getCountdownMessage();
            // Fallback message
            if(message == null) {
                message = "Config file is missing countdownMessage";
            }
            countdownLabel.setText("  Days until " + message + ": ");
        } else {
            countdownLabel.setText("Error: Could not load configuration.");
            System.err.println("[ERROR] Could not load configuration file");
        }
        JTextField countdownField = createTextField("Countdown here!");
        countdownField.setEditable(false);
        countdownField.setFocusable(false);
        if(loadedConfig != null) {
            countdownField.setText(dayCountdown(loadedConfig));
        } else {
            System.err.println("[ERROR] Could not load configuration file");
        }

        formGroup.add(countdownLabel);
        formGroup.add(countdownField);

        // RNG game
        JTextField outputField = createTextField(" Output");
        JButton randGameBtn = createButton("[ Random Game ]");
        randGameBtn.addActionListener(e -> {
            // Fetch text
            String text = MainFrame.GUILinker(gson, "rngGame");
            // Update text in output
            outputField.setText(" " + text);
        });

        formGroup.add(randGameBtn);
        formGroup.add(outputField);
        mainPanel.add(formGroup, BorderLayout.NORTH);

        // Open config
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(panelColor);
        JButton configButton = createButton("[Config");
        configButton.addActionListener(e -> {
            // Check if configWindow is open
            if(theConfigWindow == null){
                theConfigWindow = new ConfigWindow(gson);
            } else {
                System.out.println(LogColors.YELLOW + LogColors.BOLD + "[WARN] ConfigWindow already open" + LogColors.RESET);
            }
        });
        buttonPanel.add(configButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        rootPanel.add(mainPanel, BorderLayout.CENTER);

        //</editor-fold>

        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(menuFont);
        label.setForeground(Color.BLACK);
        return label;
    }

    private JTextField createTextField(String text) {
        JTextField field = new JTextField(text);
        field.setFont(menuFont);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        return field;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(menuFont);
        button.setBackground(panelColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createBevelBorder(BevelBorder.RAISED),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static String dayCountdown(Config loadedConfig) {
        String days = "";
        LocalDate today = LocalDate.now();
        LocalDate targetDate = loadedConfig.getCountdownEnd();
        if(targetDate != null) {
            long daysLeft = ChronoUnit.DAYS.between(today, targetDate);
            if (daysLeft > 0) {
                days = String.valueOf(daysLeft);
            } else if (daysLeft == 0) {
                days = String.valueOf(daysLeft);
                System.out.println(LogColors.BLUE + LogColors.BOLD + "Countdown ended"+ LogColors.RESET);
            } else {
                days = String.valueOf(daysLeft);
                System.out.println(LogColors.BLUE + LogColors.BOLD + "Countdown date has already passed" + LogColors.RESET);
            }
        }
        return days;
    }

    // Links functions to buttons
    public static String GUILinker(Gson gson , String type) {
        //<editor-fold desc="rngGame Defaults">
        String[] rngDefault = {"Aegis Defenders",
                "A.R.D. Alien Removal Division",
                "ATLYSS",
                "Hytale",
                "Halls of Torment",
                "Aviators",
                "Backrooms: Escape Together",
                "Bleed Runner",
                "Call of Juarez Gunslinger",
                "Celeste",
                "Dark Sector",
                "Darksiders",
                "Darksiders Warmastered Edition",
                "Deadlock",
                "Deep Rock Galactic",
                "FIGHT KNIGHT",
                "Half-Life",
                "ISLANDERS",
                "Jagged Alliance Gold",
                "Metro 2033",
                "Metro: Last Light Complete Edition",
                "Monster Hunter Stories 2: Wings of Ruin Trial Version",
                "Neglected",
                "Nightmare Kart",
                "Ninja Kiwi Archive",
                "PEAK",
                "Plants vs. Zombies™ Garden Warfare",
                "Spiritfarer®: Farewell Edition",
                "Steel Assault",
                "STEINS;GATE",
                "Sumire",
                "The Last Train",
                "Titan Quest Anniversary Edition",
                "Undertale",
                "V Rising",
                "Voice of Cards: The Isle Dragon Roars Demo",
                "Warhammer 40,000: Dawn of War II",
                "Warhammer 40,000: Gladius - Relics of War",
                "Warhammer: Vermintide 2",
                "でびるコネクション (Devil Connection)",
                "Aethermancer Demo",
                "Alien Swarm: Reactive Drop",
                "Among Us",
                "ASTRONEER",
                "Avorion",
                "Axis & Allies 1942 Online",
                "Baldur's Gate 3",
                "Barotrauma",
                "Battlestar Galactica Deadlock",
                "Blackwake",
                "Company of Heroes 2",
                "Conqueror's Blade",
                "Content Warning",
                "Counter-Strike 2",
                "Creativerse",
                "DEFCON",
                "Diesel Knights Playtest",
                "Divinity: Original Sin 2",
                "Dune: Spice Wars",
                "Enlisted",
                "Garry's Mod",
                "Generation Zero®",
                "Golf With Your Friends",
                "Grand Theft Auto V Enhanced",
                "Human Fall Flat",
                "Just Cause 3",
                "Kingdoms and Castles",
                "Left 4 Dead 2",
                "Magellania",
                "Marvel Rivals",
                "Next Day: Survival",
                "Nuclear Nightmare",
                "Path of Exile",
                "Plague Inc: Evolved",
                "Plants vs. Zombies: Game of the Year",
                "Portal",
                "Portal 2",
                "Purrgatory",
                "R.E.P.O.",
                "Robocraft",
                "SAS: Zombie Assault 4",
                "Satisfactory",
                "SCP: Containment Breach Multiplayer",
                "SCP: Secret Laboratory",
                "Scrap Mechanic",
                "Space Engineers",
                "Spore",
                "Squad",
                "STAR WARS Jedi: Fallen Order™",
                "Stick Fight: The Game",
                "Stormworks: Build and Rescue",
                "Subnautica",
                "Sven Co-op",
                "Terraria",
                "THE FINALS",
                "The Mean Greens - Plastic Warfare",
                "Unturned",
                "War Selection",
                "World of Tanks",
                "World of Warships",
                "Minecraft - Survival",
                "Minecraft - Hypixel",
                "Legendary Tales",
                "No Man's Sky",
                "Diablo 2",
                "Starcraft 2",
                "Starcraft 1",
                "Rain World",
                "Outer Wilds",
                "Iron Lung",
                "Mohrta",
                "Beat Saber",
                "Underdogs",
                "Nine Sols",
                "Moss VR 2",
                "Heroes of the Storm",
                "World of Warcraft"};
        //</editor-fold>
        switch(type){
            case "rngGame" -> {
                // Load JSON
                String[] rngGame = rngDefault;
                if(new File("rngGame.data").isFile()){
                    try(FileReader reader = new FileReader("rngGame.data")){
                        rngGame = gson.fromJson(reader, String[].class);
                    } catch (IOException e){
                        System.err.println("Failed to read configuration file: " + e.getMessage());
                    }
                } else {
                    try (FileWriter file = new FileWriter("rngGame.data")) {
                        file.write(gson.toJson(rngDefault));
                        System.out.println(LogColors.GREEN + LogColors.BOLD + "Successfully written defaults to " + "rngGame.data" + LogColors.RESET);
                    } catch (IOException e) {
                        System.err.println("Failed to write JSON to file: " + e.getMessage());
                    }
                }

                // Use defaults if file is empty/corrupted
                if(rngGame == null || rngGame.length == 0){
                    rngGame = rngDefault;
                }
                int r = rand.nextInt(rngGame.length);
                System.out.printf("%n%s | Number: %d%n", rngGame[r], r);
                return rngGame[r];
            }
            default -> System.out.println("Invalid GUI option");
        }
        return null;
    }    
}
