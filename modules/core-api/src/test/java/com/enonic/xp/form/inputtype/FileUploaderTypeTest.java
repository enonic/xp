package com.enonic.xp.form.inputtype;

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
        final Value value = this.type.createPropertyValue( "test", config );

        assertNotNull( value );
        assertSame( ValueTypes.REFERENCE, value.getType() );
    }

    @Test
    public void testContract()
    {
        this.type.checkBreaksRequiredContract( referenceProperty( "test" ) );
    }

    @Test
    public void testCheckValidity()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.checkValidity( config, referenceProperty( "test" ) );
    }
}
