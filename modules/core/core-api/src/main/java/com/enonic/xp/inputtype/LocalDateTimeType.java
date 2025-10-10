package com.enonic.xp.inputtype;

import java.time.ZonedDateTime;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypeException;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;

@PublicApi
public class LocalDateTimeType
    extends InputTypeBase
{
    public static final LocalDateTimeType INSTANCE = new LocalDateTimeType();

    protected LocalDateTimeType()
    {
        super( InputTypeName.LOCAL_DATE_TIME );
    }

    @Override
    public Value createValue( final Value value, final InputTypeConfig config )
    {
        return ValueFactory.newLocalDateTime( value.asLocalDateTime() );
    }

    @Override
    public Value createDefaultValue( final Input input )
    {
        final String defaultValue = input.getDefaultValue().getRootValue();

        if ( defaultValue != null )
        {
            final Value value = parseLocalDateTime( defaultValue );

            if ( value != null )
            {
                return value;
            }

            final ZonedDateTime zonedDateTime = DateTimeHelper.resolveRelativeTime( defaultValue );
            return ValueFactory.newLocalDateTime( zonedDateTime.toLocalDateTime() );
        }

        return super.createDefaultValue( input );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.LOCAL_DATE_TIME );
    }

    private Value parseLocalDateTime( final String value )
    {
        try
        {
            return ValueFactory.newLocalDateTime( ValueTypes.LOCAL_DATE_TIME.convert( value ) );
        }
        catch ( ValueTypeException e )
        {
            return null;
        }
    }
}
