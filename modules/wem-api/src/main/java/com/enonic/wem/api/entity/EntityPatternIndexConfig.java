package com.enonic.wem.api.entity;

import java.util.SortedSet;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.data.DataPath;

public class EntityPatternIndexConfig
    extends EntityIndexConfig
{
    private final ImmutableSortedSet<PathIndexConfig> pathIndexConfigs;

    private final PropertyIndexConfig defaultConfig;

    private EntityPatternIndexConfig( final Builder builder )
    {
        super( builder.analyzer, builder.collection );
        this.pathIndexConfigs = ImmutableSortedSet.copyOf( builder.configs );
        this.defaultConfig = builder.defaultConfig;
    }

    public static PathIndexConfig.Builder newConfig()
    {
        return PathIndexConfig.newConfig();
    }

    public ImmutableSortedSet<PathIndexConfig> getPathIndexConfigs()
    {
        return pathIndexConfigs;
    }

    public PropertyIndexConfig getDefaultConfig()
    {
        return defaultConfig;
    }

    @Override
    public PropertyIndexConfig getPropertyIndexConfig( final DataPath dataPath )
    {
        for ( final PathIndexConfig config : pathIndexConfigs )
        {
            if ( dataPath.startsWith( config.getPath() ) )
            {
                return config.getPropertyIndexConfig();
            }
        }

        return defaultConfig;
    }

    public static class Builder
        extends EntityIndexConfig.Builder<Builder>
    {
        private SortedSet<PathIndexConfig> configs = Sets.newTreeSet();

        private PropertyIndexConfig defaultConfig = PropertyIndexConfig.INDEXNON_PROPERTY_CONFIG;

        public Builder addConfig( final PathIndexConfig config )
        {
            this.configs.add( config );
            return this;
        }

        public Builder addConfig( final String path, final PropertyIndexConfig propertyIndexConfig )
        {
            this.configs.add(
                PathIndexConfig.newConfig().path( DataPath.from( path ) ).propertyIndexConfig( propertyIndexConfig ).build() );
            return this;
        }

        public Builder defaultConfig( final PropertyIndexConfig propertyIndexConfig )
        {
            this.defaultConfig = propertyIndexConfig;
            return this;
        }

        public EntityIndexConfig build()
        {
            return new EntityPatternIndexConfig( this );
        }

    }

}
