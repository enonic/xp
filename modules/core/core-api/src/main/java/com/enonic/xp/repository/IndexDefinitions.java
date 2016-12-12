package com.enonic.xp.repository;

import java.util.Map;

import com.google.common.annotations.Beta;
import com.google.common.collect.Maps;

import com.enonic.xp.index.IndexType;

@Beta
public class IndexDefinitions
{
    private final Map<IndexType, IndexDefinition> configs;

    private IndexDefinitions( final Builder builder )
    {
        this.configs = builder.configs;
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
        private final Map<IndexType, IndexDefinition> configs = Maps.newHashMap();

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
