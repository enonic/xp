package com.enonic.xp.query.filter;

import com.google.common.base.MoreObjects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ExistsFilter
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

    public static final class Builder
        extends FieldFilter.Builder<Builder>
    {
        private Builder()
        {
        }

        public ExistsFilter build()
        {
            return new ExistsFilter( this );
        }
    }
}


