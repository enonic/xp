package com.enonic.xp.form.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InvalidTypeException;

import static org.junit.Assert.*;

public class TextLineTypeTest
    extends BaseInputTypeTest
{
    public TextLineTypeTest()
    {
        super( TextLineType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "TextLine", this.type.getName() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "TextLine", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createPropertyValue( "test", config );

        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );
    }

    @Test
    public void testCheckTypeValidity()
    {
        this.type.checkTypeValidity( stringProperty( "test" ) );
    }

    @Test(expected = InvalidTypeException.class)
    public void testCheckTypeValidity_invalid()
    {
        this.type.checkTypeValidity( booleanProperty( true ) );
    }

    @Test
    public void testContract()
    {
        this.type.checkBreaksRequiredContract( stringProperty( "test" ) );
    }

    @Test
    public void testCheckValidity()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.checkValidity( config, stringProperty( "test" ) );
    }
}
