package com.enonic.xp.form.inputtype;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InputValidationException;
import com.enonic.xp.form.InvalidTypeException;

import static org.junit.Assert.*;

public class ContentSelectorTypeTest
    extends BaseInputTypeTest
{
    public ContentSelectorTypeTest()
    {
        super( ContentSelectorType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "ContentSelector", this.type.getName() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "ContentSelector", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createPropertyValue( "name", config );

        assertNotNull( value );
        assertSame( ValueTypes.REFERENCE, value.getType() );
    }

    @Test
    public void testCheckTypeValidity()
    {
        this.type.checkTypeValidity( referenceProperty( "value" ) );
    }

    @Test(expected = InvalidTypeException.class)
    public void testCheckTypeValidity_invalid()
    {
        this.type.checkTypeValidity( booleanProperty( true ) );
    }

    @Test
    public void testContract()
    {
        this.type.checkBreaksRequiredContract( referenceProperty( "name" ) );
    }

    @Test(expected = InputValidationException.class)
    public void testContract_invalid()
    {
        this.type.checkBreaksRequiredContract( stringProperty( "" ) );
    }

    @Test
    public void testCheckValidity()
    {
        final InputTypeConfig config = newEmptyConfig();
        this.type.checkValidity( config, stringProperty( "name" ) );
    }

    @Test
    public void testSerializeConfig_empty()
    {
        final InputTypeConfig config = newEmptyConfig();
        final ObjectNode json = this.type.serializeConfig( config );

        assertNotNull( json );
        this.jsonHelper.assertJsonEquals( this.jsonHelper.loadTestJson( "empty.json" ), json );
    }

    @Test
    public void testSerializeConfig_full()
    {
        final InputTypeConfig config = newFullConfig();
        final ObjectNode json = this.type.serializeConfig( config );

        assertNotNull( json );
        this.jsonHelper.assertJsonEquals( this.jsonHelper.loadTestJson( "full.json" ), json );
    }

    private InputTypeConfig newEmptyConfig()
    {
        return InputTypeConfig.create().build();
    }

    private InputTypeConfig newFullConfig()
    {
        return InputTypeConfig.create().
            property( "relationshipType", "system:parent" ).
            property( "allowedContentTypes", "media:video,media:image" ).
            build();
    }
}
