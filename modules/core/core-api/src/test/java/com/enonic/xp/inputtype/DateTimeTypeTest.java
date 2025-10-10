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

public class DateTimeTypeTest
    extends BaseInputTypeTest
{
    public DateTimeTypeTest()
    {
        super( DateTimeType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "DateTime", this.type.getName().toString() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "DateTime", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = newEmptyConfig();
        final Value value = this.type.createValue( ValueFactory.newString( "2015-01-02T22:11:00Z" ), config );

        assertNotNull( value );
        assertSame( ValueTypes.DATE_TIME, value.getType() );
    }

    @Test
    public void testCreateDefaultValue_format1()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.DATE_TIME, "2014-08-16T10:03:45Z" ).build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertSame( ValueTypes.DATE_TIME, value.getType() );
        assertEquals( "2014-08-16T10:03:45Z", value.toString() );
    }

    @Test
    public void testCreateDefaultValue_format2_plus()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.DATE_TIME, "2014-08-16T10:03:45+03:00" ).build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertSame( ValueTypes.DATE_TIME, value.getType() );
        assertEquals( "2014-08-16T07:03:45Z", value.toString() );
    }

    @Test
    public void testCreateDefaultValue_format2_minus()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.DATE_TIME, "2014-08-16T10:03:45-03:00" ).build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertSame( ValueTypes.DATE_TIME, value.getType() );
        assertEquals( "2014-08-16T13:03:45Z", value.toString() );
    }

    @Test
    public void testCreateDefaultValue_format2_day_change()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.DATE_TIME, "2014-08-16T22:03:45-03:00" ).build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertSame( ValueTypes.DATE_TIME, value.getType() );
        assertEquals( "2014-08-17T01:03:45Z", value.toString() );
    }


    @Test
    public void testCreateDefaultValue_invalid()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.DATE_TIME, "2014-18-16T05:03:45" ).build();

        assertThrows( IllegalArgumentException.class, () -> this.type.createDefaultValue( input ) );
    }

    @Test
    public void testRelativeDefaultValue_date_time()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.DATE_TIME, "+1year -5months -36d +2minutes -1h" ).build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertSame( ValueTypes.DATE_TIME, value.getType() );
    }

    @Test
    public void testRelativeDefaultValue_date_time_invalid()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.DATE_TIME, "+1year -5months -36d +2minutes -1haaur" ).build();

        assertThrows( IllegalArgumentException.class, () -> this.type.createDefaultValue( input ) );
    }

    @Test
    public void testValidate_dateTime()
    {
        final InputTypeConfig config = newEmptyConfig();
        this.type.validate( dateTimeProperty(), config );
    }

    @Test
    public void testValidate_invalidType()
    {
        final InputTypeConfig config = newEmptyConfig();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), config ) );
    }

    private InputTypeConfig newEmptyConfig()
    {
        return InputTypeConfig.create().build();
    }
}
