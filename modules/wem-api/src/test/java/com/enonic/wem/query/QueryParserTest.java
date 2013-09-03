package com.enonic.wem.query;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class QueryParserTest
{
    @Test
    public void parseEmpty()
    {
        assertEquals( "", parse( "" ) );
    }

    @Test
    @Ignore
    public void parseOrderBy_default()
    {
        assertEquals( "ORDER BY age ASC", parse( "ORDER BY age" ) );
    }

    @Test
    @Ignore
    public void parseOrderBy_desc()
    {
        assertEquals( "ORDER BY age DESC", parse( "ORDER BY age DESC" ) );
    }

    @Test
    @Ignore
    public void parseOrderBy_asc()
    {
        assertEquals( "ORDER BY age ASC", parse( "ORDER BY age ASC" ) );
    }

    @Test
    @Ignore
    public void parseOrderBy_default_desc()
    {
        assertEquals( "ORDER BY age ASC, name DESC", parse( "ORDER BY age, name DESC" ) );
    }

    private String parse( final String query )
    {
        final Query model = QueryParser.parse( query );
        assertNotNull( model );
        return model.toString();
    }
}
