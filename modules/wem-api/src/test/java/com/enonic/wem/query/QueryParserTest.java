package com.enonic.wem.query;

import org.junit.Test;

import com.enonic.wem.query.ast.QueryExpr;
import com.enonic.wem.query.parser.QueryParser;

import static org.junit.Assert.*;

public class QueryParserTest
{
    @Test
    public void parseEmpty()
    {
        assertEquals( "", parse( "" ) );
    }

    @Test
    public void parseWhere_default()
    {
        assertEquals( "age > 31.0 ORDER BY age ASC", parse( "age > 31 order by age" ) );
    }

    @Test
    public void parseWhere_dyamic_constraint()
    {
        assertEquals( "(age > 32.0 AND match('title', 'this is a sentence'))",
                      parse( "age > 32 and match(\"title\", \"this is a sentence\")" ) );
    }

    @Test
    public void parseWhere_dateTime()
    {
        assertEquals( "(age > 32.0 AND fuzzy('title', dateTime(\"2013-09-15T00:05:30\")))",
                      parse( "age > 32 and fuzzy(\"title\", dateTime(\"2013-09-15T00:05:30\"))" ) );
    }

    @Test
    public void parseOrderBy_default()
    {
        assertEquals( "ORDER BY age ASC", parse( "ORDER BY age" ) );
    }

    @Test
    public void parseOrderBy_desc()
    {
        assertEquals( "ORDER BY age DESC", parse( "ORDER BY age DESC" ) );
    }

    @Test
    public void parseOrderBy_asc()
    {
        assertEquals( "ORDER BY age ASC", parse( "ORDER BY age ASC" ) );
    }

    @Test
    public void parseOrderBy_default_desc()
    {
        assertEquals( "ORDER BY age ASC, name DESC", parse( "ORDER BY age, name DESC" ) );
    }

    @Test
    public void parseOrderBy_geoDistanceOrder_asc()
    {
        assertEquals( "ORDER BY geoDistanceOrder('mylocation', '70,40', 'km') ASC",
                      parse( "ORDER BY geoDistanceOrder('mylocation', \"70,40\", \"km\")" ) );
    }

    @Test
    public void parseOrderBy_score_desc()
    {
        assertEquals( "ORDER BY score() DESC", parse( "ORDER BY score() DESC" ) );
    }

    private String parse( final String query )
    {
        final QueryExpr model = QueryParser.parse( query );
        assertNotNull( model );
        return model.toString();
    }
}
