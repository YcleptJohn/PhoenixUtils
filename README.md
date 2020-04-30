# PhoenixUtils
This plugin was built as a library for other bukkit plugins. At the time there was a large push to identifying players by UUID rather than names but this required a network lookup. My intention here was to provide a cached-hashmap of currently logged in players so the lookup only has to happen onConnection, rather than every time an operation happens against a player. 
