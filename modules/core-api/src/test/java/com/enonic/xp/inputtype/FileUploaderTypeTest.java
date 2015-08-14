package com.enonic.xp.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

import static org.junit.Assert.*;

public class FileUploaderTypeTest
    extends BaseInputTypeTest
{
    public FileUploaderTypeTest()
    {
        super( FileUploaderType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "FileUploader", this.type.getName() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "FileUploader", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( "test", config );

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
