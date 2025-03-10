# PortalLock

PortalLock is a Minecraft plugin. It prevents players from speed running by locking portals.

## Features

- Locks portals to stop speed running
- Can prevent entering and leaving dimensions
- Can be limited by permissions
- Can be auto opened by date and time
- Customizable sounds for events
- Supports localization

## Installation

1. Download the latest release from the [releases page](https://github.com/TavstalDev/PortalLock/releases/latest).
2. Place the `.jar` file in the `plugins` folder of your Minecraft server.
3. Start the server to generate configuration files.

## Configuration

The configuration file is in the `plugins/PortalLock` folder. Customize settings as needed.

## Commands

- `/portallock` - Main command for PortalLock
- `/portallock reload` - Reloads the plugin
- `/portallock version` - Checks the plugin version
- `/portallock list <page>` - Lists dimensions
- `/portallock info [world] <page>` - Views dimension info
- `/portallock add [world]` - Adds a new dimension
- `/portallock edit [world] [field] [value]` - Edits a dimension
- `/portallock remove [world]` - Removes a dimension

## Permissions

- `portallock.commands.portallock` - Allows use of the `/portallock` command
- `portallock.commands.portallock.reload` - Allows reloading the plugin
- `portallock.commands.portallock.version` - Allows checking the plugin version
- `portallock.commands.portallock.list` - Allows listing dimensions
- `portallock.commands.portallock.info` - Allows viewing dimension info
- `portallock.commands.portallock.add` - Allows adding a new dimension
- `portallock.commands.portallock.edit` - Allows editing a dimension
- `portallock.commands.portallock.remove` - Allows removing a dimension
- `portallock.player` - Basic permissions for players
- `portallock.admin` - Full permissions for admins
- `portallock.*` - All permissions for PortalLock

## License

This project is under the GNU General Public License v3.0. See the `LICENSE` file for details.

## Contact

For issues or feature requests, use the [GitHub issue tracker](https://github.com/TavstalDev/PortalLock/issues).