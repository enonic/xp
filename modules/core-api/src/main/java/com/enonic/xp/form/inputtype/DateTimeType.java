package com.enonic.xp.form.inputtype;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InvalidTypeException;

@Beta
final class DateTimeType
    extends InputType
{
    public final static DateTimeType INSTANCE = new DateTimeType();

    private DateTimeType()
    {
        super( InputTypeName.DATE_TIME );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
        validateNotBlank( property );
    }

    private boolean useTimeZone( final InputTypeConfig config )
    {
        return config.getValue( "timezone", boolean.class, false );
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        if ( useTimeZone( config ) )
        {
            return Value.newInstant( ValueTypes.DATE_TIME.convert( value ) );
        }
        else
        {
            return Value.newLocalDateTime( ValueTypes.LOCAL_DATE_TIME.convert( value ) );
        }
    }

    @Override
    public void checkValidity( final InputTypeConfig config, final Property property )
    {
        if ( ( ValueTypes.DATE_TIME != property.getType() ) && ( ValueTypes.LOCAL_DATE_TIME != property.getType() ) )
        {
            throw new InvalidTypeException( property, ValueTypes.DATE_TIME );
        }
    }
}
