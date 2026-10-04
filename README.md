# FluffyControl

Java application designed to help me keep a healthy sleep schedule. With some extra experimental features.
It alerts user when it's time to head to bed or turn off the computer. It will automatically disable users network connection to prevent late night browsing. When daylight returns it automatically restores the connection

## Main Features
- **Visual Reminder**: Opens a warning window when it's time to turn off the computer.
- **Automated Disconnection**: Disables a specified network interface (Ethernet but can be configured to change Wi-Fi instead) using `nmcli`.
- **Automatic Reconnection**: Restores the internet connection automatically in the morning.

## How It Works

By default, the program enforces the following schedule:
- **Off-hours (Bedtime)**: `23:30` to `09:00` (Network disabled + Warning popup)
- **On-hours (Daytime)**: `09:00` to `23:30` (Network enabled)

## Requirements & Dependencies

- Linux
- NetworkManager must be installed and running
- Java 25
- Gson: `com.google.code.gson:gson:2.14.0`

## Configuration

Once started, make sure to set your correct network interface type in the config tab or manually in config.json:
```json
{
  "INTERFACE_TYPE": "Wi-Fi"
}
```
*You can find your active network interfaces by running `nmcli device` in your terminal.*

## Experimental features
- **Days Countdown**: Counts the days left until a specified date.
- **rngGame**: Games can be added or modified in the `rngGame.data` file, which is created when the user presses the Random Game button.
