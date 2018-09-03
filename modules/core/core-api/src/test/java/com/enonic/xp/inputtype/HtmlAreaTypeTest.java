package com.enonic.xp.inputtype;

import org.junit.Test;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;

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
        final Value value = this.type.createValue( ValueFactory.newPropertySet( new PropertySet() ), config );

        assertNotNull( value );
        assertSame( ValueTypes.PROPERTY_SET, value.getType() );
    }

    @Test
    public void testCreateDefaultValue()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.HTML_AREA, "<p>test</p>" ).build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertEquals( "value: [<p>test</p>]", value.toString().trim() );

    }

    @Test
    public void testValidate()
    {
        final PropertySet propertySet = new PropertySet();
        propertySet.addString( "value", "<b>html content </b>" );

        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( dataProperty( propertySet ), config );
    }

    @Test(expected = InputTypeValidationException.class)
    public void testValidate_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( booleanProperty( true ), config );
    }
}
