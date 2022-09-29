# Minecraft Spigot Plugin Utils
My utils for my Spigot plugins. It contains some useful Util methods and classes that I commonly use, and they make my development easier and reduce code duplications across plugins.

# Tools

[comment]: <> (Todo: It would be useful to have this as javadoc)

## Entity
Utilities for entities.

### NearPlayerEntitySpawner
Spawns an entity near the player.

Usage:
```
NearPlayerEntitySpawner.spawnEntityNearPlayer(player, EntityType.PILLAGER, 5, 10, "Destroyer", true);
```

In the example, a Pillager will spawn around 5-10 visible blocks away from the player.

The static methods `spawnEntityNearPlayer` from `NearPlayerEntitySpawner` takes in the following arguments: 
- `Player player` - The player near whom the entity will be spawned
- `EntityType entityType` - Type of entity (Pillager, Creeper, etc.)
- `int minimumBlocksAway` - The nearest block around the player where entity can be spawned
- `int maximumBlocksAway` - The furthest block around the player where entity can be spawned
- `String customEntityName` - A custom name for an entity (optional - can send `null`)
- `boolean broadcastMessage` - Flag that broadcast the message about spawned entity to players on the server

There is an overloaded method `spawnEntityNearPlayer` that does not take `customEntityName` as a parameter.

## Logging
Logging utilities.

### LoggingUtils
Just a wrapper around Bukkit logger. Nothing fancy, just makes printing `INFO` logs shorter and easier.

Uasge:
```
LoggingUtils.log("Something to log...");
```

Look at the implementation. 
If you pass just a string (message) as an argument it's an `INFO` log automatically.
You have 2 more overrides: 
- one that takes the logging level and a message as arguments
- one that takes the logging level, message and a `Throwable` as arguments

## Message
Utilities for writing messages to players and for broadcasting them on the server.

### TableGenerator
Generate messages in a table-like fashion. 

Usage: 
```
private void displayPlayerInfo(Player sender, List<NPC> players) {
        // player ID, player name, player's location
        TableGenerator tableGenerator = new TableGenerator(TableGenerator.Alignment.LEFT, 
        TableGenerator.Alignment.LEFT, 
        TableGenerator.Alignment.LEFT);
        tableGenerator.addRow("player ID", "player name", "player's location");
        players.forEach(player -> {
            if (player.getEntity() != null) {
                Location loc = player.getEntity().getLocation();
                String location = format("%s / %s / %s", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                tableGenerator.addRow(Integer.toString(player.getId()), player.getName(), location);
            } else {
                LoggingUtils.log("There was no Entity found for player " + player.getName());
            }
        });

        tableGenerator.generate().forEach(sender::sendMessage);
    }
```

In this example alignment is `LEFT`. Possible values: `LEFT`, `RIGHT`, and `CENTER`.

You also have `DASHES_LINE_DELIMITER` at your disposal, which is a string of 20 dashes. Useful to separate large chunks of text for example.

Create a `TableGenerator` object with `Alignment` objects in the constructor. The amount of alignments means the amount of columns. Then add rows by calling `addRow` method on the `TableGenerator` object. Add as many rows as you need. Each row needs to have a correct number of columns (defined when instantiating the `TableGenerator` object). In the end call the `generate` method on the `TableGenerator` object.

## Timer
Utilities for time dependant tasks such as executing a method every minute.

### CountdownTimer
Countdown timer using the Runnable interface. Timer is based on seconds.

Usage: 
```
JavaPlugin myPlugin = getMyPluginReference(); // Dummy
void methodThatUsesTimerFor60Seconds() {
    CountdownTimer countdownTimer = new CountdownTimer(myPlugin, 60,
                                                       () -> myPlugin.getServer().broadcastMessage("This code is executed before the timer starts!"),
                                                       () -> myPlugin.getServer().broadcastMessage("This code is executed after the timer ends!"),
                                                       timer -> myPlugin.getServer().broadcastMessage("This is executed every second. Amount of seconds left: " + timer.getSecondsLeft()));
    countdownTimer.scheduleTimer();
}
```

`CountdownTimer` constructor takes 5 parameters: 
- reference to the plugin
- number of seconds it will last be running for (`60` in the example)
- code that will be executed before the timer (`Runnable`)
- code that will be executed after the timer (`Runnable`)
- code that will be executed every second (`Consumer`)

After the object is instantiated, the `scheduleTimer` must be called to trigger the timer.