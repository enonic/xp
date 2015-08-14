package com.enonic.xp.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

import static org.junit.Assert.*;

public class ContentSelectorTypeTest
    extends BaseInputTypeTest
{
    public ContentSelectorTypeTest()
    {
        super( ContentSelectorType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "ContentSelector", this.type.getName() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "ContentSelector", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( "name", config );

        assertNotNull( value );
        assertSame( ValueTypes.REFERENCE, value.getType() );
    }

    @Test
    public void testValidate()
    {
        final InputTypeConfig config = newEmptyConfig();
        this.type.validate( referenceProperty( "name" ), config );
    }

    @Test(expected = InputTypeValidationException.class)
    public void testValidate_invalidType()
    {
        final InputTypeConfig config = newEmptyConfig();
        this.type.validate( booleanProperty( true ), config );
    }

    private InputTypeConfig newEmptyConfig()
    {
        return InputTypeConfig.create().build();
    }
}
