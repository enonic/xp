package com.enonic.xp.storage.spi;

import java.util.List;
import java.util.Map;

import org.jspecify.annotations.NullMarked;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import static java.util.Objects.requireNonNull;

/**
 * The canonical JSON query DSL rendered from a {@link com.enonic.xp.query.Query}, plus the
 * paging/scoring parameters that ride with it. Produced above the storage SPI (core-repo,
 * beside the NoQL parser) so that a storage backend receiving it needs no query-language
 * knowledge: every member is already a JSON-shaped, order-stable structure.
 * <p>
 * Maps and lists are insertion-ordered on purpose — element order is part of the wire format.
 */
@NullMarked
public final class SearchDsl
{
    private final Map<String, Object> query;

    private final List<Map<String, Object>> queryFilters;

    private final List<Map<String, Object>> postFilters;

    private final List<Map<String, Object>> sort;

    /**
     * The canonical suggest config, {@code {"<name>": {"text": .., "term": {..}}}}, empty when the
     * query requested none. One document rather than a list because a suggest section is a MAP
     * keyed by suggester name — and because XP's own {@code SuggestionQueries} is a set whose
     * iteration order varies per JVM, the renderer sorts it into a total order before it gets here.
     */
    private final Map<String, Object> suggest;

    /**
     * The canonical highlight config, {@code {settings: {..}, properties: [{name, settings}]}},
     * empty when the query requested none. Field names carry no sub-field postfixes: the backend
     * owns the three-field expansion, exactly as it owns postfix resolution everywhere else.
     */
    private final Map<String, Object> highlight;

    private final int from;

    private final int size;

    private final int batchSize;

    private final boolean explain;

    private final String searchOptimizer;

    private SearchDsl( final Builder builder )
    {
        this.query = requireNonNull( builder.query, "query is required" );
        this.queryFilters = builder.queryFilters.build();
        this.postFilters = builder.postFilters.build();
        this.sort = builder.sort.build();
        this.suggest = builder.suggest.build();
        this.highlight = builder.highlight.build();
        this.from = builder.from;
        this.size = builder.size;
        this.batchSize = builder.batchSize;
        this.explain = builder.explain;
        this.searchOptimizer = requireNonNull( builder.searchOptimizer, "searchOptimizer is required" );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Map<String, Object> getQuery()
    {
        return query;
    }

    public List<Map<String, Object>> getQueryFilters()
    {
        return queryFilters;
    }

    public List<Map<String, Object>> getPostFilters()
    {
        return postFilters;
    }

    public List<Map<String, Object>> getSort()
    {
        return sort;
    }

    public Map<String, Object> getSuggest()
    {
        return suggest;
    }

    public Map<String, Object> getHighlight()
    {
        return highlight;
    }

    public int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }

    public int getBatchSize()
    {
        return batchSize;
    }

    public boolean isExplain()
    {
        return explain;
    }

    public String getSearchOptimizer()
    {
        return searchOptimizer;
    }

    public static final class Builder
    {
        private Map<String, Object> query;

        private final ImmutableList.Builder<Map<String, Object>> queryFilters = ImmutableList.builder();

        private final ImmutableList.Builder<Map<String, Object>> postFilters = ImmutableList.builder();

        private final ImmutableList.Builder<Map<String, Object>> sort = ImmutableList.builder();

        private final ImmutableMap.Builder<String, Object> suggest = ImmutableMap.builder();

        private final ImmutableMap.Builder<String, Object> highlight = ImmutableMap.builder();

        private int from;

        private int size;

        private int batchSize;

        private boolean explain;

        private String searchOptimizer;

        private Builder()
        {
        }

        public Builder query( final Map<String, Object> query )
        {
            this.query = ImmutableMap.copyOf( query );
            return this;
        }

        public Builder addQueryFilter( final Map<String, Object> filter )
        {
            this.queryFilters.add( ImmutableMap.copyOf( filter ) );
            return this;
        }

        public Builder addPostFilter( final Map<String, Object> filter )
        {
            this.postFilters.add( ImmutableMap.copyOf( filter ) );
            return this;
        }

        public Builder addSort( final Map<String, Object> sort )
        {
            this.sort.add( ImmutableMap.copyOf( sort ) );
            return this;
        }

        /** One suggester, keyed by its name. Called in sorted order so the wire is order-stable. */
        public Builder addSuggester( final String name, final Map<String, Object> suggester )
        {
            this.suggest.put( name, ImmutableMap.copyOf( suggester ) );
            return this;
        }

        public Builder highlight( final Map<String, Object> highlight )
        {
            this.highlight.putAll( highlight );
            return this;
        }

        public Builder from( final int from )
        {
            this.from = from;
            return this;
        }

        public Builder size( final int size )
        {
            this.size = size;
            return this;
        }

        public Builder batchSize( final int batchSize )
        {
            this.batchSize = batchSize;
            return this;
        }

        public Builder explain( final boolean explain )
        {
            this.explain = explain;
            return this;
        }

        public Builder searchOptimizer( final String searchOptimizer )
        {
            this.searchOptimizer = searchOptimizer;
            return this;
        }

        public SearchDsl build()
        {
            return new SearchDsl( this );
        }
    }
}
