package com.clanfinder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.junit.Test;

public class ClanFinderApiClientTest
{
    private final Gson gson = new Gson();

    @Test
    public void normalizesBaseUrl()
    {
        assertEquals("https://osrsclanfinder.com", ClanFinderApiClient.normalizeBaseUrl(""));
        assertEquals("https://example.com", ClanFinderApiClient.normalizeBaseUrl("https://example.com///"));
        assertEquals("https://osrsclanfinder.com", ClanFinderApiClient.normalizeBaseUrl("osrsclanfinder.com/"));
    }

    @Test
    public void buildsEncodedClanSearchUrl()
    {
        ClanFinderApiClient client = new ClanFinderApiClient("https://example.com/", gson);
        ClanSearchQuery query = new ClanSearchQuery("learner raids", "PvM", "AU / NZ", 25);

        assertEquals(
            "https://example.com/api/v1/clans?page=1&limit=25&search=learner+raids&type=PvM&region=AU+%2F+NZ",
            client.buildClansUrl(query)
        );
    }

    @Test
    public void buildsPaginatedClanSearchUrl()
    {
        ClanFinderApiClient client = new ClanFinderApiClient("https://example.com/", gson);
        ClanSearchQuery query = new ClanSearchQuery("", "", "", 3, 25);

        assertEquals(
            "https://example.com/api/v1/clans?page=3&limit=25",
            client.buildClansUrl(query)
        );
    }

    @Test
    public void buildsEncodedClanDetailUrl()
    {
        ClanFinderApiClient client = new ClanFinderApiClient("https://example.com/", gson);

        assertEquals(
            "https://example.com/api/v1/clans/tangle+crew",
            client.buildClanUrl("tangle crew")
        );
    }

    @Test
    public void resolvesOnlyClanFinderAssetUrls()
    {
        ClanFinderPlugin plugin = new ClanFinderPlugin();

        assertEquals(
            "https://osrsclanfinder.com/uploads/banner.png",
            plugin.resolveAssetUrl("/uploads/banner.png")
        );
        assertEquals(
            "https://osrsclanfinder.com/api/v1/clans/banner-thumbnail?src=%2Fuploads%2Fbanner.webp",
            plugin.resolveAssetUrl("https://osrsclanfinder.com/api/v1/clans/banner-thumbnail?src=%2Fuploads%2Fbanner.webp")
        );
        assertEquals("", plugin.resolveAssetUrl("https://example.com/banner.png"));
        assertEquals("", plugin.resolveAssetUrl("http://osrsclanfinder.com/banner.png"));
    }

    @Test
    public void includesDiscordLogoResource() throws Exception
    {
        try (InputStream stream = ClanFinderPanel.class.getResourceAsStream("/discord-logo.png"))
        {
            assertNotNull(stream);
        }
    }

    @Test
    public void keepsCompletionistIconSizedForPluginHub() throws Exception
    {
        try (InputStream stream = ClanFinderPanel.class.getResourceAsStream("/game-icons/completionist.png"))
        {
            assertNotNull(stream);
            BufferedImage image = ImageIO.read(stream);

            assertNotNull(image);
            assertTrue(image.getWidth() <= 32);
            assertTrue(image.getHeight() <= 32);
            assertTrue(image.getWidth() * image.getHeight() * 4 <= 100_000);
        }
    }
}
