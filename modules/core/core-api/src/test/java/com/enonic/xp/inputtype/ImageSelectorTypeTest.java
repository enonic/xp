package com.enonic.xp.inputtype;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImageSelectorTypeTest
    extends BaseInputTypeTest
{
    public ImageSelectorTypeTest()
    {
        super( ImageSelectorType.INSTANCE );
    }

    @Test
    void testName()
    {
        assertEquals( "ImageSelector", this.type.getName().toString() );
    }

    @Test
    void testToString()
    {
        assertEquals( "ImageSelector", this.type.toString() );
    }

    @Test
    void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( ValueFactory.newString( "name" ), config );

        assertNotNull( value );
        assertSame( ValueTypes.REFERENCE, value.getType() );
    }

    @Test
    void testValidate()
    {
        final InputTypeConfig config = newEmptyConfig();
        this.type.validate( referenceProperty( "value" ), config );
    }

    @Test
    void testValidate_invalidType()
    {
        final InputTypeConfig config = newEmptyConfig();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), config ));
    }

    private InputTypeConfig newEmptyConfig()
    {
        return InputTypeConfig.create().build();
    }
}
