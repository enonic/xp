package com.enonic.xp.inputtype;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class MediaUploaderTypeTest
    extends BaseInputTypeTest
{
    public MediaUploaderTypeTest()
    {
        super( MediaUploaderType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "MediaUploader", this.type.getName().toString() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "MediaUploader", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( ValueFactory.newString( "test" ), config );

        assertNotNull( value );
        assertSame( ValueTypes.REFERENCE, value.getType() );
    }

    @Test
    public void testValidate()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( referenceProperty( "test" ), config );
    }
}
