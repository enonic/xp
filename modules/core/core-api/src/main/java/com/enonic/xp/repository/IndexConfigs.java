package com.enonic.xp.repository;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.xp.index.IndexType;

public class IndexConfigs
{
    private final Map<IndexType, IndexConfig> configs;

    private IndexConfigs( final Builder builder )
    {
        this.configs = builder.configs;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public IndexConfig get( final IndexType indexType )
    {
        return configs.get( indexType );
    }

    public static final class Builder
    {
        private final Map<IndexType, IndexConfig> configs = Maps.newHashMap();

        private Builder()
        {
        }

        public Builder add( final IndexType type, final IndexConfig config )
        {
            this.configs.put( type, config );
            return this;
        }

        public IndexConfigs build()
        {
            return new IndexConfigs( this );
        }
    }

}
