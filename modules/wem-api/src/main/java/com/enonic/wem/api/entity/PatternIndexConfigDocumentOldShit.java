package com.enonic.wem.api.entity;

import java.util.SortedSet;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.data.DataPath;

public class PatternIndexConfigDocumentOldShit
    extends IndexConfigDocumentOldShit
{
    private final ImmutableSortedSet<PathIndexConfig> pathIndexConfigs;

    private final PropertyIndexConfig defaultConfig;

    private PatternIndexConfigDocumentOldShit( final Builder builder )
    {
        super( builder );
        this.pathIndexConfigs = ImmutableSortedSet.copyOf( builder.configs );
        this.defaultConfig = builder.defaultConfig;
    }

    public static PatternIndexConfigDocumentOldShit.Builder create()
    {
        return new Builder();
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
    public PropertyIndexConfig getIndexConfig( final DataPath dataPath )
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
        extends IndexConfigDocumentOldShit.Builder<Builder>
    {
        private SortedSet<PathIndexConfig> configs = Sets.newTreeSet();

        private PropertyIndexConfig defaultConfig = PropertyIndexConfig.SKIP;

        public Builder addConfig( final PathIndexConfig config )
        {
            this.configs.add( config );
            return this;
        }

        public Builder addConfig( final String path, final PropertyIndexConfig propertyIndexConfig )
        {
            this.configs.add( PathIndexConfig.create().path( DataPath.from( path ) ).propertyIndexConfig( propertyIndexConfig ).build() );
            return this;
        }

        public Builder defaultConfig( final PropertyIndexConfig propertyIndexConfig )
        {
            this.defaultConfig = propertyIndexConfig;
            return this;
        }

        public IndexConfigDocumentOldShit build()
        {
            return new PatternIndexConfigDocumentOldShit( this );
        }

    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }

        final PatternIndexConfigDocumentOldShit that = (PatternIndexConfigDocumentOldShit) o;

        if ( defaultConfig != null ? !defaultConfig.equals( that.defaultConfig ) : that.defaultConfig != null )
        {
            return false;
        }
        if ( pathIndexConfigs != null ? !pathIndexConfigs.equals( that.pathIndexConfigs ) : that.pathIndexConfigs != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + ( pathIndexConfigs != null ? pathIndexConfigs.hashCode() : 0 );
        result = 31 * result + ( defaultConfig != null ? defaultConfig.hashCode() : 0 );
        return result;
    }
}
