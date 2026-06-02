package com.clanfinder;

import com.google.inject.Provides;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.function.Consumer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(
    name = "ClanFinder",
    description = "Browse approved OSRS clan recruitment listings from ClanFinder.",
    tags = {"clan", "clans", "recruitment", "social", "pvm", "raids"}
)
public class ClanFinderPlugin extends Plugin implements ClanFinderPanel.ClanFinderPanelListener
{
    private static final Logger log = LoggerFactory.getLogger(ClanFinderPlugin.class);
    private static final String CLANFINDER_BASE_URL = ClanFinderApiClient.DEFAULT_BASE_URL;
    private static final String CLANFINDER_HOST = "osrsclanfinder.com";

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private ClanFinderConfig config;

    private ExecutorService executor;
    private ClanImageCache imageCache;
    private ClanFinderPanel panel;
    private NavigationButton navigationButton;

    @Override
    protected void startUp()
    {
        executor = Executors.newSingleThreadExecutor(runnable ->
        {
            Thread thread = new Thread(runnable, "clanfinder-api");
            thread.setDaemon(true);
            return thread;
        });

        panel = new ClanFinderPanel(this);
        imageCache = new ClanImageCache();
        BufferedImage icon = ImageUtil.loadImageResource(ClanFinderPlugin.class, "/clanfinder_icon.png");
        navigationButton = NavigationButton.builder()
            .tooltip("ClanFinder")
            .icon(icon)
            .priority(7)
            .panel(panel)
            .build();

        clientToolbar.addNavigation(navigationButton);
        search(new ClanSearchQuery("", "", "", config.resultsLimit()));
        log.debug("ClanFinder started");
    }

    @Override
    protected void shutDown()
    {
        if (navigationButton != null)
        {
            clientToolbar.removeNavigation(navigationButton);
        }

        if (executor != null)
        {
            executor.shutdownNow();
        }

        if (imageCache != null)
        {
            imageCache.shutDown();
        }

        navigationButton = null;
        panel = null;
        executor = null;
        imageCache = null;
        log.debug("ClanFinder stopped");
    }

    @Override
    public void search(ClanSearchQuery query)
    {
        ClanFinderPanel currentPanel = panel;
        ExecutorService currentExecutor = executor;

        if (currentPanel == null || currentExecutor == null || currentExecutor.isShutdown())
        {
            return;
        }

        if (query.getPage() > 1)
        {
            currentPanel.showLoadingMore();
        }
        else
        {
            currentPanel.showLoading();
        }

        ClanSearchQuery request = query.withLimit(config.resultsLimit());
        currentExecutor.submit(() ->
        {
            try
            {
                ClanSearchResponse response = new ClanFinderApiClient(CLANFINDER_BASE_URL).search(request);
                javax.swing.SwingUtilities.invokeLater(() ->
                {
                    if (panel != null)
                    {
                        panel.showResults(response);
                    }
                });
            }
            catch (Exception ex)
            {
                log.warn("Unable to load ClanFinder listings", ex);
                javax.swing.SwingUtilities.invokeLater(() ->
                {
                    if (panel != null)
                    {
                        if (request.getPage() > 1)
                        {
                            panel.showLoadMoreError();
                        }
                        else
                        {
                            panel.showError("Could not load approved clan listings. Please try again shortly.");
                        }
                    }
                });
            }
        });
    }

    @Override
    public void copyClanChat(String clanChatName)
    {
        if (clanChatName == null || clanChatName.isEmpty())
        {
            return;
        }

        Toolkit.getDefaultToolkit()
            .getSystemClipboard()
            .setContents(new StringSelection(clanChatName), null);

        clientThread.invoke(() -> client.addChatMessage(
            ChatMessageType.GAMEMESSAGE,
            "",
            "ClanFinder copied clan chat: " + clanChatName,
            null
        ));
    }

    @Override
    public void openClan(String slug)
    {
        if (slug == null || slug.isEmpty())
        {
            return;
        }

        LinkBrowser.browse(CLANFINDER_BASE_URL + "/clans/" + slug);
    }

    @Override
    public void viewClan(ClanListing clan)
    {
        ClanFinderPanel currentPanel = panel;
        ExecutorService currentExecutor = executor;

        if (clan == null || clan.getSlug().isEmpty() || currentPanel == null || currentExecutor == null || currentExecutor.isShutdown())
        {
            return;
        }

        currentPanel.showClanDetailLoading(clan);
        currentExecutor.submit(() ->
        {
            try
            {
                ClanListing detail = new ClanFinderApiClient(CLANFINDER_BASE_URL).getClan(clan.getSlug());
                javax.swing.SwingUtilities.invokeLater(() ->
                {
                    if (panel != null)
                    {
                        panel.showClanDetail(detail);
                    }
                });
            }
            catch (Exception ex)
            {
                log.warn("Unable to load ClanFinder clan detail", ex);
                javax.swing.SwingUtilities.invokeLater(() ->
                {
                    if (panel != null)
                    {
                        panel.showClanDetail(clan, "Could not refresh this clan's events. Showing the listing data already loaded.");
                    }
                });
            }
        });
    }

    @Override
    public String resolveAssetUrl(String assetUrl)
    {
        if (assetUrl == null || assetUrl.trim().isEmpty())
        {
            return "";
        }

        String cleaned = assetUrl.trim();
        if (cleaned.startsWith("http://") || cleaned.startsWith("https://"))
        {
            return isClanFinderAssetUrl(cleaned) ? cleaned : "";
        }

        return cleaned.startsWith("/") ? CLANFINDER_BASE_URL + cleaned : CLANFINDER_BASE_URL + "/" + cleaned;
    }

    @Override
    public void loadImage(String assetUrl, int width, int height, Consumer<BufferedImage> callback)
    {
        ClanImageCache currentCache = imageCache;
        if (currentCache == null)
        {
            callback.accept(null);
            return;
        }

        currentCache.load(resolveAssetUrl(assetUrl), width, height, callback);
    }

    @Provides
    ClanFinderConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ClanFinderConfig.class);
    }

    private static boolean isClanFinderAssetUrl(String value)
    {
        try
        {
            URI uri = URI.create(value);
            return "https".equalsIgnoreCase(uri.getScheme()) && CLANFINDER_HOST.equalsIgnoreCase(uri.getHost());
        }
        catch (IllegalArgumentException ex)
        {
            return false;
        }
    }
}
