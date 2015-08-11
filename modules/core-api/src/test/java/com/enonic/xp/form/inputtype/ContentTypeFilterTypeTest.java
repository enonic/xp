package com.enonic.xp.form.inputtype;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InputValidationException;
import com.enonic.xp.form.InvalidTypeException;

import static org.junit.Assert.*;

public class ContentTypeFilterTypeTest
    extends BaseInputTypeTest
{
    public ContentTypeFilterTypeTest()
    {
        super( ContentTypeFilterType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "ContentTypeFilter", this.type.getName() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "ContentTypeFilter", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createPropertyValue( "name", config );

        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );
    }

    @Test
    public void testCheckTypeValidity()
    {
        this.type.checkTypeValidity( stringProperty( "value" ) );
    }

    @Test(expected = InvalidTypeException.class)
    public void testCheckTypeValidity_invalid()
    {
        this.type.checkTypeValidity( booleanProperty( true ) );
    }

    @Test
    public void testContract()
    {
        this.type.checkBreaksRequiredContract( stringProperty( "name" ) );
    }

    @Test(expected = InputValidationException.class)
    public void testContract_invalid()
    {
        this.type.checkBreaksRequiredContract( stringProperty( "" ) );
    }

    @Test
    public void testCheckValidity()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.checkValidity( config, stringProperty( "name" ) );
    }

    @Test
    public void testSerializeConfig()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final ObjectNode json = this.type.serializeConfig( config );
        assertNull( json );
    }
}
