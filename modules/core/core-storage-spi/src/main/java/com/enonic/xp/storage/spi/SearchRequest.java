package com.enonic.xp.storage.spi;

import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.query.Query;

public class SearchRequest
{
    private final Query query;

    private final ReturnFields returnFields;

    private final SearchSource searchSource;

    private final @Nullable SearchPreference searchPreference;

    private final @Nullable Supplier<SearchDsl> searchDsl;

    private SearchRequest( Builder builder )
    {
        this.query = builder.query;
        this.returnFields = builder.returnFields;
        this.searchSource = builder.searchSource;
        this.searchPreference = builder.searchPreference;
        this.searchDsl = builder.searchDsl;
    }

    public SearchSource getSearchSource()
    {
        return searchSource;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Query getQuery()
    {
        return query;
    }

    public ReturnFields getReturnFields()
    {
        return returnFields;
    }

    public @Nullable SearchPreference getSearchPreference()
    {
        return searchPreference;
    }

    /**
     * The canonical JSON query DSL for this request, rendered on demand above the storage SPI.
     * <p>
     * Deliberately lazy and deliberately nullable. Lazy, because the backend that renders its
     * own query objects must not pay for — or fail on — a rendering it never reads. Nullable,
     * because absence is the honest answer for a query surface that has no wire form (branch,
     * version and commit queries are answered from the system of record, not from a search
     * index); a backend that needs the DSL and finds none must fail fast rather than translate.
     */
    public @Nullable SearchDsl getSearchDsl()
    {
        return searchDsl == null ? null : searchDsl.get();
    }

    public static final class Builder
    {
        private Query query;

        private ReturnFields returnFields;

        private SearchSource searchSource;

        private @Nullable SearchPreference searchPreference;

        private @Nullable Supplier<SearchDsl> searchDsl;

        private Builder()
        {
        }

        public Builder searchDsl( final @Nullable Supplier<SearchDsl> searchDsl )
        {
            this.searchDsl = searchDsl;
            return this;
        }

        public Builder searchSource( final SearchSource searchSource )
        {
            this.searchSource = searchSource;
            return this;
        }

        public Builder query( Query query )
        {
            this.query = query;
            return this;
        }

        public Builder returnFields( final ReturnFields returnFields )
        {
            this.returnFields = returnFields;
            return this;
        }

        public Builder searchPreference( final @Nullable SearchPreference searchPreference )
        {
            this.searchPreference = searchPreference;
            return this;
        }

        public SearchRequest build()
        {
            return new SearchRequest( this );
        }
    }
}
