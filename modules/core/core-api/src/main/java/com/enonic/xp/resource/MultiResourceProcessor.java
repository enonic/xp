package com.enonic.xp.resource;

import java.util.List;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Processes a fixed set of resources ("file bundle") into a single cached value.
 * <p>
 * Unlike {@link ResourceProcessor}, the produced value depends on several resources at once
 * (for example, a chain of {@code .properties} files merged into one message bundle).
 * {@link ResourceService#processResources(MultiResourceProcessor)} caches the value and recomputes it
 * when the owning application bundle changes or any of the resources appears, disappears or is modified.
 *
 * @param <K> type of the cache key
 * @param <V> type of the processed value
 */
public final class MultiResourceProcessor<K, V>
{
    private final K key;

    private final String segment;

    private final Function<K, List<ResourceKey>> keysTranslator;

    private final Function<List<Resource>, V> processor;

    @SuppressWarnings("unchecked")
    private MultiResourceProcessor( final Builder builder )
    {
        this.key = (K) builder.key;
        this.segment = builder.segment;
        this.keysTranslator = builder.keysTranslator;
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

    /**
     * Resolves the keys of all resources the value is built from, in processing order.
     * Must be deterministic for a given {@link #getKey() key}.
     *
     * @return resource keys in processing order
     */
    public List<ResourceKey> toResourceKeys()
    {
        return List.copyOf( this.keysTranslator.apply( this.key ) );
    }

    /**
     * Computes the value from the resolved resources. Resources are passed in {@link #toResourceKeys()} order
     * and may not exist - the processor decides how to treat missing ones.
     *
     * @param resources resolved resources, existing or not, in {@link #toResourceKeys()} order
     * @return processed value, or null to skip caching
     */
    public V process( final List<Resource> resources )
    {
        return this.processor.apply( resources );
    }

    public static final class Builder<K, V>
    {
        private K key;

        private String segment;

        private Function<K, List<ResourceKey>> keysTranslator;

        private Function<List<Resource>, V> processor;

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

        public Builder<K, V> keysTranslator( final Function<K, List<ResourceKey>> keysTranslator )
        {
            this.keysTranslator = keysTranslator;
            return this;
        }

        public Builder<K, V> processor( final Function<List<Resource>, V> processor )
        {
            this.processor = processor;
            return this;
        }

        public MultiResourceProcessor<K, V> build()
        {
            requireNonNull( this.key, "key is required" );
            requireNonNull( this.segment, "segment is required" );
            requireNonNull( this.keysTranslator, "keysTranslator is required" );
            requireNonNull( this.processor, "processor is required" );

            return new MultiResourceProcessor<>( this );
        }
    }
}
