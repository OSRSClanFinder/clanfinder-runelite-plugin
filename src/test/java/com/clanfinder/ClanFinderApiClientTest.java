package com.clanfinder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.awt.Component;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
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
    public void retriesTransientSearchFailure() throws Exception
    {
        String body = "{\"data\":[{\"slug\":\"one\",\"name\":\"One Clan\"}],\"pagination\":{\"page\":1,\"limit\":25,\"total\":1}}";
        try (ServerSocket server = new ServerSocket(0))
        {
            AtomicReference<Exception> serverFailure = new AtomicReference<>();
            Thread serverThread = new Thread(() -> serveFailedThenSuccessfulResponse(server, body, serverFailure));
            serverThread.setDaemon(true);
            serverThread.start();

            ClanFinderApiClient client = new ClanFinderApiClient("http://127.0.0.1:" + server.getLocalPort(), gson);
            ClanSearchResponse response = client.search(new ClanSearchQuery("", "", "", 25));

            serverThread.join(5000);
            assertFalse(serverThread.isAlive());
            assertNull(serverFailure.get());
            assertEquals(1, response.getData().size());
            assertEquals("One Clan", response.getData().get(0).getName());
        }
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

    @Test
    public void freshResultsStartAtTopOfScrollPane() throws Exception
    {
        ClanFinderPanel panel = new ClanFinderPanel(new NoopPanelListener());
        ClanSearchResponse response = gson.fromJson(
            "{\"data\":[{\"slug\":\"one\",\"name\":\"One Clan\",\"description\":\"Long enough listing text for a result card.\"}]," +
                "\"pagination\":{\"page\":1,\"limit\":25,\"total\":1}}",
            ClanSearchResponse.class
        );

        SwingUtilities.invokeAndWait(() ->
        {
            panel.showResults(response);
            JScrollPane scrollPane = findScrollPane(panel);
            assertNotNull(scrollPane);
            scrollPane.getVerticalScrollBar().setValues(120, 20, 0, 500);
            panel.showResults(response);
        });
        SwingUtilities.invokeAndWait(() -> { });

        JScrollPane scrollPane = findScrollPane(panel);
        assertNotNull(scrollPane);
        assertEquals(0, scrollPane.getVerticalScrollBar().getValue());
    }

    @Test
    public void searchControlsCanBeCollapsedAndExpanded() throws Exception
    {
        ClanFinderPanel panel = new ClanFinderPanel(new NoopPanelListener());

        SwingUtilities.invokeAndWait(() ->
        {
            JButton toggle = findNamedComponent(panel, "search-toggle-button", JButton.class);
            JPanel controls = findNamedComponent(panel, "search-controls-panel", JPanel.class);
            assertNotNull(toggle);
            assertNotNull(controls);

            assertTrue(controls.isVisible());
            assertEquals("Hide search controls", toggle.getToolTipText());

            toggle.doClick();
            assertFalse(controls.isVisible());
            assertEquals("Show search controls", toggle.getToolTipText());

            toggle.doClick();
            assertTrue(controls.isVisible());
            assertEquals("Hide search controls", toggle.getToolTipText());
        });
    }

    private static JScrollPane findScrollPane(Component component)
    {
        if (component instanceof JScrollPane)
        {
            return (JScrollPane) component;
        }

        if (component instanceof java.awt.Container)
        {
            for (Component child : ((java.awt.Container) component).getComponents())
            {
                JScrollPane scrollPane = findScrollPane(child);
                if (scrollPane != null)
                {
                    return scrollPane;
                }
            }
        }

        return null;
    }

    private static <T extends Component> T findNamedComponent(Component component, String name, Class<T> type)
    {
        if (type.isInstance(component) && name.equals(component.getName()))
        {
            return type.cast(component);
        }

        if (component instanceof java.awt.Container)
        {
            for (Component child : ((java.awt.Container) component).getComponents())
            {
                T match = findNamedComponent(child, name, type);
                if (match != null)
                {
                    return match;
                }
            }
        }

        return null;
    }

    private static void serveFailedThenSuccessfulResponse(ServerSocket server, String body, AtomicReference<Exception> failure)
    {
        try
        {
            try (Socket first = server.accept())
            {
                readHttpRequest(first);
            }

            try (Socket second = server.accept())
            {
                readHttpRequest(second);
                writeJsonResponse(second, body);
            }
        }
        catch (Exception ex)
        {
            failure.set(ex);
        }
    }

    private static void readHttpRequest(Socket socket) throws IOException
    {
        socket.setSoTimeout(5000);
        InputStream input = socket.getInputStream();
        int previous = -1;
        int matched = 0;
        int read;
        while ((read = input.read()) != -1)
        {
            if ((matched == 0 || matched == 2) && read == '\r' ||
                (matched == 1 || matched == 3) && read == '\n')
            {
                matched++;
                if (matched == 4)
                {
                    return;
                }
            }
            else
            {
                matched = previous == '\r' && read == '\n' ? 2 : 0;
            }
            previous = read;
        }
    }

    private static void writeJsonResponse(Socket socket, String body) throws IOException
    {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        String headers = "HTTP/1.1 200 OK\r\n" +
            "Content-Type: application/json\r\n" +
            "Content-Length: " + bytes.length + "\r\n" +
            "Connection: close\r\n\r\n";
        OutputStream output = socket.getOutputStream();
        output.write(headers.getBytes(StandardCharsets.US_ASCII));
        output.write(bytes);
        output.flush();
    }

    private static final class NoopPanelListener implements ClanFinderPanel.ClanFinderPanelListener
    {
        @Override
        public void search(ClanSearchQuery query)
        {
        }

        @Override
        public void copyClanChat(String clanChatName)
        {
        }

        @Override
        public void openClan(String slug)
        {
        }

        @Override
        public void openClanRegistration()
        {
        }

        @Override
        public void openSupportDiscord()
        {
        }

        @Override
        public void viewClan(ClanListing clan)
        {
        }

        @Override
        public String resolveAssetUrl(String assetUrl)
        {
            return "";
        }

        @Override
        public void loadImage(String assetUrl, int width, int height, Consumer<BufferedImage> callback)
        {
            callback.accept(null);
        }
    }
}
