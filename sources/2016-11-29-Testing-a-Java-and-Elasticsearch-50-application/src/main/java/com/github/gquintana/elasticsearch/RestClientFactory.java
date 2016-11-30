package com.github.gquintana.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class RestClientFactory {
    private static HttpHost toHttpHost(String s) {
        s = s.trim();
        if (s.isEmpty()) {
            throw new IllegalArgumentException("Empty URL ");
        }
        try {
            URI u = new URI(s);
            return new HttpHost(u.getHost(), u.getPort() < 0 ? 9200 : u.getPort(), u.getScheme());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL " + s, e);
        }
    }

    public static RestClient create() {
        try (InputStream configStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties")) {
            Properties config = new Properties();
            config.load(configStream);
            String url = config.getProperty("elasticsearch.url");
            HttpHost[] hosts;
            if (url == null) {
                hosts = new HttpHost[]{new HttpHost("localhost", 9200)};
            } else {
                List<HttpHost> hostList = Arrays.stream(url.split(",")).map(RestClientFactory::toHttpHost).collect(Collectors.toList());
                hosts = hostList.toArray(new HttpHost[hostList.size()]);
            }
            return RestClient.builder(hosts).build();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config", e);
        }
    }

}
