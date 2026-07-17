package com.enonic.xp.storage.spi;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.core.internal.json.JsonHelper;

/**
 * Opaque, engine-shaped per-index settings (today: verbatim Elasticsearch index settings
 * JSON). Backend-internal semantics — the SPI only carries the JSON blob through.
 */
public final class IndexSettings
{
    private final JsonNode jsonNode;

    private IndexSettings( final JsonNode resourceNode )
    {
        this.jsonNode = resourceNode;
    }

    public static IndexSettings from( final Map<String, ?> settings )
    {
        return new IndexSettings( JsonHelper.from( settings ) );
    }

    public Map<String, Object> getData()
    {
        return JsonHelper.toMap( jsonNode );
    }
}
