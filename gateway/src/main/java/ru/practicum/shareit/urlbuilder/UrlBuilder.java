package ru.practicum.shareit.urlbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UrlBuilder {
    private final List<String> path = new ArrayList<>();
    private final Map<String, String> params = new HashMap<>();

    private UrlBuilder() {

    }

    public static UrlBuilder start() {
        return new UrlBuilder();
    }

    public UrlBuilder id(long id) {
        return pathPart("/" + id);
    }

    public UrlBuilder pathPart(String pathPart) {
        path.add(pathPart);
        return this;
    }

    public UrlBuilder param(String name, boolean value) {
        return param(name, Boolean.toString(value));
    }

    public UrlBuilder param(String name, String value) {
        params.put(name, value);
        return this;
    }

    public UrlBuilder param(String name, Object value) {
        return param(name, value.toString());
    }

    public String build() {
        StringBuilder stringBuilder = new StringBuilder();
        buildPath(stringBuilder);
        if (!params.isEmpty()) {
            buildParams(stringBuilder);
        }
        return stringBuilder.toString();
    }

    private void buildPath(StringBuilder stringBuilder) {
        for (String pathPart : path) {
            stringBuilder.append(pathPart);
        }
    }

    private void buildParams(StringBuilder stringBuilder) {
        stringBuilder.append("?");
        List<Map.Entry<String, String>> entries = new ArrayList<>(params.entrySet());
        int paramsCount = entries.size();
        for (int i = 0; i < paramsCount; i++) {
            Map.Entry<String, String> entry = entries.get(i);
            stringBuilder.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue());
            if (i < paramsCount - 1) {
                stringBuilder.append("&");
            }
        }
    }
}
