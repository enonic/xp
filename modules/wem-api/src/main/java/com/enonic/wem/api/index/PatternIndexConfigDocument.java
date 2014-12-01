package com.enonic.wem.api.index;

import java.util.SortedSet;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.data2.PropertyPath;

public class PatternIndexConfigDocument
    extends AbstractIndexConfigDocument
{
    public final ImmutableSortedSet<PathIndexConfig> pathIndexConfigs;

    private final IndexConfig defaultConfig;

    public IndexConfig getDefaultConfig()
    {
        return defaultConfig;
    }

    private PatternIndexConfigDocument( final Builder builder )
    {
        super( builder );
        pathIndexConfigs = ImmutableSortedSet.copyOf( builder.pathIndexConfigs );
        defaultConfig = builder.defaultConfig;
    }

    public static Builder create()
    {
        return new Builder();
    }


    @Override
    public IndexConfig getConfigForPath( final PropertyPath dataPath )
    {
        for ( final PathIndexConfig pathIndexConfig : pathIndexConfigs )
        {
            if ( pathIndexConfig.matches( dataPath ) )
            {
                return pathIndexConfig.getIndexConfig();
            }
        }

        return defaultConfig;
    }

    public static final class Builder
        extends AbstractIndexConfigDocument.Builder<Builder>
    {
        private final SortedSet<PathIndexConfig> pathIndexConfigs = Sets.newTreeSet();

        private IndexConfig defaultConfig = IndexConfig.BY_TYPE;

        private Builder()
        {
        }

        public Builder add( final String path, final IndexConfig indexConfig )
        {
            this.pathIndexConfigs.add( PathIndexConfig.create().
                path( PropertyPath.from( path ) ).
                indexConfig( indexConfig ).
                build() );

            return this;
        }

        public Builder add( final PropertyPath path, final IndexConfig indexConfig )
        {
            this.pathIndexConfigs.add( PathIndexConfig.create().
                path( path ).
                indexConfig( indexConfig ).
                build() );

            return this;
        }

        public Builder addPattern( final PathIndexConfig pathIndexConfig )
        {
            this.pathIndexConfigs.add( pathIndexConfig );
            return this;
        }

        public Builder defaultConfig( IndexConfig defaultConfig )
        {
            this.defaultConfig = defaultConfig;
            return this;
        }

        public PatternIndexConfigDocument build()
        {
            return new PatternIndexConfigDocument( this );
        }
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PatternIndexConfigDocument ) )
        {
            return false;
        }

        final PatternIndexConfigDocument that = (PatternIndexConfigDocument) o;

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
        int result = pathIndexConfigs != null ? pathIndexConfigs.hashCode() : 0;
        result = 31 * result + ( defaultConfig != null ? defaultConfig.hashCode() : 0 );
        return result;
    }
}
