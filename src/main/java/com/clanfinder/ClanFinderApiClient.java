package com.clanfinder;

import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

final class ClanFinderApiClient
{
    static final String DEFAULT_BASE_URL = "https://osrsclanfinder.com";

    private static final int CONNECT_TIMEOUT_MILLIS = 5000;
    private static final int READ_TIMEOUT_MILLIS = 8000;
    private static final int MAX_RESPONSE_BYTES = 2 * 1024 * 1024;
    private static final String USER_AGENT = "ClanFinder-RuneLite-Plugin/0.1.0";

    private final Gson gson;
    private final String baseUrl;

    ClanFinderApiClient(String baseUrl, Gson gson)
    {
        this.baseUrl = normalizeBaseUrl(baseUrl);
        this.gson = Objects.requireNonNull(gson, "gson");
    }

    ClanSearchResponse search(ClanSearchQuery query) throws IOException
    {
        HttpURLConnection connection = openConnection(buildClansUrl(query));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        String body = readSuccessfulResponse(connection);
        ClanSearchResponse response = gson.fromJson(body, ClanSearchResponse.class);
        return response == null ? new ClanSearchResponse() : response;
    }

    ClanListing getClan(String slug) throws IOException
    {
        HttpURLConnection connection = openConnection(buildClanUrl(slug));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        String body = readSuccessfulResponse(connection);
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
