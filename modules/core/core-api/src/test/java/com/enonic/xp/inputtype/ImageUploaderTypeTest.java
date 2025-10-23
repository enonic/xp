package com.enonic.xp.inputtype;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

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
        assertEquals( "ImageUploader", this.type.getName().toString() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "ImageUploader", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final PropertyTree tree = new PropertyTree();
        final Value value = this.type.createValue( ValueFactory.newPropertySet( tree.newSet() ), GenericValue.object().build() );

        assertNotNull( value );
        assertSame( ValueTypes.PROPERTY_SET, value.getType() );
    }

    @Test
    public void testValidate()
    {
        this.type.validate( stringProperty( "test" ), GenericValue.object().build() );
    }
}
