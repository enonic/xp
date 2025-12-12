package com.enonic.xp.index;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

public final class AllTextIndexConfig
{
    private final ImmutableList<String> languages;

    private final boolean enabled;

    private final boolean nGram;

    private final boolean fulltext;

    private AllTextIndexConfig( final Builder builder )
    {
        this.languages = builder.languages.build();
        this.enabled = builder.enabled;
        this.nGram = builder.nGram;
        this.fulltext = builder.fulltext;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AllTextIndexConfig source )
    {
        return new Builder( source );
    }

    public List<String> getLanguages()
    {
        return languages;
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
        final AllTextIndexConfig that = (AllTextIndexConfig) o;
        return enabled == that.enabled && nGram == that.nGram && fulltext == that.fulltext &&
            Objects.equals( languages, that.languages );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( languages, enabled, nGram, fulltext );
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<String> languages;

        private boolean enabled;

        private boolean nGram;

        private boolean fulltext;

        private Builder()
        {
            languages = ImmutableList.builder();
        }

        private Builder( final AllTextIndexConfig source )
        {
            this.languages = ImmutableList.<String>builder().addAll( source.languages );
            this.enabled = source.enabled;
            this.nGram = source.nGram;
            this.fulltext = source.fulltext;
        }

        public Builder addLanguage( final String language )
        {
            this.languages.add( language );
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

        public AllTextIndexConfig build()
        {
            return new AllTextIndexConfig( this );
        }
    }
}
