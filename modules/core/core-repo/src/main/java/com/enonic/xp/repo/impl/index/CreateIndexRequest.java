package com.enonic.xp.repo.impl.index;

import java.util.Map;

import com.google.common.base.Preconditions;

import com.enonic.xp.index.IndexType;

import static com.google.common.base.Strings.isNullOrEmpty;

public class CreateIndexRequest
{
    private final String indexName;

    private final IndexSettings indexSettings;

    private final Map<IndexType, IndexMapping> mappings;

    private CreateIndexRequest( final Builder builder )
    {
        indexName = builder.indexName;
        mappings = builder.mappings;
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

    public Map<IndexType, IndexMapping> getMappings()
    {
        return mappings;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String indexName;

        private IndexSettings indexSettings;

        private Map<IndexType, IndexMapping> mappings;

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

        public Builder mappings( Map<IndexType, IndexMapping> val )
        {
            mappings = Map.copyOf( val );
            return this;
        }

        public CreateIndexRequest build()
        {
            this.validate();
            return new CreateIndexRequest( this );
        }
    }
}
