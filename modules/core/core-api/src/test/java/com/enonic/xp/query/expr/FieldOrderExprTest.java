package com.enonic.xp.query.expr;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import com.enonic.xp.index.IndexPath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class FieldOrderExprTest
{
    @Test
    void testExpression()
    {
        final FieldExpr field = FieldExpr.from( "name" );
        final FieldOrderExpr expr = new FieldOrderExpr( field, OrderExpr.Direction.DESC );

        assertSame( field, expr.getField() );
        assertEquals( OrderExpr.Direction.DESC, expr.getDirection() );
        assertEquals( "name DESC", expr.toString() );
    }

    @Test
    void testExpressionWithoutDirection()
    {
        final FieldExpr field = FieldExpr.from( "name" );
        final FieldOrderExpr expr = new FieldOrderExpr( field, null );

        assertSame( field, expr.getField() );
        assertNull( expr.getDirection() );
        assertEquals( "name", expr.toString() );
    }

    @Test
    void testExpressionWithLanguage()
    {
        final FieldOrderExpr expr =
            FieldOrderExpr.create( IndexPath.from( "name" ), OrderExpr.Direction.ASC, Locale.forLanguageTag( "no" ) );

        assertEquals( OrderExpr.Direction.ASC, expr.getDirection() );
        assertEquals( Locale.forLanguageTag( "no" ), expr.getLanguage() );
        assertEquals( "name COLLATE no ASC", expr.toString() );
    }

    @Test
    void testExpressionWithoutLanguage()
    {
        final FieldOrderExpr expr = FieldOrderExpr.create( IndexPath.from( "name" ), OrderExpr.Direction.DESC );

        assertNull( expr.getLanguage() );
        assertEquals( "name DESC", expr.toString() );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( FieldOrderExpr.class ).withRedefinedSuperclass().withNonnullFields( "field" ).verify();
    }
}
