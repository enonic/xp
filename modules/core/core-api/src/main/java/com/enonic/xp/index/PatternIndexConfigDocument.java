package com.enonic.xp.index;

import java.util.Map;
import java.util.SortedSet;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.xp.data.PropertyPath;

@Beta
public class PatternIndexConfigDocument
    extends AbstractIndexConfigDocument
{
    private final ImmutableSortedSet<PathIndexConfig> pathIndexConfigs;

    private final Map<String, PathIndexConfig> pathIndexConfigMap;

    private final IndexConfig defaultConfig;

    public IndexConfig getDefaultConfig()
    {
        return defaultConfig;
    }

    private PatternIndexConfigDocument( final Builder builder )
    {
        super( builder );
        this.pathIndexConfigs = ImmutableSortedSet.copyOf( builder.pathIndexConfigs );
        this.pathIndexConfigMap = builder.stringPathIndexConfigMap;
        this.defaultConfig = builder.defaultConfig;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ImmutableSortedSet<PathIndexConfig> getPathIndexConfigs()
    {
        return pathIndexConfigs;
    }

    @Override
    public IndexConfig getConfigForPath( final PropertyPath dataPath )
    {
        final PathIndexConfig exactMatch = pathIndexConfigMap.get( dataPath.resetAllIndexesTo( 0 ).toString().toLowerCase() );

        if ( exactMatch != null )
        {
            return exactMatch.getIndexConfig();
        }

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

        private final Map<String, PathIndexConfig> stringPathIndexConfigMap = Maps.newHashMap();

        private IndexConfig defaultConfig = IndexConfig.BY_TYPE;

        private Builder()
        {
        }

        public Builder add( final String path, final IndexConfig indexConfig )
        {
            final PathIndexConfig pathIndexConfig = PathIndexConfig.create().
                path( PropertyPath.from( path ) ).
                indexConfig( indexConfig ).
                build();

            this.pathIndexConfigs.add( pathIndexConfig );
            this.stringPathIndexConfigMap.put( path.toLowerCase(), pathIndexConfig );

            return this;
        }

        public Builder add( final PropertyPath path, final IndexConfig indexConfig )
        {
            final PathIndexConfig pathIndexConfig = PathIndexConfig.create().
                path( path ).
                indexConfig( indexConfig ).
                build();
            this.pathIndexConfigs.add( pathIndexConfig );
            this.stringPathIndexConfigMap.put( path.resetAllIndexesTo( 0 ).toString().toLowerCase(), pathIndexConfig );

            return this;
        }

        public Builder addPattern( final PathIndexConfig pathIndexConfig )
        {
            this.pathIndexConfigs.add( pathIndexConfig );
            this.stringPathIndexConfigMap.put( pathIndexConfig.getPath().resetAllIndexesTo( 0 ).toString().toLowerCase(), pathIndexConfig );
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
