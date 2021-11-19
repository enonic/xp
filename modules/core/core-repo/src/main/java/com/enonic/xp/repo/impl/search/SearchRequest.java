package com.enonic.xp.repo.impl.search;

import com.enonic.xp.query.Query;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.SearchPreference;
import com.enonic.xp.repo.impl.SearchSource;

public class SearchRequest
{
    private final Query query;

    private final ReturnFields returnFields;

    private final SearchSource searchSource;

    private final SearchPreference searchPreference;

    private SearchRequest( Builder builder )
    {
        this.query = builder.query;
        this.returnFields = builder.returnFields;
        this.searchSource = builder.searchSource;
        this.searchPreference = builder.searchPreference;
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

    public SearchPreference getSearchPreference()
    {
        return searchPreference;
    }

    public static final class Builder
    {
        private Query query;

        private ReturnFields returnFields;

        private SearchSource searchSource;

        private SearchPreference searchPreference;

        private Builder()
        {
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

        public Builder searchPreference( final SearchPreference searchPreference )
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
