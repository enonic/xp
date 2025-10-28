package com.enonic.xp.inputtype;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateTypeTest
    extends BaseInputTypeTest
{
    public DateTypeTest()
    {
        super( DateType.INSTANCE );
    }

    @Test
    void testName()
    {
        assertEquals( "Date", this.type.getName().toString() );
    }

    @Test
    void testToString()
    {
        assertEquals( "Date", this.type.toString() );
    }

    @Test
    void testCreateProperty()
    {
        final InputTypeConfig config = newEmptyConfig();
        final Value value = this.type.createValue( ValueFactory.newString( "2015-01-02" ), config );

        assertNotNull( value );
        assertSame( ValueTypes.LOCAL_DATE, value.getType() );
    }

    @Test
    void testCreateDefaultValue()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.DATE, "2014-08-16" ).build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertSame( ValueTypes.LOCAL_DATE, value.getType() );
        assertEquals( value.toString(), "2014-08-16" );
    }

    @Test
    void testRelativeDefaultValue()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.DATE, "+1year -5months -36d" ).build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertSame( ValueTypes.LOCAL_DATE, value.getType() );
        assertEquals( value.getObject(), LocalDate.now().plusYears( 1 ).plusMonths( -5 ).plusDays( -36 ) );
    }

    @Test
    void testCreateDefaultValue_invalid()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.DATE, "2014-18-16" ).build();

        assertThrows(IllegalArgumentException.class, () -> this.type.createDefaultValue( input ) );
    }

    @Test
    void testValidate()
    {
        final InputTypeConfig config = newEmptyConfig();
        this.type.validate( localDateProperty(), config );
    }

    @Test
    void testValidate_invalidType()
    {
        final InputTypeConfig config = newEmptyConfig();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), config ) );
    }

    private InputTypeConfig newEmptyConfig()
    {
        return InputTypeConfig.create().build();
    }
}
