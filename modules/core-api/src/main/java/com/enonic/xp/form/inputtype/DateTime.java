package com.enonic.xp.form.inputtype;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InvalidTypeException;

@Beta
final class DateTime
    extends InputType
{
    public DateTime()
    {
        super( "DateTime", DateTimeConfig.class, false );
    }

    @Override
    public InputTypeConfigSerializer getConfigSerializer()
    {
        return DateTimeConfigSerializer.INSTANCE;
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
    public InputTypeConfig getDefaultConfig()
    {
        return DateTimeConfig.create().
            withTimezone( false ).
            build();
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        if ( config != null && !( config instanceof DateTimeConfig ) )
        {
            throw new IllegalArgumentException(
                "Expected config of type " + DateTimeConfig.class.getName() + ", got " + config.getClass() );
        }

        final DateTimeConfig dateTimeConfig = config == null ? (DateTimeConfig) getDefaultConfig() : (DateTimeConfig) config;
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