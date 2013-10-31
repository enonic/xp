/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.wem.query.parser;

import com.enonic.wem.query.QueryException;
import com.enonic.wem.query.expr.Query;

public final class QueryParser
{
    private final QueryGrammar queryGrammar;

    public QueryParser()
    {
        queryGrammar = new QueryGrammar();
    }

    private Query doParse( final String query )
        throws QueryException
    {
        return queryGrammar.definition().parse( query );
    }

    public static Query parse( final String query )
        throws QueryException
    {
        return new QueryParser().doParse( query );
    }
}
