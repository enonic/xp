package com.enonic.xp.query;

import com.google.common.annotations.Beta;

@Beta
public final class QueryException
    extends RuntimeException
{
    public QueryException( final String message )
    {
        super( message );
    }
}
