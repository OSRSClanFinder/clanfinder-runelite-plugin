# Commercial Readiness Notes

## Compliance Position

The plugin is intentionally narrow: public directory discovery only. It should remain aligned with RuneLite Plugin Hub
review expectations by avoiding automation, overlays that solve gameplay decisions, hidden telemetry, private account
data collection, and any dependency that is not needed for the directory experience.

Member totals are listing metadata from ClanFinder website data. They may come from Wise Old Man group stats when a clan
has connected those stats on the site, or from a manually entered listing count. They are not live online clan/member
status and the plugin must not imply otherwise.

## Release Gates

1. Production API is deployed over HTTPS.
2. Website terms, privacy policy, and listing rules are live.
3. Anonymous plugin usage count is disclosed, disabled by default, and can be enabled in plugin settings.
4. API failures degrade cleanly in the panel without blocking RuneLite startup.
5. Plugin defaults use the production ClanFinder URL.
6. The submitted Plugin Hub commit builds with Java 11 and `./gradlew test`.
7. The Plugin Hub marker includes the external-data `warning=` line from `plugin-hub-submission.properties`.
8. Clan banner thumbnails used by the plugin are served as PNG or JPEG, not WebP.

## API Contract

The plugin currently depends on:

```txt
GET  /api/v1/clans?page=1&limit=25&search=&type=&region=
POST /api/v1/active-users
```

Keep these endpoints backwards-compatible once the plugin is public. Add fields to responses instead of renaming or
removing existing fields.
