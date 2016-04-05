package com.enonic.xp.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

import static org.junit.Assert.*;

public class HtmlAreaTypeTest
    extends BaseInputTypeTest
{
    public HtmlAreaTypeTest()
    {
        super( HtmlAreaType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "HtmlArea", this.type.getName().toString() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "HtmlArea", this.type.toString() );
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
    public void testCreateDefaultValue()
    {
        final InputTypeDefault config = InputTypeDefault.create().
            property( InputTypeProperty.create( "default", "<p>test</p>" ).
                build() ).
            build();

        final Value value = this.type.createDefaultValue( config );

        assertNotNull( value );
        assertEquals( "<p>test</p>", value.toString() );

    }

    @Test
    public void testValidate()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( stringProperty( "test" ), config );
    }

    @Test(expected = InputTypeValidationException.class)
    public void testValidate_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( booleanProperty( true ), config );
    }
}
