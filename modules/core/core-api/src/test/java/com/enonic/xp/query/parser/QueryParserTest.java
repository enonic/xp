package com.enonic.xp.query.parser;

import org.junit.jupiter.api.Test;

import com.enonic.xp.query.QueryException;
import com.enonic.xp.query.expr.QueryExpr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class QueryParserTest
{
    @Test
    public void comparison()
    {
        testQuery( "a = 3", "a = 3.0" );
        testQuery( "a != 3", "a != 3.0" );
        testQuery( "a > 3", "a > 3.0" );
        testQuery( "a >= 3", "a >= 3.0" );
        testQuery( "a < 3", "a < 3.0" );
        testQuery( "a <= 3", "a <= 3.0" );
    }

    private void testQuery( final String query, final String expected )
    {
        final QueryExpr expr = QueryParser.parse( query );

        assertNotNull( expr );
        assertEquals( expected, expr.toString() );
    }

    @Test
    public void invalid_query()
    {
        assertThrows(QueryException.class, () -> QueryParser.parse( "a - 3" ) );
    }

    @Test
    public void like_compare()
    {
        testQuery( "a like '3'", "a LIKE '3'" );
        testQuery( "a not LIKE '3'", "a NOT LIKE '3'" );
    }

    @Test
    public void in_compare()
    {
        testQuery( "a IN ('3')", "a IN ('3')" );
        testQuery( "a NOT in ('3')", "a NOT IN ('3')" );
        testQuery( "a IN (1, 2, '3')", "a IN (1.0, 2.0, '3')" );
    }

    @Test
    public void geopoint_function()
    {
        testQuery( "a = geoPoint('1,1')", "a = geoPoint('1.0,1.0')" );
    }

    @Test
    public void instant_function()
    {
        testQuery( "a = instant('2013-11-11T22:22:22.000Z')", "a = instant('2013-11-11T22:22:22Z')" );
    }

    @Test
    public void not_expression()
    {
        testQuery( "NOT(a > 3 AND b = 3)", "NOT ((a > 3.0 AND b = 3.0))" );
    }

    @Test
    public void and_expression()
    {
        testQuery( "a > 3 AND b = 3 AND c = 3", "((a > 3.0 AND b = 3.0) AND c = 3.0)" );
    }

    @Test
    public void or_expression()
    {
        testQuery( "a > 3 OR b = 3 OR c = 3", "((a > 3.0 OR b = 3.0) OR c = 3.0)" );
    }

    @Test
    public void and_or_expression()
    {
        testQuery( "a > 3 AND b = 3 OR c = 3", "((a > 3.0 AND b = 3.0) OR c = 3.0)" );
    }

    @Test
    public void dynamic_constraint()
    {
        testQuery( "a > 3 AND geoLocation('arg1', 2)", "(a > 3.0 AND geoLocation('arg1', 2.0))" );
    }

    @Test
    public void empty_query()
    {
        testQuery( "", "" );
    }

    @Test
    public void full_query()
    {
        testQuery( "a > 3 ORDER BY a DESC", "a > 3.0 ORDER BY a DESC" );
    }

    @Test
    public void illegal_value_function()
    {
        assertThrows(QueryException.class, () -> QueryParser.parse( "a = badFunc()" ) );
    }

    @Test
    public void field_order()
    {
        testQuery( "ORDER BY a", "ORDER BY a ASC" );
        testQuery( "order BY a DESC", "ORDER BY a DESC" );
        testQuery( "ORDER BY a, b", "ORDER BY a ASC, b ASC" );
        testQuery( "order by a DESC, b ASC", "ORDER BY a DESC, b ASC" );
    }

    @Test
    public void dynamic_order()
    {
        testQuery( "ORDER BY score()", "ORDER BY score() ASC" );
        testQuery( "order BY geoLocation('arg') DESC", "ORDER BY geoLocation('arg') DESC" );
        testQuery( "ORDER BY a, score()", "ORDER BY a ASC, score() ASC" );
        testQuery( "order by score() DESC, a", "ORDER BY score() DESC, a ASC" );
    }

    @Test
    public void invalid_order_expression()
    {
        assertThrows(QueryException.class, () ->  QueryParser.parseCostraintExpression( "AND" ));
    }

    @Test
    public void invalid_constraint_expression()
    {
        assertThrows(QueryException.class, () -> QueryParser.parseOrderExpressions( "AND < 3" ));
    }
}
