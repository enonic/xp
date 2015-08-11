package com.enonic.xp.form.inputtype;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InvalidTypeException;

import static org.junit.Assert.*;

public class DoubleTypeTest
    extends BaseInputTypeTest
{
    public DoubleTypeTest()
    {
        super( DoubleType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "Double", this.type.getName() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "Double", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createPropertyValue( "1.3", config );

        assertNotNull( value );
        assertSame( ValueTypes.DOUBLE, value.getType() );
    }

    @Test
    public void testCheckTypeValidity()
    {
        this.type.checkTypeValidity( doubleProperty( 1.3 ) );
    }

    @Test(expected = InvalidTypeException.class)
    public void testCheckTypeValidity_invalid()
    {
        this.type.checkTypeValidity( stringProperty( "value" ) );
    }

    @Test
    public void testContract()
    {
        this.type.checkBreaksRequiredContract( doubleProperty( 1.3 ) );
    }

    @Test
    public void testCheckValidity()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.checkValidity( config, doubleProperty( 1.3 ) );
    }

    @Test
    public void testSerializeConfig()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final ObjectNode json = this.type.serializeConfig( config );
        assertNull( json );
    }
}
