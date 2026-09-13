Run `./gradlew runGameTestServer` (or the IDE's GameTestServer configuration) to check dungeon placement.

The tests cover doorway clearance, ground support, flooded and buried entrances, all four horizontal orientations, deterministic room placement, and actual placement into generated Sift terrain. Test functions register only in development.

Minecraft's game test server normally omits custom dimensions. The test-only pack in `resources/dungeon_world` adds Sift to its flat world preset. Keep that preset's `wellspring:sift` entry in sync with `src/main/resources/data/wellspring/dimension/sift.json` when changing the dimension's generator or biome source. This pack is not included in the mod's main resources.

The entrance checks match the current 7 x 7 x 9 anchor: its outward-facing jigsaw is at the center of the doorway floor and is named `minecraft:empty`, with pool `minecraft:empty`. Its opening is five blocks wide and five blocks high. If the anchor changes size or its marker moves, update `CaveWallEntrance` and the structure JSON's `entrance_jigsaw` accordingly. The hallway jigsaw continues to use `wellspring:dungeon/pieces`.

`worldgen/structure/dungeon.json` controls the entrance floor search range (`min_y`/`max_y`), jigsaw connection depth (`size`), and maximum layout radius. `terrain_adaptation` must remain `none` so terrain wrapping cannot seal the doorway. Unsuitable locations and layouts that obstruct the outside approach are skipped.
