package com.clanfinder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.awt.Font;
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

    @Test
    public void normalizesUserTextForPluginDisplay()
    {
        assertEquals(
            "Billed PvM Clan | YOU'VE NEVER SEEN SO MUCH TOB & HMT | 🎉 Weekly Events | 🎲 Bingos | Friendly | 💰350+",
            ClanFinderPanel.normalizeDisplayText(
                "<p>Billed PvM Clan | YOU&#39;VE NEVER SEEN SO MUCH TOB &amp; HMT</p>" +
                    "<div>| &#x1F389; Weekly Events | &#127922; Bingos | Friendly | &#x1F4B0;350+</div>"
            )
        );
    }

    @Test
    public void preservesUserTextSpacingForPluginDisplay()
    {
        assertEquals(
            "First line\nSecond line\n\nNext paragraph",
            ClanFinderPanel.normalizeMultilineDisplayText("<p>First line<br>Second line</p><div>Next paragraph</div>")
        );
    }

    @Test
    public void readsRequirementNotesForPluginDisplay()
    {
        ClanRequirements requirements = gson.fromJson(
            "{\"applicationInstructions\":\"Apply on Discord<br>Wait for review\",\"notes\":\"Bring raids gear\\nBe respectful\"}",
            ClanRequirements.class
        );

        assertEquals("Apply on Discord<br>Wait for review", requirements.getApplicationInstructions());
        assertEquals("Bring raids gear\nBe respectful", requirements.getNotes());
    }

    @Test
    public void usesLogicalFontForUserTextFallback()
    {
        Font font = ClanFinderPanel.displayTextFont(Font.PLAIN, 13.6f);

        assertEquals(Font.DIALOG, font.getName());
        assertEquals(13.6f, font.getSize2D(), 0.01f);
        assertTrue(new Font(ClanFinderPanel.emojiFontFamily(), Font.PLAIN, 14).canDisplay(0x1F389));
        assertTrue(new Font(ClanFinderPanel.emojiFontFamily(), Font.PLAIN, 14).canDisplay(0x1F3B2));
        assertTrue(new Font(ClanFinderPanel.emojiFontFamily(), Font.PLAIN, 14).canDisplay(0x1F4B0));
        assertTrue(ClanFinderPanel.hasGeneratedEmojiIcon(0x2694));
        assertTrue(ClanFinderPanel.hasGeneratedEmojiIcon(0x2620));
        assertTrue(ClanFinderPanel.hasGeneratedEmojiIcon(0x1F332));
        assertTrue(ClanFinderPanel.hasGeneratedEmojiIcon(0x1F37B));
        assertTrue(ClanFinderPanel.hasGeneratedEmojiIcon(0x1F389));
        assertTrue(ClanFinderPanel.hasGeneratedEmojiIcon(0x1F3B2));
        assertTrue(ClanFinderPanel.hasGeneratedEmojiIcon(0x1F426));
        assertTrue(ClanFinderPanel.hasGeneratedEmojiIcon(0x1F43C));
        assertTrue(ClanFinderPanel.hasGeneratedEmojiIcon(0x1F49D));
        assertTrue(ClanFinderPanel.hasGeneratedEmojiIcon(0x1F4B0));
        assertTrue(ClanFinderPanel.hasGeneratedEmojiIcon(0x1F4C5));
        assertTrue(ClanFinderPanel.hasGeneratedEmojiIcon(0x1F4C6));
        assertTrue(ClanFinderPanel.hasGeneratedEmojiIcon(0x1F4B5));
        assertTrue(ClanFinderPanel.hasGeneratedEmojiIcon(0x1F4E3));
        assertTrue(ClanFinderPanel.hasGeneratedEmojiIcon(0x1F525));
        assertTrue(ClanFinderPanel.hasGeneratedEmojiIcon(0x1F5E3));
        assertTrue(ClanFinderPanel.hasGeneratedEmojiIcon(0x1F916));
        assertTrue(ClanFinderPanel.hasGeneratedEmojiIcon(0x1F917));
        assertTrue(ClanFinderPanel.hasGeneratedEmojiIcon(0x1F91D));
        assertTrue(ClanFinderPanel.hasGeneratedEmojiIcon(0x1F4CA));
        assertTrue(ClanFinderPanel.hasGeneratedEmojiIcon(0x1F600));
    }
}
