package com.enonic.xp.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

import static org.junit.Assert.*;

public class VectorUploaderTypeTest
    extends BaseInputTypeTest
{
    public VectorUploaderTypeTest()
    {
        super( VectorUploaderType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "VectorUploader", this.type.getName().toString() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "VectorUploader", this.type.toString() );
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
