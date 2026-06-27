package com.clanfinder;

import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

final class ClanFinderApiClient
{
    static final String DEFAULT_BASE_URL = "https://osrsclanfinder.com";

    private static final int CONNECT_TIMEOUT_MILLIS = 8000;
    private static final int READ_TIMEOUT_MILLIS = 15000;
    private static final int MAX_ATTEMPTS = 3;
    private static final int RETRY_DELAY_MILLIS = 1000;
    private static final int MAX_RESPONSE_BYTES = 2 * 1024 * 1024;
    private static final String USER_AGENT = "ClanFinder-RuneLite-Plugin/0.1.2";

    private final Gson gson;
    private final String baseUrl;

    ClanFinderApiClient(String baseUrl, Gson gson)
    {
        this.baseUrl = normalizeBaseUrl(baseUrl);
        this.gson = Objects.requireNonNull(gson, "gson");
    }

    ClanSearchResponse search(ClanSearchQuery query) throws IOException
    {
        String body = readJsonWithRetry(buildClansUrl(query));
        ClanSearchResponse response = gson.fromJson(body, ClanSearchResponse.class);
        return response == null ? new ClanSearchResponse() : response;
    }

    ClanListing getClan(String slug) throws IOException
    {
        String body = readJsonWithRetry(buildClanUrl(slug));
        ClanListing clan = gson.fromJson(body, ClanListing.class);
        return clan == null ? new ClanListing() : clan;
    }

    String buildClansUrl(ClanSearchQuery query)
    {
        StringBuilder url = new StringBuilder(baseUrl)
            .append("/api/v1/clans?page=")
            .append(query.getPage())
            .append("&limit=")
            .append(query.getLimit());

        appendParam(url, "search", query.getSearch());
        appendParam(url, "type", query.getType());
        appendParam(url, "region", query.getRegion());
        return url.toString();
    }

    String buildClanUrl(String slug)
    {
        return new StringBuilder(baseUrl)
            .append("/api/v1/clans/")
            .append(encode(slug == null ? "" : slug.trim()))
            .toString();
    }

    static String normalizeBaseUrl(String value)
    {
        String cleaned = value == null ? "" : value.trim();

        if (cleaned.isEmpty())
        {
            return DEFAULT_BASE_URL;
        }

        while (cleaned.endsWith("/"))
        {
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }

        if (!cleaned.startsWith("http://") && !cleaned.startsWith("https://"))
        {
            return "https://" + cleaned;
        }

        return cleaned;
    }

    private HttpURLConnection openConnection(String url) throws IOException
    {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
        connection.setReadTimeout(READ_TIMEOUT_MILLIS);
        connection.setRequestProperty("User-Agent", USER_AGENT);
        return connection;
    }

    private String readJsonWithRetry(String url) throws IOException
    {
        IOException failure = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++)
        {
            HttpURLConnection connection = null;
            try
            {
                connection = openConnection(url);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                return readSuccessfulResponse(connection);
            }
            catch (IOException ex)
            {
                failure = ex;
                if (attempt >= MAX_ATTEMPTS || !isRetryable(ex))
                {
                    throw ex;
                }

                sleepBeforeRetry();
            }
            finally
            {
                if (connection != null)
                {
                    connection.disconnect();
                }
            }
        }

        throw failure == null ? new IOException("ClanFinder API request failed.") : failure;
    }

    private static boolean isRetryable(IOException ex)
    {
        String message = ex.getMessage();
        return message == null || (!message.startsWith("ClanFinder API returned HTTP ") &&
            !"ClanFinder API response is too large.".equals(message));
    }

    private static void sleepBeforeRetry() throws IOException
    {
        try
        {
            Thread.sleep(RETRY_DELAY_MILLIS);
        }
        catch (InterruptedException ex)
        {
            Thread.currentThread().interrupt();
            InterruptedIOException interrupted = new InterruptedIOException("Interrupted before retrying ClanFinder API request.");
            interrupted.initCause(ex);
            throw interrupted;
        }
    }

    private static void appendParam(StringBuilder url, String key, String value)
    {
        if (value == null || value.isEmpty())
        {
            return;
        }

        url.append('&')
            .append(encode(key))
            .append('=')
            .append(encode(value));
    }

    private static String encode(String value)
    {
        try
        {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        }
        catch (IOException ex)
        {
            throw new IllegalStateException("UTF-8 encoding is unavailable", ex);
        }
    }

    private static String readSuccessfulResponse(HttpURLConnection connection) throws IOException
    {
        int status = connection.getResponseCode();
        InputStream responseStream = status >= 200 && status < 300
            ? connection.getInputStream()
            : connection.getErrorStream();
        String body = readBody(responseStream);

        if (status < 200 || status >= 300)
        {
            throw new IOException("ClanFinder API returned HTTP " + status + ": " + body);
        }

        return body;
    }

    private static String readBody(InputStream stream) throws IOException
    {
        if (stream == null)
        {
            return "";
        }

        try (InputStream response = stream; ByteArrayOutputStream output = new ByteArrayOutputStream())
        {
            byte[] buffer = new byte[8192];
            int total = 0;
            int read;

            while ((read = response.read(buffer)) != -1)
            {
                total += read;
                if (total > MAX_RESPONSE_BYTES)
                {
                    throw new IOException("ClanFinder API response is too large.");
                }

                output.write(buffer, 0, read);
            }

            return output.toString(StandardCharsets.UTF_8.name());
        }
    }
}
