package com.enonic.xp.index;

import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.util.GlobPatternMatcher;

import static com.google.common.base.Strings.nullToEmpty;

@PublicApi
public final class PatternIndexConfigDocument
    extends AbstractIndexConfigDocument
{
    private final ImmutableSortedSet<PathIndexConfig> pathIndexConfigs;

    private final ImmutableMap<IndexPath, PathIndexConfig> pathIndexConfigMap;

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
        this.pathIndexConfigMap = builder.pathIndexConfigs.stream()
            .collect( ImmutableMap.toImmutableMap( pic -> IndexPath.from( pic.getPath() ), Function.identity() ) );
        this.defaultConfig = builder.defaultConfig;
        this.allTextConfig = builder.allTextIndexConfig != null ? builder.allTextIndexConfig :
            AllTextIndexConfig.create().enabled( true ).nGram( true ).fulltext( false ).build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final PatternIndexConfigDocument source )
    {
        return new Builder( source );
    }

    public SortedSet<PathIndexConfig> getPathIndexConfigs()
    {
        return pathIndexConfigs;
    }

    @Override
    public IndexConfig getConfigForPath( final IndexPath indexPath )
    {
        final PathIndexConfig exactMatch = pathIndexConfigMap.get( indexPath );

        if ( exactMatch != null )
        {
            return exactMatch.getIndexConfig();
        }

        final String path = indexPath.toString();
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

    @Override
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
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }
        final PatternIndexConfigDocument that = (PatternIndexConfigDocument) o;
        return Objects.equals( pathIndexConfigs, that.pathIndexConfigs ) &&
            Objects.equals( defaultConfig, that.defaultConfig ) && Objects.equals( allTextConfig, that.allTextConfig );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), pathIndexConfigs, defaultConfig, allTextConfig );
    }

    public static final class Builder
        extends AbstractIndexConfigDocument.Builder<Builder>
    {
        private SortedSet<PathIndexConfig> pathIndexConfigs = new TreeSet<>();

        private IndexConfig defaultConfig = IndexConfig.BY_TYPE;

        private AllTextIndexConfig allTextIndexConfig;

        private Builder()
        {
        }

        private Builder( final PatternIndexConfigDocument source )
        {
            this.pathIndexConfigs = new TreeSet<>( source.pathIndexConfigs );
            this.defaultConfig = IndexConfig.create( source.defaultConfig ).build();
            this.allTextIndexConfig = source.allTextConfig;
        }

        public Builder add( final String path, final IndexConfig indexConfig )
        {
            add( PathIndexConfig.create().
                path( PropertyPath.from( path ) ).
                indexConfig( indexConfig ).
                build() );

            return this;
        }

        public Builder add( final PropertyPath path, final IndexConfig indexConfig )
        {
            add( PathIndexConfig.create().
                path( path ).
                indexConfig( indexConfig ).
                build() );

            return this;
        }

        public Builder add( final PathIndexConfig pathIndexConfig )
        {
            this.pathIndexConfigs.add( pathIndexConfig );
            return this;
        }

        public Builder remove( final PathIndexConfig pathIndexConfig )
        {
            this.pathIndexConfigs.remove( pathIndexConfig );
            return this;
        }

        public Builder defaultConfig( IndexConfig defaultConfig )
        {
            this.defaultConfig = defaultConfig;
            return this;
        }

        public Builder addAllTextConfigLanguage( final String language )
        {
            if ( this.allTextIndexConfig == null )
            {
                this.allTextIndexConfig = AllTextIndexConfig.create().enabled( true ).nGram( true ).fulltext( false ).build();
            }
            if ( !nullToEmpty( language ).isBlank() )
            {
                this.allTextIndexConfig = AllTextIndexConfig.create( this.allTextIndexConfig ).addLanguage( language ).build();
            }
            return this;
        }

        public Builder allTextConfig( final AllTextIndexConfig allTextConfig )
        {
            this.allTextIndexConfig = allTextIndexConfig;
            return this;
        }

        public PatternIndexConfigDocument build()
        {
            return new PatternIndexConfigDocument( this );
        }
    }
}
