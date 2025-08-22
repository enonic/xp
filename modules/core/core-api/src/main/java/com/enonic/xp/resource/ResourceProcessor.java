package com.enonic.xp.resource;

import java.util.Objects;
import java.util.function.Function;

public final class ResourceProcessor<K, V>
{
    private final K key;

    private final String segment;

    private final Function<K, ResourceKey> keyTranslator;

    private final Function<Resource, V> processor;

    @SuppressWarnings("unchecked")
    private ResourceProcessor( final Builder builder )
    {
        this.key = (K) builder.key;
        this.segment = builder.segment;
        this.keyTranslator = builder.keyTranslator;
        this.processor = builder.processor;
    }

    public K getKey()
    {
        return this.key;
    }

    public String getSegment()
    {
        return this.segment;
    }

    public ResourceKey toResourceKey()
    {
        return this.keyTranslator.apply( this.key );
    }

    public V process( final Resource resource )
    {
        if ( !resource.exists() )
        {
            return null;
        }

        return this.processor.apply( resource );
    }

    public static final class Builder<K, V>
    {
        private K key;

        private String segment;

        private Function<K, ResourceKey> keyTranslator;

        private Function<Resource, V> processor;

        public Builder<K, V> key( final K key )
        {
            this.key = key;
            return this;
        }

        public Builder<K, V> segment( final String segment )
        {
            this.segment = segment;
            return this;
        }

        public Builder<K, V> keyTranslator( final Function<K, ResourceKey> keyTranslator )
        {
            this.keyTranslator = keyTranslator;
            return this;
        }

        public Builder<K, V> processor( final Function<Resource, V> processor )
        {
            this.processor = processor;
            return this;
        }

        public ResourceProcessor<K, V> build()
        {
            Objects.requireNonNull( this.key, "key is required" );
            Objects.requireNonNull( this.segment, "segment is required" );
            Objects.requireNonNull( this.keyTranslator, "keyTranslator is required" );
            Objects.requireNonNull( this.processor, "processor is required" );

            return new ResourceProcessor<>( this );
        }
    }
}
