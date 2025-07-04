package com.enonic.xp.index;

import java.util.List;

import com.google.common.collect.ImmutableList;

public final class AllTextIndexConfig
{
    private final ImmutableList<String> languages;

    private AllTextIndexConfig( final ImmutableList<String> languages )
    {
        this.languages = languages;
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


    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof AllTextIndexConfig && languages.equals( ( (AllTextIndexConfig) o ).languages );
    }

    @Override
    public int hashCode()
    {
        return languages.hashCode();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<String> languages;

        Builder()
        {
            languages = ImmutableList.builder();
        }

        Builder( final AllTextIndexConfig source )
        {
            this.languages = ImmutableList.<String>builder().addAll( source.languages );
        }

        public Builder addLanguage( final String language )
        {
            this.languages.add( language );
            return this;
        }

        public AllTextIndexConfig build()
        {
            return new AllTextIndexConfig( languages.build() );
        }
    }
}
