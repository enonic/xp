package com.enonic.xp.site;


import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.xdata.XDataName;

@PublicApi
public final class XDataMapping
{
    private final XDataName xDataName;

    private final String allowContentTypes;

    private final Boolean optional;

    public XDataMapping( final Builder builder )
    {
        this.xDataName = builder.xDataName;
        this.allowContentTypes = builder.allowContentTypes;
        this.optional = builder.optional;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public XDataName getXDataName()
    {
        return xDataName;
    }

    public String getAllowContentTypes()
    {
        return allowContentTypes;
    }

    public Boolean getOptional()
    {
        return optional;
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
        final XDataMapping that = (XDataMapping) o;
        return Objects.equals( xDataName, that.xDataName ) && Objects.equals( allowContentTypes, that.allowContentTypes ) &&
            Objects.equals( optional, that.optional );
    }

    @Override
    public int hashCode()
    {

        return Objects.hash( xDataName, allowContentTypes, optional );
    }

    public static class Builder
    {
        private XDataName xDataName;

        private String allowContentTypes;

        private Boolean optional = false;

        public Builder xDataName( final XDataName xDataName )
        {
            this.xDataName = xDataName;
            return this;
        }

        public Builder allowContentTypes( final String allowContentTypes )
        {
            this.allowContentTypes = allowContentTypes;
            return this;
        }

        public Builder optional( final Boolean optional )
        {
            this.optional = optional;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( xDataName, "XData name cannot be null" );
        }

        public XDataMapping build()
        {
            validate();
            return new XDataMapping( this );
        }
    }
}
