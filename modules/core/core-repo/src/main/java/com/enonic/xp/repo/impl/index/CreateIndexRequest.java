package com.enonic.xp.repo.impl.index;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.xp.repository.IndexResource;

public class CreateIndexRequest
{
    private final String indexName;

    private final IndexResource indexSettings;

    private CreateIndexRequest( final Builder builder )
    {
        indexName = builder.indexName;
        indexSettings = builder.indexSettings;
    }

    public String getIndexName()
    {
        return indexName;
    }

    public IndexResource getIndexSettings()
    {
        return indexSettings;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String indexName;

        private IndexResource indexSettings;

        private Builder()
        {
        }

        public Builder indexName( final String val )
        {
            indexName = val;
            return this;
        }

        public Builder indexSettings( final IndexResource val )
        {
            indexSettings = val;
            return this;
        }

        private void validate()
        {
            Preconditions.checkArgument( !Strings.isNullOrEmpty( indexName ), "IndexName must be set" );
            Preconditions.checkArgument( indexSettings != null, "Index settings must be given" );
        }

        public CreateIndexRequest build()
        {
            return new CreateIndexRequest( this );
        }
    }
}
