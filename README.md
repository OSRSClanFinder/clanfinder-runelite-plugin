# ClanFinder RuneLite Plugin

ClanFinder for RuneLite is a read-only companion plugin for the ClanFinder website. It fetches approved public clan
listings, lets players filter them in a RuneLite sidebar panel, copies clan chat names to the clipboard, and opens the
website listing after an explicit user action.

Member totals shown in the plugin come from the ClanFinder website listing data. When a listing is connected to Wise Old
Man group stats, the site-provided WOM total is shown; otherwise the manually entered listing count is shown. The plugin
does not detect or display live online status for clans or clan members.

## Scope

- Reads `GET /api/v1/clans` from the ClanFinder website.
- Optionally posts an anonymous plugin-session heartbeat to `POST /api/v1/active-users`; this is disabled by default.
- Does not request RuneScape credentials, automate gameplay, send chat messages, join clans, scrape private clan data,
  detect online clan/member status, or interact with OSRS clan systems.
- Uses the existing ClanFinder website as the source of truth for listings, ownership, moderation, and reporting.

## Development

Use Java 11 for RuneLite plugin development.

```bash
cd clanfinder-runelite-plugin
./gradlew test
./gradlew run
```

The default API and website base URLs point at `https://osrsclanfinder.com`, so developer builds use the live approved
listings by default. To test against a local Next.js app, change both URLs in the RuneLite config panel to
`http://localhost:3000`.

## Plugin Hub Readiness

This folder follows the RuneLite external plugin shape:

- `runelite-plugin.properties` declares display name, support URL, author, tags, plugin class, version, and
  `build=standard`.
- `plugin-hub-submission.properties` contains the Plugin Hub marker template, including the required external-data
  warning for osrsclanfinder.com requests.
- `build.gradle` uses `runeLiteVersion = 'latest.release'` and Java 11 bytecode.
- `src/test/java/com/clanfinder/ClanFinderPluginTest.java` launches RuneLite in developer mode.
- Banner images are expected to be served as PNG or JPEG, avoiding third-party ImageIO dependencies during Plugin Hub
  review.
- `LICENSE` uses BSD 2-Clause, matching RuneLite Plugin Hub guidance.

## Commercial Checklist

- Keep the default API and website URLs on the HTTPS production origin.
- Add API cache headers and server-side monitoring for `/api/v1/clans`.
- Serve plugin banner thumbnails as PNG or JPEG.
- Keep the plugin read-only and user-action driven.
- Publish a clear privacy policy for external ClanFinder data requests and the optional anonymous plugin usage count.
- Keep `support=` pointed at a public RuneLite plugin support/disclosure page.
- Add a GitHub issue tracker after the plugin source is moved into its public repository.
- Submit to `runelite/plugin-hub` with the repository URL, a full commit hash, and the `warning=` line from
  `plugin-hub-submission.properties` after the first stable release.
