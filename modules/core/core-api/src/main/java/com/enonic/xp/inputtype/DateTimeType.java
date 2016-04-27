package com.enonic.xp.inputtype;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

@Beta
final class DateTimeType
    extends InputTypeBase
{
    public final static DateTimeType INSTANCE = new DateTimeType();

    private DateTimeType()
    {
        super( InputTypeName.DATE_TIME );
    }

    private boolean useTimeZone( final InputTypeConfig config )
    {
        return config.getValue( "timezone", boolean.class, false );
    }

    @Override
    public Value createValue( final Value value, final InputTypeConfig config )
    {
        if ( useTimeZone( config ) )
        {
            return ValueFactory.newDateTime( value.asInstant() );
        }
        else
        {
            return ValueFactory.newLocalDateTime( value.asLocalDateTime() );
        }
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.DATE_TIME, ValueTypes.LOCAL_DATE_TIME );
    }
}
