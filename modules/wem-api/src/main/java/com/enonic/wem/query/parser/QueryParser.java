package com.enonic.wem.query.parser;

import org.codehaus.jparsec.error.ParserException;

import com.enonic.wem.query.QueryException;
import com.enonic.wem.query.expr.QueryExpr;

public final class QueryParser
{
    private final QueryGrammar grammar;

    public QueryParser()
    {
        this.grammar = new QueryGrammar();
    }

    private QueryExpr doParse( final String query )
    {
        try
        {
            return this.grammar.grammar().parse( query );
        }
        catch ( final ParserException e )
        {
            throw new QueryException( e.getMessage() );
        }
    }

    public static QueryExpr parse( final String query )
    {
        return new QueryParser().doParse( query );
    }
}
