package com.enonic.xp.inputtype;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ImageUploaderTypeTest
    extends BaseInputTypeTest
{
    public ImageUploaderTypeTest()
    {
        super( ImageUploaderType.INSTANCE );
    }

    @Test
    void testName()
    {
        assertEquals( "ImageUploader", this.type.getName().toString() );
    }

    @Test
    void testToString()
    {
        assertEquals( "ImageUploader", this.type.toString() );
    }

    @Test
    void testCreateProperty()
    {
        final PropertyTree tree = new PropertyTree();
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( ValueFactory.newPropertySet( tree.newSet() ), config );

        assertNotNull( value );
        assertSame( ValueTypes.PROPERTY_SET, value.getType() );
    }

    @Test
    void testValidate()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( stringProperty( "test" ), config );
    }
}
