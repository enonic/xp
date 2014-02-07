package com.enonic.wem.api.content.site;


import com.google.common.base.Objects;

public final class Vendor
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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof Vendor ) )
        {
            return false;
        }

        final Vendor that = (Vendor) o;

        return java.util.Objects.equals( this.name, that.name ) && java.util.Objects.equals( this.url, that.url );
    }

    @Override
    public int hashCode()
    {
        return java.util.Objects.hash( name, url );
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper( this ).
            add( "name", name ).
            add( "url", url ).
            omitNullValues().
            toString();
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
