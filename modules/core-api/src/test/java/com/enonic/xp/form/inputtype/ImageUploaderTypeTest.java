package com.enonic.xp.form.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

import static org.junit.Assert.*;

public class ImageUploaderTypeTest
    extends BaseInputTypeTest
{
    public ImageUploaderTypeTest()
    {
        super( ImageUploaderType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "ImageUploader", this.type.getName() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "ImageUploader", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createPropertyValue( "test", config );

        assertNotNull( value );
        assertSame( ValueTypes.PROPERTY_SET, value.getType() );
    }

    @Test
    public void testContract()
    {
        this.type.checkBreaksRequiredContract( stringProperty( "test" ) );
    }

    @Test
    public void testCheckValidity()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.checkValidity( config, stringProperty( "test" ) );
    }

    @Test
    public void testCheckValidity_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.checkValidity( config, booleanProperty( true ) );
    }
}
