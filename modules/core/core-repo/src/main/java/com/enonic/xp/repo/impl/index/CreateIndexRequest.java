package com.enonic.xp.repo.impl.index;

import com.google.common.base.Preconditions;

import com.enonic.xp.repository.IndexSettings;

import static com.google.common.base.Strings.isNullOrEmpty;

public class CreateIndexRequest
{
    private final String indexName;

    private final IndexSettings indexSettings;

    private CreateIndexRequest( final Builder builder )
    {
        indexName = builder.indexName;
        indexSettings = builder.indexSettings;
    }

    public String getIndexName()
    {
        return indexName;
    }

    public IndexSettings getIndexSettings()
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

        private IndexSettings indexSettings;

        private Builder()
        {
        }

        public Builder indexName( final String val )
        {
            indexName = val;
            return this;
        }

        public Builder indexSettings( final IndexSettings val )
        {
            indexSettings = val;
            return this;
        }

        private void validate()
        {
            Preconditions.checkArgument( !isNullOrEmpty( indexName ), "IndexName must be set" );
            Preconditions.checkArgument( indexSettings != null, "Index settings must be given" );
        }

        public CreateIndexRequest build()
        {
            this.validate();
            return new CreateIndexRequest( this );
        }
    }
}
