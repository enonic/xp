package com.enonic.xp.query.filter;

import com.google.common.base.MoreObjects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class ExistsFilter
    extends FieldFilter
{
    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).omitNullValues().add( "fieldName", fieldName ).toString();
    }

    public ExistsFilter( final Builder builder )
    {
        super( builder );
    }

    public static class Builder
        extends FieldFilter.Builder<Builder>
    {
        public final ExistsFilter build()
        {
            return new ExistsFilter( this );
        }
    }
}


