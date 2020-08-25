package com.enonic.xp.repository;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.index.IndexType;

@PublicApi
public class IndexDefinitions
{
    private final Map<IndexType, IndexDefinition> configs;

    private IndexDefinitions( final Builder builder )
    {
        this.configs = builder.configs.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public IndexDefinition get( final IndexType indexType )
    {
        return configs.get( indexType );
    }

    public static final class Builder
    {
        private final ImmutableMap.Builder<IndexType, IndexDefinition> configs = ImmutableMap.builder();

        private Builder()
        {
        }

        public Builder add( final IndexType type, final IndexDefinition config )
        {
            this.configs.put( type, config );
            return this;
        }

        public IndexDefinitions build()
        {
            return new IndexDefinitions( this );
        }
    }

}
