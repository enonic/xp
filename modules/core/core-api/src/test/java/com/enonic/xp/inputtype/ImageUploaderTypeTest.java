package com.enonic.xp.inputtype;

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
        assertEquals( "ImageUploader", this.type.getName().toString().toString() );
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
        final Value value = this.type.createValue( "test", config );

        assertNotNull( value );
        assertSame( ValueTypes.PROPERTY_SET, value.getType() );
    }

    @Test
    public void testValidate()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( stringProperty( "test" ), config );
    }
}
