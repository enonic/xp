package com.enonic.xp.index;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class IndexConfig
    implements Comparable<IndexConfig>
{
    public final static IndexConfig NONE = IndexConfig.create().
        enabled( false ).
        fulltext( false ).
        nGram( false ).
        decideByType( false ).
        includeInAllText( false ).
        path( false ).
        build();

    public final static IndexConfig FULLTEXT = IndexConfig.create().
        enabled( true ).
        fulltext( true ).
        nGram( true ).
        decideByType( false ).
        includeInAllText( true ).
        path( false ).
        build();

    public final static IndexConfig PATH = IndexConfig.create().
        enabled( true ).
        fulltext( false ).
        nGram( false ).
        decideByType( false ).
        includeInAllText( false ).
        path( true ).
        build();

    public final static IndexConfig MINIMAL = IndexConfig.create().
        enabled( true ).
        fulltext( false ).
        nGram( false ).
        decideByType( false ).
        includeInAllText( false ).
        path( false ).
        build();

    public final static IndexConfig BY_TYPE = IndexConfig.create().
        enabled( true ).
        fulltext( false ).
        nGram( false ).
        decideByType( true ).
        includeInAllText( false ).
        path( false ).
        build();

    public final static IndexConfig NGRAM = IndexConfig.create().
        enabled( true ).
        nGram( true ).
        fulltext( false ).
        decideByType( false ).
        includeInAllText( false ).
        path( false ).
        build();

    public static final Comparator<IndexConfig> COMPARATOR =
        Comparator.comparingInt( ( IndexConfig indexConfig ) -> indexConfig.decideByType ? 1 : 0 ).
            thenComparingInt( indexConfig -> indexConfig.enabled ? 1 : 0 ).
            thenComparingInt( indexConfig -> indexConfig.nGram ? 1 : 0 ).
            thenComparingInt( indexConfig -> indexConfig.fulltext ? 1 : 0 ).
            thenComparingInt( indexConfig -> indexConfig.includeInAllText ? 1 : 0 ).
            thenComparingInt( indexConfig -> indexConfig.path ? 1 : 0 );

    private final boolean decideByType;

    private final boolean enabled;

    private final boolean nGram;

    private final boolean fulltext;

    private final boolean includeInAllText;

    private final boolean path;

    private final ImmutableList<String> languages;

    private final ImmutableList<IndexValueProcessor> indexValueProcessors;

    private IndexConfig( Builder builder )
    {
        decideByType = builder.decideByType;
        enabled = builder.enabled;
        nGram = builder.nGram;
        fulltext = builder.fulltext;
        includeInAllText = builder.includeInAllText;
        indexValueProcessors = ImmutableList.copyOf( builder.indexValueProcessors );
        languages = ImmutableList.copyOf( builder.languages );
        path = builder.path;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final IndexConfig source )
    {
        return new Builder( source );
    }

    public boolean isDecideByType()
    {
        return decideByType;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public boolean isnGram()
    {
        return nGram;
    }

    public boolean isFulltext()
    {
        return fulltext;
    }

    public boolean isPath()
    {
        return path;
    }

    public boolean isIncludeInAllText()
    {
        return includeInAllText;
    }

    public boolean isStemmed()
    {
        return languages.size() > 0;
    }

    public ImmutableList<String> getLanguages()
    {
        return languages;
    }

    public ImmutableList<IndexValueProcessor> getIndexValueProcessors()
    {
        return indexValueProcessors;
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

        final IndexConfig that = (IndexConfig) o;

        if ( decideByType != that.decideByType )
        {
            return false;
        }
        if ( enabled != that.enabled )
        {
            return false;
        }
        if ( nGram != that.nGram )
        {
            return false;
        }
        if ( fulltext != that.fulltext )
        {
            return false;
        }
        if ( includeInAllText != that.includeInAllText )
        {
            return false;
        }
        if ( path != that.path )
        {
            return false;
        }
        return Objects.equals( languages, that.languages );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( decideByType, enabled, nGram, fulltext, includeInAllText, path, languages );
    }

    @Override
    public int compareTo( final IndexConfig o )
    {
        return COMPARATOR.
            compare( this, o );
    }

    public static final class Builder
    {
        private boolean decideByType = false;

        private boolean enabled = true;

        private boolean nGram = false;

        private boolean fulltext = false;

        private boolean includeInAllText = false;

        private boolean path = false;

        private List<String> languages = new ArrayList<>();

        private List<IndexValueProcessor> indexValueProcessors = new ArrayList<>();

        private Builder()
        {
        }

        private Builder( final IndexConfig indexConfig )
        {
            this.decideByType = indexConfig.decideByType;
            this.enabled = indexConfig.enabled;
            this.nGram = indexConfig.nGram;
            this.fulltext = indexConfig.fulltext;
            this.includeInAllText = indexConfig.includeInAllText;
            this.path = indexConfig.path;
            this.indexValueProcessors.addAll( indexConfig.indexValueProcessors );
            this.languages.addAll( indexConfig.languages );
        }

        public Builder decideByType( final boolean decideByType )
        {
            this.decideByType = decideByType;
            return this;
        }

        public Builder enabled( final boolean enabled )
        {
            this.enabled = enabled;
            return this;
        }

        public Builder nGram( final boolean nGram )
        {
            this.nGram = nGram;
            return this;
        }

        public Builder fulltext( final boolean fulltext )
        {
            this.fulltext = fulltext;
            return this;
        }

        public Builder path( final boolean path )
        {
            this.path = path;
            return this;
        }

        public Builder includeInAllText( final boolean includeInAllText )
        {
            this.includeInAllText = includeInAllText;
            return this;
        }

        public Builder addIndexValueProcessor( final IndexValueProcessor indexValueProcessor )
        {
            if ( indexValueProcessor != null )
            {
                this.indexValueProcessors.add( indexValueProcessor );
            }
            return this;
        }

        public Builder addLanguage( final String language )
        {
            if ( language != null )
            {
                this.languages.add( language );
            }
            return this;
        }

        public IndexConfig build()
        {
            return new IndexConfig( this );
        }
    }
}
