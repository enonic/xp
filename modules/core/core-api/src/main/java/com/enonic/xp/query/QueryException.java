package com.enonic.xp.query;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class QueryException
    extends RuntimeException
{
    public QueryException( final String message )
    {
        super( message );
    }
}
