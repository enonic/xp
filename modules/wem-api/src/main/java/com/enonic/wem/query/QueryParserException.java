/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.query;

/**
 * This exception is used by the query parser.
 */
public final class QueryParserException
    extends RuntimeException
{
    /**
     * Construct the exception.
     */
    public QueryParserException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public QueryParserException( Exception cause )
    {
        super( cause.getMessage() );
    }
}
