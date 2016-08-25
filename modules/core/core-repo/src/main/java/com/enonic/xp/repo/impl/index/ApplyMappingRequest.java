package com.enonic.xp.repo.impl.index;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repository.IndexResource;

public class ApplyMappingRequest
{
    private final IndexResource mapping;

    private final String indexName;

    private final IndexType indexType;

    private ApplyMappingRequest( final Builder builder )
    {
        mapping = builder.mapping;
        indexName = builder.indexName;
        indexType = builder.indexType;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public IndexResource getMapping()
    {
        return mapping;
    }

    public String getIndexName()
    {
        return indexName;
    }

    public IndexType getIndexType()
    {
        return indexType;
    }

    public static final class Builder
    {
        private IndexResource mapping;

        private String indexName;

        private IndexType indexType;

        private Builder()
        {
        }

        public Builder mapping( final IndexResource val )
        {
            mapping = val;
            return this;
        }

        public Builder indexName( final String val )
        {
            indexName = val;
            return this;
        }

        public Builder indexType( final IndexType val )
        {
            indexType = val;
            return this;
        }

        public ApplyMappingRequest build()
        {
            return new ApplyMappingRequest( this );
        }
    }
}
