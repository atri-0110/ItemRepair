# ItemRepair Plugin for AllayMC

A plugin that allows players to repair damaged items and tools using experience levels.

## Features

- **Repair Items in Hand**: Use `/repair` to repair the item you're currently holding
- **Repair Cost Calculator**: Use `/repair check` to see how much XP it will cost without repairing
- **Smart Cost Calculation**: Repair cost is based on the damage percentage of the item
- **Supports All Tools and Armor**: Works with all repairable items (pickaxes, swords, armor, elytra, etc.)
- **Permission System**: Use `itemrepair.use` permission to control access

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/repair` | Repair the item in your hand | `itemrepair.use` |
| `/repair check` | Check repair cost without repairing | `itemrepair.use` |

## Repair Cost Formula

The repair cost is calculated based on the damage percentage of the item:

```
Cost = ceil(damage_percent * 10) experience levels
```

- Minimum cost: 1 experience level
- Maximum cost: 10 experience levels (for completely broken items)

Example:
- Diamond Pickaxe with 50% damage → 5 experience levels
- Iron Sword with 20% damage → 2 experience levels

## Requirements

- AllayMC API 0.24.0
- Java 21
- No external dependencies

## Installation

1. Download the latest `ItemRepair-X.X.X-shaded.jar` from [Releases](https://github.com/atri-0110/ItemRepair/releases)
2. Place the JAR file in your AllayMC server's `plugins` folder
3. Restart the server
4. The plugin will automatically load

## Configuration

No configuration file is required. The plugin works out of the box with default settings.

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `itemrepair.use` | Allows using /repair command | OP only |

## Usage Example

```
/repair
> [ItemRepair] Item repaired! 5 experience levels spent.

/repair check
> [ItemRepair] Repair Cost: 5 experience levels (You have: 12)
```

## Development

### Build from Source

```bash
cd ~/clawd/allaymc-plugins/ItemRepair
./gradlew clean shadowJar -Dorg.gradle.jvmargs="-Xmx3g" --no-daemon
```

The compiled JAR will be in `build/libs/`.

### Project Structure

```
ItemRepair/
├── src/main/java/org/allaymc/itemrepair/
│   ├── ItemRepairPlugin.java      # Main plugin class
│   ├── command/
│   │   └── RepairCommand.java     # Command handler
│   └── manager/
│       └── RepairManager.java     # Repair logic
├── build.gradle.kts                # Gradle build configuration
├── settings.gradle.kts             # Gradle settings
└── README.md                       # This file
```

## Contributing

Contributions are welcome! Feel free to submit issues or pull requests on GitHub.

## License

This plugin is open source and available under the MIT License.

## Author

- **atri-0110** - [GitHub](https://github.com/atri-0110)

## Acknowledgments

- Built for the [AllayMC](https://github.com/AllayMC/Allay) server software
- Inspired by the vanilla Minecraft anvil repair mechanic
