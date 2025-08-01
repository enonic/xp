package com.enonic.xp.query.suggester;

public final class TermSuggestionQuery
    extends SuggestionQuery
{
    private final Sort sort;

    private final SuggestMode suggestMode;

    private final Integer maxEdits;

    private final Integer prefixLength;

    private final Integer minWordLength;

    private final Integer maxInspections;

    private final Float minDocFreq;

    private final Float maxTermFreq;

    private final StringDistance stringDistance;

    private TermSuggestionQuery( final Builder builder )
    {
        super( builder );

        this.sort = builder.sort;
        this.suggestMode = builder.suggestMode;
        this.maxEdits = builder.maxEdits;
        this.prefixLength = builder.prefixLength;
        this.minWordLength = builder.minWordLength;
        this.maxInspections = builder.maxInspections;
        this.minDocFreq = builder.minDocFreq;
        this.maxTermFreq = builder.maxTermFreq;
        this.stringDistance = builder.stringDistance;
    }

    public Sort getSort()
    {
        return sort;
    }

    public SuggestMode getSuggestMode()
    {
        return suggestMode;
    }

    public Integer getMaxEdits()
    {
        return maxEdits;
    }

    public Integer getPrefixLength()
    {
        return prefixLength;
    }

    public Integer getMinWordLength()
    {
        return minWordLength;
    }

    public Integer getMaxInspections()
    {
        return maxInspections;
    }

    public Float getMinDocFreq()
    {
        return minDocFreq;
    }

    public Float getMaxTermFreq()
    {
        return maxTermFreq;
    }

    public StringDistance getStringDistance()
    {
        return stringDistance;
    }

    @Override
    public String toString()
    {
        return "TermSuggestionQuery{" + "sort=" + sort + ", suggestMode=" + suggestMode + ", maxEdits=" + maxEdits + ", prefixLength=" +
            prefixLength + ", minWordLength=" + minWordLength + ", maxInspections=" + maxInspections + ", minDocFreq=" + minDocFreq +
            ", maxTermFreq=" + maxTermFreq + ", stringDistance=" + stringDistance + '}';
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    public static final class Builder
        extends SuggestionQuery.Builder<Builder>
    {
        private Sort sort;

        private SuggestMode suggestMode = SuggestMode.MISSING;

        private Integer maxEdits;

        private Integer prefixLength;

        private Integer minWordLength;

        private Integer maxInspections;

        private Float minDocFreq;

        private Float maxTermFreq;

        private StringDistance stringDistance;

        private Builder( final String name )
        {
            super( name );
        }

        public Builder sort( final Sort sort )
        {
            this.sort = sort;
            return this;
        }

        public Builder suggestMode( final SuggestMode suggestMode )
        {
            this.suggestMode = suggestMode;
            return this;
        }

        public Builder maxEdits( final Integer maxEdits )
        {
            this.maxEdits = maxEdits;
            return this;
        }

        public Builder prefixLength( final Integer prefixLength )
        {
            this.prefixLength = prefixLength;
            return this;
        }

        public Builder minWordLength( final Integer minWordLength )
        {
            this.minWordLength = minWordLength;
            return this;
        }

        public Builder maxInspections( final Integer maxInspections )
        {
            this.maxInspections = maxInspections;
            return this;
        }

        public Builder minDocFreq( final Float minDocFreq )
        {
            this.minDocFreq = minDocFreq;
            return this;
        }

        public Builder maxTermFreq( final Float maxTermFreq )
        {
            this.maxTermFreq = maxTermFreq;
            return this;
        }

        public Builder stringDistance( final StringDistance stringDistance )
        {
            this.stringDistance = stringDistance;
            return this;
        }

        public TermSuggestionQuery build()
        {
            return new TermSuggestionQuery( this );
        }
    }

    public enum Sort
    {
        SCORE( "score" ), FREQUENCY( "frequency" );

        private final String value;

        Sort( final String value )
        {
            this.value = value;
        }

        public String value()
        {
            return this.value;
        }

        public static Sort from( final String state )
        {
            if ( SCORE.value().equals( state ) )
            {
                return SCORE;
            }
            if ( FREQUENCY.value().equals( state ) )
            {
                return FREQUENCY;
            }

            return null;
        }
    }

    public enum StringDistance
    {
        INTERNAL( "internal" ), DAMERAU_LEVENSHTEIN( "damerau_levenshtein" ), LEVENSHTEIN( "levenshtein" ), JAROWINKLER(
        "jarowinkler" ), NGRAM( "ngram" );

        private final String value;

        StringDistance( final String value )
        {
            this.value = value;
        }

        public String value()
        {
            return this.value;
        }

        public static StringDistance from( final String state )
        {

            if ( INTERNAL.value().equals( state ) )
            {
                return INTERNAL;
            }
            if ( DAMERAU_LEVENSHTEIN.value().equals( state ) )
            {
                return DAMERAU_LEVENSHTEIN;
            }
            if ( LEVENSHTEIN.value().equals( state ) )
            {
                return LEVENSHTEIN;
            }
            if ( JAROWINKLER.value().equals( state ) )
            {
                return JAROWINKLER;
            }
            if ( NGRAM.value().equals( state ) )
            {
                return NGRAM;
            }

            return null;
        }
    }

    public enum SuggestMode
    {
        MISSING( "missing" ), POPULAR( "popular" ), ALWAYS( "always" );

        private final String value;

        SuggestMode( final String value )
        {
            this.value = value;
        }

        public String value()
        {
            return this.value;
        }

        public static SuggestMode from( final String state )
        {

            if ( MISSING.value().equals( state ) )
            {
                return MISSING;
            }
            if ( POPULAR.value().equals( state ) )
            {
                return POPULAR;
            }
            if ( ALWAYS.value().equals( state ) )
            {
                return ALWAYS;
            }

            return null;
        }
    }
}
