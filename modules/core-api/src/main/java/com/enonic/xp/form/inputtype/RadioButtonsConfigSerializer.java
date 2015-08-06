package com.enonic.xp.form.inputtype;

import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.xml.DomHelper;

final class RadioButtonsConfigSerializer
    implements InputTypeConfigSerializer<RadioButtonsConfig>
{
    @Override
    public RadioButtonsConfig parseConfig( final ApplicationKey currentApplication, final Element elem )
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
    public ObjectNode serializeConfig( final RadioButtonsConfig radioButtonsConfig )
    {
        final ObjectNode jsonConfig = JsonNodeFactory.instance.objectNode();

        final ArrayNode jsonArray = jsonConfig.putArray( "options" );
        for ( Option option : radioButtonsConfig.getOptions() )
        {
            final ObjectNode jsonOption = jsonArray.addObject();
            jsonOption.put( "label", option.getLabel() );
            jsonOption.put( "value", option.getValue() );
        }

        return jsonConfig;
    }
}
