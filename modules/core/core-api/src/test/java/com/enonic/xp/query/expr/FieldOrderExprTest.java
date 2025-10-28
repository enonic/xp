package com.enonic.xp.query.expr;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

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
    void equalsContract()
    {
        EqualsVerifier.forClass( FieldOrderExpr.class ).withRedefinedSuperclass().withNonnullFields( "field" ).verify();
    }
}
