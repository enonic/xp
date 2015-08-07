package com.enonic.xp.form.inputtype;

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

final class RadioButtons
    extends InputType
{
    public RadioButtons()
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
        if ( !( config instanceof RadioButtonsConfig ) )
        {
            return;
        }

        final RadioButtonsConfig typedConfig = (RadioButtonsConfig) config;
        final String valueAsString = property.getString();
        if ( valueAsString != null && !typedConfig.containsKey( valueAsString ) )
        {
            throw new InvalidValueException( property, "Value can only be of one the following strings: " +
                typedConfig.optionValuesAsCommaSeparatedString() );
        }
    }

    @Override
    public InputTypeConfig parseConfig( final ApplicationKey app, final Element elem )
    {
        final RadioButtonsConfig.Builder builder = RadioButtonsConfig.create();

        final Element optionsEl = DomHelper.getChildElementByTagName( elem, "options" );
        for ( final Element optionEl : DomHelper.getChildElementsByTagName( optionsEl, "option" ) )
        {
            final String label = DomHelper.getChildElementValueByTagName( optionEl, "label" );
            final String value = DomHelper.getChildElementValueByTagName( optionEl, "value" );
            builder.addOption( label, value );
        }

        return builder.build();
    }

    @Override
    public ObjectNode serializeConfig( final InputTypeConfig config )
    {
        if ( !( config instanceof RadioButtonsConfig ) )
        {
            return null;
        }

        final RadioButtonsConfig typedConfig = (RadioButtonsConfig) config;
        final ObjectNode jsonConfig = JsonNodeFactory.instance.objectNode();

        final ArrayNode jsonArray = jsonConfig.putArray( "options" );
        for ( Option option : typedConfig.getOptions() )
        {
            final ObjectNode jsonOption = jsonArray.addObject();
            jsonOption.put( "label", option.getLabel() );
            jsonOption.put( "value", option.getValue() );
        }

        return jsonConfig;
    }
}
