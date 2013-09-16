package com.enonic.wem.query.parser;

import com.enonic.wem.query.Query;
import com.enonic.wem.query.QueryException;

public final class QueryParser
{
    private QueryParser()
    {
    }

    private Query doParse( final String query )
        throws QueryException
    {
        return new Query( null, null );
    }

    public static Query parse( final String query )
        throws QueryException
    {
        return new QueryParser().doParse( query );
    }
}
