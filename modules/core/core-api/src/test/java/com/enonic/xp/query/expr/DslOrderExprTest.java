package com.enonic.xp.query.expr;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DslOrderExprTest
{
    @Test
    public void testToString()
    {
        final PropertyTree expression1 = new PropertyTree();
        expression1.addString( "field", "myField" );

        final PropertyTree expression2 = new PropertyTree();
        expression2.addString( "field", "anotherField" );
        expression2.addString( "direction", "DESC" );

        assertEquals( "myField", DslOrderExpr.from( expression1 ).toString() );
        assertEquals( "anotherField DESC", DslOrderExpr.from( expression2 ).toString() );

    }

    @Test
    public void equalsContract()
    {
        final PropertySet expression1 = new PropertyTree().newSet();
        expression1.addString( "field", "myField" );

        final PropertySet expression2 = new PropertyTree().newSet();
        expression2.addString( "field", "anotherField" );

        EqualsVerifier.forClass( DslOrderExpr.class )
            .withPrefabValues( PropertySet.class, expression1, expression2 )
            .usingGetClass()
            .verify();

    }
}
