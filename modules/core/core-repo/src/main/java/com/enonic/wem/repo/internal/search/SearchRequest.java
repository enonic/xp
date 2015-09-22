package com.enonic.wem.repo.internal.search;

import com.enonic.wem.repo.internal.storage.StorageSettings;
import com.enonic.xp.query.Query;

public class SearchRequest
{
    private StorageSettings settings;

    private Query query;

    private SearchRequest( Builder builder )
    {
        settings = builder.settings;
        query = builder.query;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public StorageSettings getSettings()
    {
        return settings;
    }

    public Query getQuery()
    {
        return query;
    }


    public static final class Builder
    {
        private StorageSettings settings;

        private Query query;

        private Builder()
        {
        }

        public Builder settings( StorageSettings settings )
        {
            this.settings = settings;
            return this;
        }

        public Builder query( Query query )
        {
            this.query = query;
            return this;
        }

        public SearchRequest build()
        {
            return new SearchRequest( this );
        }
    }
}
