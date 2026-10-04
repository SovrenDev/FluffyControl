package fluffycontrol;

import com.google.gson.Gson;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * date: 2026-09-26
 * @author Sovren
 */
public class ConfigWindow extends JFrame{
    private Point mouseClickPoint; // Store initial mouse position on click

    public ConfigWindow(Gson gson) {
        setTitle("Fluffy Control Config Panel v" + FluffyControl.version);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(720, 600);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setResizable(false);

        Config loadedConfig = Config.loadFile(gson, new File(Config.configFile), true);

        JPanel rootPanel = new JPanel(new BorderLayout(10, 10));
        rootPanel.setBackground(MainFrame.backgroundColor);
        rootPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(rootPanel);

        //<editor-fold desc="Top Menu Bar">
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(MainFrame.panelColor);
        menuBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        JLabel label = new JLabel("  " + "FluffyControl.Config");
        label.setFont(MainFrame.menuFont);
        menuBar.add(label);

        menuBar.add(Box.createHorizontalGlue());

        // Window buttons
        JButton hideBtn = new JButton("-");
        hideBtn.setFont(MainFrame.menuFont);
        hideBtn.setFocusPainted(false);
        hideBtn.addActionListener(e -> fluffycontrol.ConfigWindow.this.setExtendedState(fluffycontrol.ConfigWindow.this.getExtendedState() | Frame.ICONIFIED));
        menuBar.add(hideBtn);

        JButton closeBtn = new JButton("X");
        closeBtn.setFont(MainFrame.menuFont);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> {
            MainFrame.theConfigWindow = null; // Makes sure that config window can be created again
            ConfigWindow.this.dispose();});
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

        //<editor-fold desc="Application Settings">
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(MainFrame.panelColor);

        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createBevelBorder(BevelBorder.RAISED),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Section Box
        JPanel formGroup = new JPanel(new GridLayout(5, 2, 4, 3));
        formGroup.setBackground(MainFrame.panelColor);
        TitledBorder groupBorder = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
                " [ Application Settings ] "
        );
        groupBorder.setTitleFont(new Font("Monospaced", Font.BOLD, 12));
        formGroup.setBorder(groupBorder);


        // Objects Here:
        // Autostart config
        /* UI and saving code for future autostart feature
        JLabel autoStartLabel = createLabel("    AutoStart:");
        Boolean[] check = {true,false};
        JList<Boolean> autoStartList = new JList<>(check);
        autoStartList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        autoStartList.setSelectedValue(loadedConfig.isAutoStart(), true); // pre-selects the last selection

        autoStartList.addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()){
                Boolean strAutoStart = autoStartList.getSelectedValue();
                if(strAutoStart != null) {
                    System.out.println(LogColors.BLUE + LogColors.BOLD + "Selected: " + strAutoStart + LogColors.RESET);
                    // Change to auto start here / remove auto start here
                    loadedConfig.setAutoStart(strAutoStart); // Temp save
                }
            }
        });
        */

        // Bedtime config
        JLabel bedtimeLabel = createLabel("    Bedtime:");
        JTextField bedtimeField = createTextField("23,30");
        LocalTime bedtimeTime = loadedConfig.getBedtime();
        if (bedtimeTime != null) { // pre-selects the last selection
            bedtimeField.setText(bedtimeTime.toString());
        } else {
            bedtimeField.setText("09:00");
        }

        bedtimeField.addActionListener(e -> {
            String input = bedtimeField.getText().trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            try{
                LocalTime validatedTime = LocalTime.parse(input, formatter);
                System.out.println(LogColors.BLUE + LogColors.BOLD + "Time set: " + validatedTime + LogColors.RESET);
                //loadedConfig.setBedtime(validatedTime); // Temp save
            } catch (DateTimeException ex) {
                bedtimeField.setText("24-hour HH:mm format only!");
                System.out.println(LogColors.YELLOW + LogColors.BOLD + "[WARN] Invalid format. 24-Hour HH:mm format only"  + LogColors.RESET);
            }
        });


        // Wake up config
        JLabel wakeupLabel = createLabel("    Wake-up:");
        JTextField wakeupField = createTextField("9,0");
        LocalTime wakeupTime = loadedConfig.getWakeup();
        if (wakeupTime != null) { // pre-selects the last selection
            wakeupField.setText(wakeupTime.toString());
        } else {
            wakeupField.setText("09:00");
        }

        wakeupField.addActionListener(e -> {
            String input = wakeupField.getText().trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            try{
                LocalTime validatedTime = LocalTime.parse(input, formatter);
                System.out.println(LogColors.BLUE + LogColors.BOLD + "Time set: " + validatedTime + LogColors.RESET);
                //loadedConfig.setWakeup(validatedTime); // Temp save
            } catch (DateTimeException ex) {
                wakeupField.setText("24-hour HH:mm format only!");
                System.out.println(LogColors.YELLOW + LogColors.BOLD + "[WARN] Invalid format. 24-Hour HH:mm format only"  + LogColors.RESET);
            }
        });

        // Interface config
        JLabel interfaceTypeLabel = createLabel("    Network interface:");
        String[] check = {"Ethernet", "Wi-Fi"};
        JList<String> interfaceTypeList = new JList<>(check);
        interfaceTypeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        interfaceTypeList.setSelectedValue(loadedConfig.getINTERFACE_TYPE(), true); // pre-selects the last selection

        interfaceTypeList.addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()){
                String strInterfaceType = interfaceTypeList.getSelectedValue();
                if(strInterfaceType != null) {
                    System.out.printf("%s%s%nSelected Interface: %s %s%n",LogColors.BLUE, LogColors.BOLD, strInterfaceType, LogColors.RESET);
                    loadedConfig.setINTERFACE_TYPE(strInterfaceType); // Temp save
                }
            }
        });

        // Countdown Days
        JLabel countdownMessageLabel = createLabel("    Countdown message: ");
        JTextField countdownMessageField = createTextField("Black Friday");
        // Saved string is shown on start
        String countdownStr = loadedConfig.getCountdownMessage();
        if (countdownStr != null) {
            countdownMessageField.setText(countdownStr);
        }

        JLabel countdownLabel = createLabel("    Countdown date: ");
        JTextField countdownField = createTextField("2026-11-27");
        LocalDate countdownEnd = loadedConfig.getCountdownEnd();
        if (countdownEnd != null) { // pre-selects the last selection
            countdownField.setText(countdownEnd.toString());
        } else {
            countdownField.setText("2026-11-27");
        }

        countdownField.addActionListener(e -> {
            String input = countdownField.getText().trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            try{
                LocalDate validatedTime = LocalDate.parse(input, formatter);
                System.out.println(LogColors.BLUE + LogColors.BOLD + "Date set: " + validatedTime + LogColors.RESET);
            } catch (DateTimeException ex) {
                countdownField.setText("yyyy-MM-dd format only!");
                System.out.println(LogColors.YELLOW + LogColors.BOLD + "[WARN] Invalid format. yyyy-MM-dd format only"  + LogColors.RESET);
            }
        });

        //formGroup.add(autoStartLabel);
        //formGroup.add(autoStartList);
        formGroup.add(bedtimeLabel);
        formGroup.add(bedtimeField);
        formGroup.add(wakeupLabel);
        formGroup.add(wakeupField);
        formGroup.add(interfaceTypeLabel);
        formGroup.add(interfaceTypeList);
        formGroup.add(countdownMessageLabel);
        formGroup.add(countdownMessageField);
        formGroup.add(countdownLabel);
        formGroup.add(countdownField);
        mainPanel.add(formGroup, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(MainFrame.panelColor);
        JButton saveButton = createButton("[Save]");
        saveButton.addActionListener(e -> {
            // Save code here then ->
            boolean canSave = true;

            String input = bedtimeField.getText().trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            try{
                LocalTime validatedTime = LocalTime.parse(input, formatter);
                System.out.println(LogColors.BLUE + LogColors.BOLD + "Time set: " + validatedTime + LogColors.RESET);
                loadedConfig.setBedtime(validatedTime);
            } catch (DateTimeException ex) {
                bedtimeField.setText("24-hour HH:mm format only!");
                System.out.println(LogColors.YELLOW + LogColors.BOLD + "[WARN] Invalid format. 24-Hour HH:mm format only"  + LogColors.RESET);
                canSave = false;
            }
            String input2 = wakeupField.getText().trim();
            try{
                LocalTime validatedTime = LocalTime.parse(input2, formatter);
                System.out.println(LogColors.BLUE + LogColors.BOLD + "Time set: " + validatedTime + LogColors.RESET);
                loadedConfig.setWakeup(validatedTime);
            } catch (DateTimeException ex) {
                wakeupField.setText("24-hour HH:mm format only!");
                System.out.println(LogColors.YELLOW + LogColors.BOLD + "[WARN] Invalid format. 24-Hour HH:mm format only"  + LogColors.RESET);
                canSave = false;
            }
            String input3 = countdownField.getText().trim();
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            try {
                LocalDate validatedTime = LocalDate.parse(input3, formatter);
                System.out.println(LogColors.BLUE + LogColors.BOLD + "Date set: " + validatedTime + LogColors.RESET);
                loadedConfig.setCountdownEnd(validatedTime);
            } catch (DateTimeException ex) {
                countdownField.setText("yyyy-MM-dd format only!");
                System.out.println(LogColors.YELLOW + LogColors.BOLD + "[WARN] Invalid format. yyyy-MM-dd format only"  + LogColors.RESET);
                canSave = false;
            }

            if(countdownStr != null) {
                String currentText = countdownMessageField.getText().trim();
                loadedConfig.setCountdownMessage(currentText);
                System.out.println(LogColors.BLUE + LogColors.BOLD + "Countdown label set: " + currentText + LogColors.RESET);
            }

            // Actual save
            if (canSave){
            String jsonOutput = gson.toJson(loadedConfig);
            Config.writeToFile(new File(Config.configFile), jsonOutput);
            MainFrame.theConfigWindow = null; // Makes sure that config window can be created again
            ConfigWindow.this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid format.","Warning", JOptionPane.ERROR_MESSAGE);
            }
        });


        JButton cancelButton = createButton("[Cancel]");
        cancelButton.addActionListener(e -> {
            MainFrame.theConfigWindow = null; // Makes sure that config window can be created again
            ConfigWindow.this.dispose();
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        rootPanel.add(mainPanel, BorderLayout.CENTER);
        //</editor-fold>

        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(MainFrame.menuFont);
        label.setForeground(Color.BLACK);
        return label;
    }

    private JTextField createTextField(String text) {
        JTextField field = new JTextField(text);
        field.setFont(MainFrame.menuFont);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        return field;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(MainFrame.menuFont);
        button.setBackground(MainFrame.panelColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createBevelBorder(BevelBorder.RAISED),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}
