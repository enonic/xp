package com.enonic.xp.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

import static org.junit.Assert.*;

public class AttachmentTypeTest
    extends BaseInputTypeTest
{
    public AttachmentTypeTest()
    {
        super( AttachmentType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "Attachment", this.type.getName().toString() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "Attachment", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( "test", config );

        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );
    }

    @Test
    public void testValidate()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( stringProperty( "test" ), config );
    }
}
