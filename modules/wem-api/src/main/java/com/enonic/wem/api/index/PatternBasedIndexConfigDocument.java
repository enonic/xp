package com.enonic.wem.api.index;

import java.util.SortedSet;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.data.DataPath;

public class PatternBasedIndexConfigDocument
    extends AbstractIndexConfigDocument
{
    public final ImmutableSortedSet<PatternConfig> patternConfigs;

    public final IndexConfig defaultConfig;

    public IndexConfig getDefaultConfig()
    {
        return defaultConfig;
    }

    public ImmutableSortedSet<PatternConfig> getPatternConfigs()
    {
        return patternConfigs;
    }

    private PatternBasedIndexConfigDocument( final Builder builder )
    {
        super( builder );
        patternConfigs = ImmutableSortedSet.copyOf( builder.patternConfigs );
        defaultConfig = builder.defaultConfig;
    }

    public static Builder create()
    {
        return new Builder();
    }


    @Override
    public IndexConfig getConfigForData( final DataPath dataPath )
    {
        for ( final PatternConfig patternConfig : patternConfigs )
        {
            if ( patternConfig.matches( dataPath ) )
            {
                return patternConfig.getIndexConfig();
            }
        }

        return defaultConfig;
    }

    public static final class Builder
        extends AbstractIndexConfigDocument.Builder<Builder>
    {
        private SortedSet<PatternConfig> patternConfigs = Sets.newTreeSet();

        private IndexConfig defaultConfig;

        private Builder()
        {
        }

        public Builder add( final String path, final IndexConfig indexConfig )
        {
            this.patternConfigs.add( PatternConfig.create().
                path( DataPath.from( path ) ).
                indexConfig( indexConfig ).
                build() );

            return this;
        }

        public Builder add( final DataPath path, final IndexConfig indexConfig )
        {
            this.patternConfigs.add( PatternConfig.create().
                path( path ).
                indexConfig( indexConfig ).
                build() );

            return this;
        }

        public Builder addPattern( final PatternConfig patternConfig )
        {
            this.patternConfigs.add( patternConfig );
            return this;
        }

        public Builder defaultConfig( IndexConfig defaultConfig )
        {
            this.defaultConfig = defaultConfig;
            return this;
        }

        public PatternBasedIndexConfigDocument build()
        {
            return new PatternBasedIndexConfigDocument( this );
        }
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PatternBasedIndexConfigDocument ) )
        {
            return false;
        }

        final PatternBasedIndexConfigDocument that = (PatternBasedIndexConfigDocument) o;

        if ( defaultConfig != null ? !defaultConfig.equals( that.defaultConfig ) : that.defaultConfig != null )
        {
            return false;
        }
        if ( patternConfigs != null ? !patternConfigs.equals( that.patternConfigs ) : that.patternConfigs != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = patternConfigs != null ? patternConfigs.hashCode() : 0;
        result = 31 * result + ( defaultConfig != null ? defaultConfig.hashCode() : 0 );
        return result;
    }
}
