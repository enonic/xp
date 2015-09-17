package com.enonic.xp.query.filter;

import com.google.common.annotations.Beta;

@Beta
public class ExistsFilter
    extends FieldFilter
{
    public static Builder create()
    {
        return new Builder();
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


