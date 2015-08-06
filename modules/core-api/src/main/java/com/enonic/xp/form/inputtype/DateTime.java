package com.enonic.xp.form.inputtype;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InvalidTypeException;

@Beta
final class DateTime
    extends ConfigurableInputType<DateTimeConfig>
{
    public DateTime()
    {
        super( InputTypeName.DATE_TIME, new DateTimeConfigSerializer() );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
        validateNotBlank( property );
    }

    @Override
    public void checkTypeValidity( final Property property )
    {
        if ( ( ValueTypes.DATE_TIME != property.getType() ) && ( ValueTypes.LOCAL_DATE_TIME != property.getType() ) )
        {
            throw new InvalidTypeException( property, ValueTypes.DATE_TIME );
        }
    }

    @Override
    public DateTimeConfig getDefaultConfig()
    {
        final DateTimeConfig.Builder builder = DateTimeConfig.create();
        builder.withTimezone( false );
        return builder.build();
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        if ( config != null && !( config instanceof DateTimeConfig ) )
        {
            throw new IllegalArgumentException(
                "Expected config of type " + DateTimeConfig.class.getName() + ", got " + config.getClass() );
        }

        final DateTimeConfig dateTimeConfig = config == null ? getDefaultConfig() : (DateTimeConfig) config;
        if ( dateTimeConfig.isWithTimezone() )
        {
            return Value.newInstant( ValueTypes.DATE_TIME.convert( value ) );
        }
        else
        {
            return Value.newLocalDateTime( ValueTypes.LOCAL_DATE_TIME.convert( value ) );
        }
    }
}
