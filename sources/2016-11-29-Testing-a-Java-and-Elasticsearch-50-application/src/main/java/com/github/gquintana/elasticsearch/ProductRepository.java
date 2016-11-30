package com.github.gquintana.elasticsearch;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ByteArrayEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProductRepository {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private RestClient client;

    public void initialize() {
        client = RestClientFactory.create();
    }

    public void close() throws IOException {
        client.close();
    }

    private <T> HttpEntity toEntity(T object) {
        try {
            return new ByteArrayEntity(objectMapper.writeValueAsBytes(object));
        } catch (IOException e) {
            throw new ProductException("Failed to write " + object.getClass(), e);
        }
    }

    private <T> T fromEntity(HttpEntity entity, Class<T> clazz) {
        try (InputStream inputStream = entity.getContent()) {
            return objectMapper.readValue(inputStream, clazz);
        } catch (IOException e) {
            throw new ProductException("Failed to read " + clazz, e);
        }
    }

    public void index(Product product) {
        try {
            Response response;
            if (product.getId() == null) {
                response = client.performRequest("POST", "/product/product", Collections.emptyMap(), toEntity(product));
            } else {
                response = client.performRequest("PUT", "/product/product/" + product.getId(), Collections.emptyMap(), toEntity(product));
            }
            Map<String, Object> result = fromEntity(response.getEntity(), Map.class);
            product.setId((String) result.get("_id"));
        } catch (IOException e) {
            throw new ProductException("Failed to index", e);
        }
    }

    public Product get(String id) {
        Response response;
        try {
            response = client.performRequest("GET", "/product/product/" + id);
            Map<String, Object> result = fromEntity(response.getEntity(), Map.class);
            return objectMapper.readValue(objectMapper.writeValueAsBytes(result.get("_source")), Product.class);
        } catch (ResponseException e) {
            if (e.getResponse().getStatusLine().getStatusCode() == 404) {
                return null;
            }
            throw new ProductException("Failed to get " + id, e);
        } catch (IOException e) {
            throw new ProductException("Failed to get " + id, e);
        }
    }

    public void delete(String id) {
        try {
            Response response = client.performRequest("DELETE", "/product/product/" + id);
        } catch (IOException e) {
            throw new ProductException("Failed to delete", e);
        }
    }

    public void deleteAll() {
        Response response;
        try {
            response = client.performRequest("DELETE", "/product");
        } catch (ResponseException e) {
            if (e.getResponse().getStatusLine().getStatusCode() == 404) {
                return;
            }
            throw new ProductException("Failed to delete all", e);
        } catch (IOException e) {
            throw new ProductException("Failed to delete all", e);
        }
    }
}
