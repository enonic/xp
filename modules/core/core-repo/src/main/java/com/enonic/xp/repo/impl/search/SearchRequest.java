package com.enonic.xp.repo.impl.search;

import com.enonic.xp.query.Query;
import com.enonic.xp.repo.impl.DataSource;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.StorageSettings;

public class SearchRequest
{
    private final StorageSettings settings;

    private final Query query;

    private final ReturnFields returnFields;

    private final DataSource dataSource;

    private SearchRequest( Builder builder )
    {
        this.settings = builder.settings;
        this.query = builder.query;
        this.returnFields = builder.returnFields;
        this.dataSource = builder.dataSource;
    }

    public DataSource getDataSource()
    {
        return dataSource;
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

    public ReturnFields getReturnFields()
    {
        return returnFields;
    }

    public static final class Builder
    {
        private StorageSettings settings;

        private Query query;

        private ReturnFields returnFields;

        private DataSource dataSource;

        private Builder()
        {
        }

        public Builder settings( StorageSettings settings )
        {
            this.settings = settings;
            return this;
        }

        public Builder dataSource( final DataSource dataSource )
        {
            this.dataSource = dataSource;
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

        public SearchRequest build()
        {
            return new SearchRequest( this );
        }
    }
}
