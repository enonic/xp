package com.enonic.wem.api.content.site;


public class Vendor
{
    private final String name;

    private final String url;

    public Vendor( final Builder builder )
    {
        this.name = builder.name;
        this.url = builder.url;
    }

    public String getName()
    {
        return name;
    }

    public String getUrl()
    {
        return url;
    }

    public static Builder newVendor()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String name;

        private String url;

        public Builder name( String value )
        {
            this.name = value;
            return this;
        }

        public Builder url( String value )
        {
            this.url = value;
            return this;
        }

        public Vendor build()
        {
            return new Vendor( this );
        }
    }
}
