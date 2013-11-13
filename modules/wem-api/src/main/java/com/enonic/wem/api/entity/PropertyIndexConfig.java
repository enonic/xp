package com.enonic.wem.api.entity;


import java.util.Objects;

public final class PropertyIndexConfig
{
    private final boolean enabled;

    private boolean fulltextEnabled;

    private boolean tokenizedEnabled;

    private PropertyIndexConfig( final Builder builder )
    {
        this.enabled = builder.enabled;
        this.fulltextEnabled = builder.fulltextEnabled;
        this.tokenizedEnabled = builder.tokenizedEnabled;
    }


    public boolean enabled()
    {
        return enabled;
    }

    public static Builder newPropertyIndexConfig()
    {
        return new Builder();
    }


    public boolean isFulltextEnabled()
    {
        return fulltextEnabled;
    }

    public boolean isTokenizeEnabled()
    {
        return tokenizedEnabled;
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

        final PropertyIndexConfig that = (PropertyIndexConfig) o;
        return Objects.equals( this.enabled, that.enabled ) &&
            Objects.equals( this.fulltextEnabled, that.fulltextEnabled ) &&
            Objects.equals( this.tokenizedEnabled, that.tokenizedEnabled );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.enabled, this.fulltextEnabled, this.tokenizedEnabled );
    }

    public static class Builder
    {
        private boolean enabled = true;

        private boolean fulltextEnabled = true;

        private boolean tokenizedEnabled = true;

        public Builder enabled( boolean value )
        {
            this.enabled = value;
            return this;
        }

        public PropertyIndexConfig build()
        {
            return new PropertyIndexConfig( this );
        }

        public Builder fulltextEnabled( final boolean fulltextEnabled )
        {
            this.fulltextEnabled = fulltextEnabled;
            return this;
        }

        public Builder autocompleteEnabled( final boolean autocompleteEnabled )
        {
            this.tokenizedEnabled = autocompleteEnabled;
            return this;
        }
    }
}
