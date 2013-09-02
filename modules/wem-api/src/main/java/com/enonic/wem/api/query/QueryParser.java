package com.enonic.wem.api.query;

import java.util.ArrayList;

import com.google.common.base.Joiner;

// Modelled after https://github.com/apache/jackrabbit-oak/blob/trunk/oak-core/src/main/java/org/apache/jackrabbit/oak/query/SQL2Parser.java
public final class QueryParser
{
    private QueryObjectModelFactory factory;

    private String currentToken;

    private boolean currentTokenQuoted;

    private ArrayList<String> expected;

    private int parseIndex;

    private String statement;

    private QueryParser()
    {
    }

    private Query doParse( final String query )
        throws QueryException
    {
        initialize( query );
        this.expected = new ArrayList<>();

        final Constraint constraint = parseConstraint();
        final Ordering[] orderings = parseOrderings();

        return new Query();
    }

    private boolean readIf( final String token )
    {
        if ( isToken( token ) )
        {
            read();
            return true;
        }

        return false;
    }

    private boolean isToken( final String token )
    {
        final boolean result = token.equalsIgnoreCase( this.currentToken ) && !this.currentTokenQuoted;
        if ( result )
        {
            return true;
        }

        addExpected( token );
        return false;
    }

    private void addExpected( final String token )
    {
        if ( this.expected != null )
        {
            this.expected.add( token );
        }
    }

    private void read()
    {
        // TODO: Implement
    }

    private void read( final String expected )
    {
        if ( !expected.equalsIgnoreCase( this.currentToken ) || this.currentTokenQuoted )
        {
            throw createException( expected );
        }

        read();
    }

    private void initialize( final String query )
    {
        // TODO: Implement
    }

    private Constraint parseConstraint()
    {
        Constraint c = parseAnd();
        while ( readIf( "OR" ) )
        {
            c = null; // TODO: this.factory.or( c, parseAnd() );
        }

        return c;
    }

    private Constraint parseAnd()
    {
        Constraint c = parseCondition();
        while ( readIf( "AND" ) )
        {
            c = factory.and( c, parseCondition() );
        }

        return c;
    }

    private Constraint parseCondition()
    {
        return null;
    }

    private Ordering[] parseOrderings()
    {
        if ( readIf( "ORDER" ) )
        {
            read( "BY" );
            return parseOrderList();
        }

        return null;
    }

    private Ordering[] parseOrderList()
    {
        return null;
    }

    private QueryException createException()
    {
        if ( this.expected == null || this.expected.isEmpty() )
        {
            return createException( null );
        }

        final String str = Joiner.on( "," ).join( this.expected );
        return createException( str );
    }

    private QueryException createException( final String expected )
    {
        final int index = Math.max( 0, Math.min( this.parseIndex, this.statement.length() - 1 ) );
        String query = this.statement.substring( 0, index ) + "(*)" + this.statement.substring( index ).trim();

        if ( expected != null )
        {
            query += "; expected: " + expected;
        }

        return new QueryException( "Query: %s", query );
    }

    public static Query parse( final String query )
        throws QueryException
    {
        return new QueryParser().doParse( query );
    }
}
