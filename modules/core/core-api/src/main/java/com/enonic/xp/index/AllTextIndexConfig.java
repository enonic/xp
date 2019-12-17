package com.enonic.xp.index;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

public final class AllTextIndexConfig
{
    private List<String> languages;

    private AllTextIndexConfig( final Builder builder )
    {
        this.languages = ImmutableList.copyOf( builder.languages );
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
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final AllTextIndexConfig that = (AllTextIndexConfig) o;
        return Objects.equals( languages, that.languages );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( languages );
    }

    public static class Builder
    {
        private List<String> languages = new ArrayList<>();

        Builder()
        {
        }

        Builder( final AllTextIndexConfig source )
        {
            this.languages = new ArrayList<>( source.languages );
        }

        public Builder addLanguage( final String language )
        {
            this.languages.add( language );
            return this;
        }

        public AllTextIndexConfig build()
        {
            return new AllTextIndexConfig( this );
        }
    }
}
