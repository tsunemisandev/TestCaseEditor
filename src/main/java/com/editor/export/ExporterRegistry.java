package com.editor.export;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ExporterRegistry {
    private final Map<String, Exporter> exporters = new LinkedHashMap<>();

    public void register(Exporter exporter) {
        exporters.put(exporter.id(), exporter);
    }

    public List<Exporter> all() {
        return List.copyOf(exporters.values());
    }

    public Optional<Exporter> get(String id) {
        return Optional.ofNullable(exporters.get(id));
    }
}
