# ClanFinder RuneLite Plugin

ClanFinder for RuneLite is a read-only companion plugin for the ClanFinder website. It fetches approved public clan
listings, lets players filter them in a RuneLite sidebar panel, copies clan chat names to the clipboard, and opens the
website listing after an explicit user action.

Member totals shown in the plugin come from the ClanFinder website listing data. When a listing is connected to Wise Old
Man group stats, the site-provided WOM total is shown; otherwise the manually entered listing count is shown. The plugin
does not detect or display live online status for clans or clan members.

## What it does

- Reads `GET /api/v1/clans` from the ClanFinder website.
- Opens public clan listing pages only after an explicit user action.
- Does not request RuneScape credentials, automate gameplay, send chat messages, join clans, scrape private clan data,
  detect online clan/member status, or interact with OSRS clan systems.
- Uses the ClanFinder website as the source of truth for listings, ownership, moderation, and reporting.

## Privacy and support

The plugin loads public listing JSON, banner thumbnails, and event data from `osrsclanfinder.com`. External requests may
expose your IP address to that service. It does not send your RuneScape account name, clan membership, chat messages,
or live online status.

Privacy and support information is available at:

```txt
https://osrsclanfinder.com/runelite-plugin
```

## Development

Use Java 11 for RuneLite plugin development.

```bash
cd clanfinder-runelite-plugin
./gradlew test
./gradlew run
```

Developer builds use the live approved listings from `https://osrsclanfinder.com` by default.

## Plugin Hub

The plugin uses the standard RuneLite external plugin layout:

- `runelite-plugin.properties` declares display name, support URL, author, tags, plugin class, version, and
  `build=standard`.
- `build.gradle` uses `runeLiteVersion = 'latest.release'` and Java 11 bytecode.
- `src/test/java/com/clanfinder/ClanFinderPluginTest.java` launches RuneLite in developer mode.
- Banner thumbnails are served as PNG or JPEG, avoiding third-party ImageIO dependencies.
- `LICENSE` uses BSD 2-Clause, matching RuneLite Plugin Hub guidance.
