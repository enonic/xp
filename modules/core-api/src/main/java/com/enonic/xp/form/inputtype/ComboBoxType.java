package com.enonic.xp.form.inputtype;

import java.util.Map;

import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InvalidValueException;
import com.enonic.xp.xml.DomHelper;

final class ComboBoxType
    extends InputType
{
    public ComboBoxType()
    {
        super( InputTypeName.COMBOBOX );
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
    public InputTypeConfig parseConfig( final ApplicationKey app, final Element elem )
    {
        final InputTypeConfig.Builder builder = InputTypeConfig.create();
        final Element optionsEl = DomHelper.getChildElementByTagName( elem, "options" );

        for ( final Element optionEl : DomHelper.getChildElementsByTagName( optionsEl, "option" ) )
        {
            final String label = DomHelper.getChildElementValueByTagName( optionEl, "label" );
            final String value = DomHelper.getChildElementValueByTagName( optionEl, "value" );
            builder.property( "option." + value, label );
        }

        return builder.build();
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
