package com.clanfinder;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

final class ClanImageCache
{
    private static final int CONNECT_TIMEOUT_MILLIS = 5000;
    private static final int READ_TIMEOUT_MILLIS = 8000;
    private static final int MAX_IMAGE_BYTES = 2 * 1024 * 1024;
    private static final int MAX_CACHE_ENTRIES = 80;
    private static final String CLANFINDER_HOST = "osrsclanfinder.com";

    private final ExecutorService executor = Executors.newFixedThreadPool(2, runnable ->
    {
        Thread thread = new Thread(runnable, "clanfinder-images");
        thread.setDaemon(true);
        return thread;
    });
    private final Map<String, BufferedImage> cache = new LinkedHashMap<String, BufferedImage>(MAX_CACHE_ENTRIES, 0.75f, true)
    {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, BufferedImage> eldest)
        {
            return size() > MAX_CACHE_ENTRIES;
        }
    };

    void load(String url, int width, int height, Consumer<BufferedImage> callback)
    {
        if (url == null || url.trim().isEmpty())
        {
            deliver(callback, null);
            return;
        }

        String cacheKey = url + "|" + width + "x" + height;
        synchronized (cache)
        {
            BufferedImage cached = cache.get(cacheKey);
            if (cached != null)
            {
                deliver(callback, cached);
                return;
            }
        }

        executor.submit(() ->
        {
            BufferedImage image = null;
            try
            {
                image = downloadAndScale(url, width, height);
                if (image != null)
                {
                    synchronized (cache)
                    {
                        cache.put(cacheKey, image);
                    }
                }
            }
            catch (Exception ignored)
            {
                image = null;
            }

            deliver(callback, image);
        });
    }

    void shutDown()
    {
        executor.shutdownNow();
        synchronized (cache)
        {
            cache.clear();
        }
    }

    private static BufferedImage downloadAndScale(String value, int width, int height) throws Exception
    {
        URL url = new URL(value);
        String protocol = url.getProtocol();
        if (!"https".equalsIgnoreCase(protocol) || !CLANFINDER_HOST.equalsIgnoreCase(url.getHost()))
        {
            return null;
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
        connection.setReadTimeout(READ_TIMEOUT_MILLIS);
        connection.setRequestProperty("Accept", "image/jpeg,image/png");
        connection.setRequestProperty("User-Agent", "ClanFinder-RuneLite-Plugin/0.1.0");

        int status = connection.getResponseCode();
        if (status < 200 || status >= 300)
        {
            return null;
        }

        String contentType = connection.getContentType();
        if (contentType == null ||
            (!contentType.startsWith("image/jpeg") &&
                !contentType.startsWith("image/png")))
        {
            return null;
        }

        byte[] bytes = readLimited(connection.getInputStream());
        BufferedImage source = ImageIO.read(new ByteArrayInputStream(bytes));
        if (source == null)
        {
            return null;
        }

        return scaleCover(source, width, height);
    }

    private static byte[] readLimited(InputStream input) throws Exception
    {
        try (InputStream stream = input; ByteArrayOutputStream output = new ByteArrayOutputStream())
        {
            byte[] buffer = new byte[8192];
            int total = 0;
            int read;

            while ((read = stream.read(buffer)) != -1)
            {
                total += read;
                if (total > MAX_IMAGE_BYTES)
                {
                    throw new IllegalArgumentException("Image response is too large.");
                }

                output.write(buffer, 0, read);
            }

            return output.toByteArray();
        }
    }

    private static BufferedImage scaleCover(BufferedImage source, int width, int height)
    {
        double scale = Math.max(width / (double) source.getWidth(), height / (double) source.getHeight());
        int scaledWidth = Math.max(width, (int) Math.round(source.getWidth() * scale));
        int scaledHeight = Math.max(height, (int) Math.round(source.getHeight() * scale));
        int x = (width - scaledWidth) / 2;
        int y = (height - scaledHeight) / 2;

        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = output.createGraphics();
        try
        {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.drawImage(source, x, y, scaledWidth, scaledHeight, null);
        }
        finally
        {
            graphics.dispose();
        }

        return output;
    }

    private static void deliver(Consumer<BufferedImage> callback, BufferedImage image)
    {
        SwingUtilities.invokeLater(() -> callback.accept(image));
    }
}
