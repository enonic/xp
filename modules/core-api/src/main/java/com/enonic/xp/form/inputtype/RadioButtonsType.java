package com.enonic.xp.form.inputtype;

import java.util.Map;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InvalidValueException;

final class RadioButtonsType
    extends InputType
{
    public final static RadioButtonsType INSTANCE = new RadioButtonsType();

    private RadioButtonsType()
    {
        super( InputTypeName.RADIO_BUTTONS );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
        validateNotBlank( property );
    }

    @Override
    public void checkTypeValidity( final Property property )
    {
        validateType( property, ValueTypes.STRING );
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newString( value );
    }

    @Override
    public void checkValidity( final InputTypeConfig config, final Property property )
    {
        final String valueAsString = property.getString();
        if ( valueAsString != null && !config.hasProperty( "option." + valueAsString ) )
        {
            throw new InvalidValueException( property, "Value is not a valid option" );
        }
    }

    @Override
    public ObjectNode serializeConfig( final InputTypeConfig config )
    {
        final ObjectNode jsonConfig = JsonNodeFactory.instance.objectNode();

        final ArrayNode jsonArray = jsonConfig.putArray( "options" );
        final Map<String, String> subConfig = config.toSubMap( "option." );

        for ( final String key : subConfig.keySet() )
        {
            final ObjectNode jsonOption = jsonArray.addObject();
            jsonOption.put( "value", key );
            jsonOption.put( "label", subConfig.get( key ) );
        }

        return jsonConfig;
    }
}
