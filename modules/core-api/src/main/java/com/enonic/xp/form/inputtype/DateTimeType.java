package com.enonic.xp.form.inputtype;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

    @Override
    public void checkTypeValidity( final Property property )
    {
        if ( ( ValueTypes.DATE_TIME != property.getType() ) && ( ValueTypes.LOCAL_DATE_TIME != property.getType() ) )
        {
            throw new InvalidTypeException( property, ValueTypes.DATE_TIME );
        }
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
    public ObjectNode serializeConfig( final InputTypeConfig config )
    {
        final ObjectNode jsonConfig = JsonNodeFactory.instance.objectNode();
        jsonConfig.put( "withTimezone", config.getValue( "timezone", boolean.class, false ) );
        return jsonConfig;
    }
}
