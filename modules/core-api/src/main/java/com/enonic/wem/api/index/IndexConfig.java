package com.enonic.wem.api.index;

public class IndexConfig
{
    public final static IndexConfig NONE = IndexConfig.create().
        enabled( false ).
        fulltext( false ).
        nGram( false ).
        decideByType( false ).
        includeInAllText( false ).
        build();


    public final static IndexConfig FULLTEXT = IndexConfig.create().
        enabled( true ).
        fulltext( true ).
        nGram( true ).
        decideByType( false ).
        includeInAllText( true ).
        build();

    public final static IndexConfig MINIMAL = IndexConfig.create().
        enabled( true ).
        fulltext( false ).
        nGram( false ).
        decideByType( false ).
        includeInAllText( false ).
        build();

    public final static IndexConfig BY_TYPE = IndexConfig.create().
        enabled( true ).
        fulltext( false ).
        nGram( false ).
        decideByType( true ).
        includeInAllText( false ).
        build();

    private final boolean decideByType;

    private final boolean enabled;

    private final boolean nGram;

    private final boolean fulltext;

    private final boolean includeInAllText;

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

    public boolean isIncludeInAllText()
    {
        return includeInAllText;
    }

    private IndexConfig( Builder builder )
    {
        decideByType = builder.decideByType;
        enabled = builder.enabled;
        nGram = builder.nGram;
        fulltext = builder.fulltext;
        includeInAllText = builder.includeInAllText;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private boolean decideByType;

        private boolean enabled;

        private boolean nGram;

        private boolean fulltext;

        private boolean includeInAllText;

        private Builder()
        {
        }

        public Builder decideByType( boolean decideByType )
        {
            this.decideByType = decideByType;
            return this;
        }

        public Builder enabled( boolean enabled )
        {
            this.enabled = enabled;
            return this;
        }

        public Builder nGram( boolean nGram )
        {
            this.nGram = nGram;
            return this;
        }

        public Builder fulltext( boolean fulltext )
        {
            this.fulltext = fulltext;
            return this;
        }

        public Builder includeInAllText( boolean includeInAllText )
        {
            this.includeInAllText = includeInAllText;
            return this;
        }

        public IndexConfig build()
        {
            return new IndexConfig( this );
        }
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof IndexConfig ) )
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
        if ( fulltext != that.fulltext )
        {
            return false;
        }
        if ( includeInAllText != that.includeInAllText )
        {
            return false;
        }
        if ( nGram != that.nGram )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = ( decideByType ? 1 : 0 );
        result = 31 * result + ( enabled ? 1 : 0 );
        result = 31 * result + ( nGram ? 1 : 0 );
        result = 31 * result + ( fulltext ? 1 : 0 );
        result = 31 * result + ( includeInAllText ? 1 : 0 );
        return result;
    }
}
