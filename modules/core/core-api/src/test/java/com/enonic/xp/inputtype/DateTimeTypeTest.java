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

class DateTimeTypeTest
    extends BaseInputTypeTest
{
    public DateTimeTypeTest()
    {
        super( DateTimeType.INSTANCE );
    }

    @Test
    void testName()
    {
        assertEquals( "DateTime", this.type.getName().toString() );
    }

    @Test
    void testToString()
    {
        assertEquals( "DateTime", this.type.toString() );
    }

    @Test
    void testCreateProperty()
    {
        final InputTypeConfig config = newEmptyConfig();
        final Value value = this.type.createValue( ValueFactory.newString( "2015-01-02T22:11:00" ), config );

        assertNotNull( value );
        assertSame( ValueTypes.LOCAL_DATE_TIME, value.getType() );
    }

    @Test
    void testCreateProperty_withTimezone()
    {
        final InputTypeConfig config = newFullConfig();
        final Value value = this.type.createValue( ValueFactory.newString( "2015-01-02T22:11:00Z" ), config );

        assertNotNull( value );
        assertSame( ValueTypes.DATE_TIME, value.getType() );
    }

    @Test
    void testCreateDefaultValue()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.DATE_TIME, "2014-08-16T05:03:45" ).
            inputTypeConfig( InputTypeConfig.create().
                property( InputTypeProperty.create( "timezone", "false" ).
                    build() ).
                build() ).
            build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertSame( ValueTypes.LOCAL_DATE_TIME, value.getType() );
        assertEquals( value.toString(), "2014-08-16T05:03:45" );

    }

    @Test
    void testCreateDefaultValue_withTimezone_format1()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.DATE_TIME, "2014-08-16T10:03:45Z" ).
            inputTypeConfig( InputTypeConfig.create().
                property( InputTypeProperty.create( "timezone", "true" ).
                    build() ).
                build() ).
            build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertSame( ValueTypes.DATE_TIME, value.getType() );
        assertEquals( value.toString(), "2014-08-16T10:03:45Z" );

    }

    @Test
    void testCreateDefaultValue_withTimezone_format2_plus()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.DATE_TIME, "2014-08-16T10:03:45+03:00" ).
            inputTypeConfig( InputTypeConfig.create().
                property( InputTypeProperty.create( "timezone", "true" ).
                    build() ).
                build() ).
            build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertSame( ValueTypes.DATE_TIME, value.getType() );
        assertEquals( value.toString(), "2014-08-16T07:03:45Z" );

    }

    @Test
    void testCreateDefaultValue_withTimezone_format2_minus()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.DATE_TIME, "2014-08-16T10:03:45-03:00" ).
            inputTypeConfig( InputTypeConfig.create().
                property( InputTypeProperty.create( "timezone", "true" ).
                    build() ).
                build() ).
            build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertSame( ValueTypes.DATE_TIME, value.getType() );
        assertEquals( value.toString(), "2014-08-16T13:03:45Z" );
    }

    @Test
    void testCreateDefaultValue_withTimezone_format2_day_change()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.DATE_TIME, "2014-08-16T22:03:45-03:00" ).
            inputTypeConfig( InputTypeConfig.create().
                property( InputTypeProperty.create( "timezone", "true" ).
                    build() ).
                build() ).
            build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertSame( ValueTypes.DATE_TIME, value.getType() );
        assertEquals( value.toString(), "2014-08-17T01:03:45Z" );
    }


    @Test
    void testCreateDefaultValue_invalid()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.DATE_TIME, "2014-18-16T05:03:45" ).
            inputTypeConfig( InputTypeConfig.create().
                property( InputTypeProperty.create( "timezone", "true" ).
                    build() ).
                build() ).
            build();

        assertThrows(IllegalArgumentException.class, () ->  this.type.createDefaultValue( input ));
    }

    @Test
    void testRelativeDefaultValue_only_relative_date_exists()
    {

        final Input input = getDefaultInputBuilder( InputTypeName.DATE_TIME, "+1year -5months -36d" ).build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertSame( ValueTypes.LOCAL_DATE_TIME, value.getType() );
    }

    @Test
    void testRelativeDefaultValue_only_relative_time_exists()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.DATE_TIME, "+1hour -5minutes -36s" ).
            inputTypeConfig( InputTypeConfig.create().
                property( InputTypeProperty.create( "timezone", "false" ).
                    build() ).
                build() ).
            build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertSame( ValueTypes.LOCAL_DATE_TIME, value.getType() );
    }

    @Test
    void testRelativeDefaultValue_date_time()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.DATE_TIME, "+1year -5months -36d +2minutes -1h" ).
            inputTypeConfig( InputTypeConfig.create().
                property( InputTypeProperty.create( "timezone", "true" ).
                    build() ).
                build() ).
            build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertSame( ValueTypes.DATE_TIME, value.getType() );
    }

    @Test
    void testRelativeDefaultValue_date_time_invalid()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.DATE_TIME, "+1year -5months -36d +2minutes -1haaur" ).build();

        assertThrows(IllegalArgumentException.class, () -> this.type.createDefaultValue( input ) );
    }

    @Test
    void testValidate_dateTime()
    {
        final InputTypeConfig config = newEmptyConfig();
        this.type.validate( dateTimeProperty(), config );
    }

    @Test
    void testValidate_localDateTime()
    {
        final InputTypeConfig config = newEmptyConfig();
        this.type.validate( localDateTimeProperty(), config );
    }

    @Test
    void testValidate_invalidType()
    {
        final InputTypeConfig config = newEmptyConfig();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), config ));
    }

    private InputTypeConfig newEmptyConfig()
    {
        return InputTypeConfig.create().build();
    }

    private InputTypeConfig newFullConfig()
    {
        return InputTypeConfig.create().
            property( InputTypeProperty.create( "timezone", "true" ).build() ).
            build();
    }
}
