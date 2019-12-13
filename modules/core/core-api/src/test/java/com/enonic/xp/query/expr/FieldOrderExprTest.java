package com.enonic.xp.query.expr;

import org.junit.jupiter.api.Test;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.support.AbstractEqualsTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class FieldOrderExprTest
{
    @Test
    public void testExpression()
    {
        final FieldExpr field = FieldExpr.from( "name" );
        final FieldOrderExpr expr = new FieldOrderExpr( field, OrderExpr.Direction.DESC );

        assertSame( field, expr.getField() );
        assertEquals( OrderExpr.Direction.DESC, expr.getDirection() );
        assertEquals( "name DESC", expr.toString() );
    }

    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                final FieldExpr field = FieldExpr.from( "name" );
                final FieldOrderExpr expr = new FieldOrderExpr( field, OrderExpr.Direction.DESC );

                return expr;
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                final FieldExpr field1 = FieldExpr.from( "name" );
                final FieldOrderExpr expr1 = new FieldOrderExpr( field1, OrderExpr.Direction.ASC );

                final FieldExpr field2 = FieldExpr.from( "value" );
                final FieldOrderExpr expr2 = new FieldOrderExpr( field2, OrderExpr.Direction.DESC );

                return new Object[]{expr1, expr2, new Object()};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                final FieldExpr field = FieldExpr.from( "name" );
                final FieldOrderExpr expr = new FieldOrderExpr( field, OrderExpr.Direction.DESC );
                return expr;
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                final FieldOrderExpr expr = FieldOrderExpr.create( IndexPath.from( "name" ), OrderExpr.Direction.DESC );
                return expr;
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }
}
