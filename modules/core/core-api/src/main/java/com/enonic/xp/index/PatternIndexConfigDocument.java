package com.enonic.xp.index;

import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;

import org.apache.commons.lang.StringUtils;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.util.GlobPatternMatcher;

@Beta
public class PatternIndexConfigDocument
    extends AbstractIndexConfigDocument
{
    private final ImmutableSortedSet<PathIndexConfig> pathIndexConfigs;

    private final Map<String, PathIndexConfig> pathIndexConfigMap;

    private final IndexConfig defaultConfig;

    private final AllTextIndexConfig allTextConfig;

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
        this.allTextConfig = builder.allTextIndexConfig.build();
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
        return doGetConfigForPath( dataPath.resetAllIndexesTo( 0 ).toString() );
    }

    @Override
    public IndexConfig getConfigForPath( final IndexPath indexPath )
    {
        return doGetConfigForPath( indexPath.toString() );
    }

    private IndexConfig doGetConfigForPath( final String path )
    {
        final PathIndexConfig exactMatch = pathIndexConfigMap.get( path.toLowerCase() );

        if ( exactMatch != null )
        {
            return exactMatch.getIndexConfig();
        }

        for ( final PathIndexConfig pathIndexConfig : pathIndexConfigs )
        {
            if ( GlobPatternMatcher.match( pathIndexConfig.getPath().toString(), path, "." ) )
            {
                return pathIndexConfig.getIndexConfig();
            }

            if ( pathIndexConfig.matches( path ) )
            {
                return pathIndexConfig.getIndexConfig();
            }
        }

        return defaultConfig;
    }

    public AllTextIndexConfig getAllTextConfig()
    {
        return allTextConfig;
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

        if ( !Objects.equals( defaultConfig, that.defaultConfig ) )
        {
            return false;
        }
        if ( !Objects.equals( pathIndexConfigs, that.pathIndexConfigs ) )
        {
            return false;
        }
        if ( !Objects.equals( this.allTextConfig, that.allTextConfig ) )
        {
            return false;
        }

        return true;
    }

    public static final class Builder
        extends AbstractIndexConfigDocument.Builder<Builder>
    {
        private final SortedSet<PathIndexConfig> pathIndexConfigs = Sets.newTreeSet();

        private final Map<String, PathIndexConfig> stringPathIndexConfigMap = Maps.newHashMap();

        private IndexConfig defaultConfig = IndexConfig.BY_TYPE;

        private AllTextIndexConfig.Builder allTextIndexConfig = AllTextIndexConfig.create();

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

        public Builder addAllTextConfigLanguage( final String language )
        {
            if ( StringUtils.isNotBlank( language ) )
            {
                this.allTextIndexConfig.addLanguage( language );
            }
            return this;
        }

        public PatternIndexConfigDocument build()
        {
            return new PatternIndexConfigDocument( this );
        }
    }

    @Override
    public int hashCode()
    {
        int result = pathIndexConfigs != null ? pathIndexConfigs.hashCode() : 0;
        result = 31 * result + ( defaultConfig != null ? defaultConfig.hashCode() : 0 );
        return result;
    }
}
