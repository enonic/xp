package com.enonic.xp.web.session.impl.reporter;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMetrics;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

class WebSessionReport
{
    private final IgniteCache cache;

    private WebSessionReport( final Builder builder )
    {
        cache = builder.cache;
    }

    JsonNode toJson()
    {
        final ObjectNode node = JsonNodeFactory.instance.objectNode();

        if ( cache == null )
        {
            return node;
        }

        node.set( "cache", getCacheInfo() );
        return node;
    }

    ObjectNode getCacheInfo()
    {
        final ObjectNode node = JsonNodeFactory.instance.objectNode();

        node.put( "name", this.cache.getName() );
        node.put( "size", this.cache.size() );
        node.set( "metrics", getCacheMetrics( this.cache.metrics() ) );

        return node;
    }

    @NotNull
    private ObjectNode getCacheMetrics( final CacheMetrics cacheMetrics )
    {
        final ObjectNode node = JsonNodeFactory.instance.objectNode();

        if ( !cacheMetrics.isStatisticsEnabled() )
        {
            node.put( "statsEnabled", false );
            return node;
        }

        node.put( "gets", cacheMetrics.getCacheGets() );
        node.put( "puts", cacheMetrics.getCachePuts() );
        node.put( "valueType", cacheMetrics.getValueType() );
        node.put( "hits", cacheMetrics.getCacheHits() );
        node.put( "hitsPercentage", cacheMetrics.getCacheHitPercentage() );
        node.put( "misses", cacheMetrics.getCacheMisses() );
        node.put( "puts", cacheMetrics.getCachePuts() );
        node.put( "evictions", cacheMetrics.getCacheEvictions() );
        node.put( "heapEntries", cacheMetrics.getHeapEntriesCount() );
        node.put( "offHeapSize", cacheMetrics.getOffHeapAllocatedSize() );
        return node;
    }

    static Builder create()
    {
        return new Builder();
    }

    static final class Builder
    {
        private IgniteCache cache;

        private Builder()
        {
        }

        Builder cache( final IgniteCache val )
        {
            cache = val;
            return this;
        }

        WebSessionReport build()
        {
            return new WebSessionReport( this );
        }
    }
}
