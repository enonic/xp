package com.enonic.xp.inputtype;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HtmlAreaTypeTest
    extends BaseInputTypeTest
{
    public HtmlAreaTypeTest()
    {
        super( HtmlAreaType.INSTANCE );
    }

    @Test
    void testName()
    {
        assertEquals( "HtmlArea", this.type.getName().toString() );
    }

    @Test
    void testToString()
    {
        assertEquals( "HtmlArea", this.type.toString() );
    }

    @Test
    void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( ValueFactory.newString( "test"), config );

        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );
    }

    @Test
    void testCreateDefaultValue()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.HTML_AREA, "<p>test</p>" ).build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertEquals( "<p>test</p>", value.toString() );

    }

    @Test
    void testValidate()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( stringProperty( "test" ), config );
    }

    @Test
    void testValidate_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), config ));
    }
}
