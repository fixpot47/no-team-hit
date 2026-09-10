# No Team Hit

A lightweight client-side Fabric mod for Minecraft Java Edition 26.2.

Add teammate usernames to a local protected list. Direct melee attacks against protected players are cancelled on the client before the normal attack is sent.

## Features

- Client-side only; teammates and servers do not need the mod.
- Press **O** to open the teammate list (rebindable in Controls).
- Add or remove player names in-game.
- Saves names locally in `config/noteamhit.json`.
- Case-insensitive username matching.
- English and Russian translations.

## Limitations

Version 1.0 protects direct melee attacks only. It does not guarantee protection from sweep damage, arrows, potions, explosions, fire, or other server-side damage sources.

## Requirements

- Minecraft Java Edition 26.2
- Fabric Loader 0.19.5+
- Fabric API for 26.2
- Java 25

## License

MIT
