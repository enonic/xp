package com.enonic.xp.query.expr;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FieldExprTest
{
    @Test
    public void testExpression()
    {
        final FieldExpr expr = FieldExpr.from( "name" );

        assertEquals( "name", expr.getFieldPath() );
        assertEquals( "name", expr.toString() );
    }

    @Test
    public void equalsContract()
    {
        EqualsVerifier.forClass( FieldExpr.class ).withNonnullFields( "indexPath" ).verify();
    }
}
