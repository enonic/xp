package com.enonic.wem.api.item;


public final class PropertyIndexConfig
{
    private final boolean enabled;

    private final String analyzer;

    private PropertyIndexConfig( final Builder builder )
    {
        this.enabled = builder.enabled;
        this.analyzer = builder.analyzer;
    }

    public boolean enabled()
    {
        return enabled;
    }

    public String analyzer()
    {
        return analyzer;
    }

    public static Builder newPropertyIndexConfig()
    {
        return new Builder();
    }

    public static class Builder
    {
        private boolean enabled;

        private String analyzer;

        public Builder enabled( boolean value )
        {
            this.enabled = value;
            return this;
        }

        public Builder analyzer( String value )
        {
            this.analyzer = value;
            return this;
        }

        public PropertyIndexConfig build()
        {
            return new PropertyIndexConfig( this );
        }
    }
}
