package com.enonic.wem.query;

import org.junit.Test;

import com.enonic.wem.query.expr.Query;
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
    public void parseWhereContains_default()
    {
        assertEquals( "age > 31.0 AND name CONTAINS \"runar\" ORDER BY age ASC, name DESC",
                      parse( "age > 31 and name contains 'runar' order by age, name desc" ) );
    }

    @Test
    public void parseWhere_match_2_params()
    {
        assertEquals( "age > 32.0 AND match(\"title\", \"this is a sentence\")",
                      parse( "age > 32 and match(\"title\", \"this is a sentence\")" ) );
    }

    @Test
    public void parseWhere_match_4_params()
    {
        assertEquals( "age > 32.0 AND match(\"title\", \"this is a sentence\", \"OR\", \"75%\")",
                      parse( "age > 32 and match(\"title\", \"this is a sentence\", \"OR\", \"75%\")" ) );
    }

    @Test
    public void parseWhere_multi_match()
    {
        assertEquals( "age > 32.0 AND multi_match([\"title^5\", \"data/descr\"], \"searching for ost\", \"AND\")",
                      parse( "age > 32 and multi_match([\"title^5\",\"data/descr\"], \"searching for ost\", \"AND\")" ) );
    }

    @Test
    public void parseWhere_relationExists_simple()
    {
        assertEquals( "relationExists(friend, name = \"rmy\")", parse( "relationExists(friend, name = \"rmy\")" ) );
    }

    @Test
    public void parseWhere_relation_count()
    {
        assertEquals( "relation_count(friend) > 5.0", parse( "relation_count(friend) > 5" ) );
    }

    @Test
    public void parseWhere_relationExists()
    {
        assertEquals( "likes IN (\"justin\") AND relationExists(friend, name = \"rmy\") AND relation_count(friend) > 5.0",
                      parse( "likes IN ('justin') AND relationExists(friend, name = 'rmy') AND relation_count(friend) > 5" ) );
    }

    @Test
    public void parseWhere_relationExists_true()
    {
        assertEquals( "likes IN (\"justin\") AND relationExists(friend, name = \"rmy\") = \"true\"",
                      parse( "likes IN ('justin') AND relationExists(friend, name = 'rmy') = 'true'" ) );
    }

    @Test
    public void parseWhere_date()
    {
        assertEquals( "age > 32.0 AND fuzzy(\"title\", date(\"2013-09-15 00:05:30\"))",
                      parse( "age > 32 and fuzzy(\"title\", date(\"2013-09-15 00:05:30\"))" ) );
    }

    @Test
    public void parseWhere_static_value()
    {
        assertEquals( "location = geoLocation(\"70,40\") AND modified > date(\"2013-01-01\")",
                      parse( "location = geoLocation('70,40') and modified > date('2013-01-01')" ) );
    }

    @Test
    public void parseWhere_fulltext()
    {
        assertEquals( "age > 30.0 AND fulltext(\"some text\") OR name = \"test\"",
                      parse( "(age > 30 and fulltext('some text')) or name = 'test'" ) );
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
        assertEquals( "ORDER BY GEODISTANCEORDER(mylocation, \"70,40\", \"km\") ASC",
                      parse( "ORDER BY GEODISTANCEORDER(mylocation, \"70,40\", \"km\") ASC" ) );
    }

    @Test
    public void parseOrderBy_score_desc()
    {
        assertEquals( "ORDER BY score() DESC", parse( "ORDER BY SCORE() DESC" ) );
    }

    private String parse( final String query )
    {
        final Query model = QueryParser.parse( query );
        assertNotNull( model );
        return model.toString();
    }
}
