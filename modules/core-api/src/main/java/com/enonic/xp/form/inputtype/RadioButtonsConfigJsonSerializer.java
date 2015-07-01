package com.enonic.xp.form.inputtype;


import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.annotations.Beta;


@Beta
public class RadioButtonsConfigJsonSerializer
    extends AbstractInputTypeConfigJsonSerializer<RadioButtonsConfig>
{
    public static final RadioButtonsConfigJsonSerializer DEFAULT = new RadioButtonsConfigJsonSerializer();

    @Override
    public JsonNode serializeConfig( final RadioButtonsConfig radioButtonsConfig, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonConfig = objectMapper.createObjectNode();

        final ArrayNode jsonArray = jsonConfig.putArray( "options" );
        for ( Option option : radioButtonsConfig.getOptions() )
        {
            final ObjectNode jsonOption = jsonArray.addObject();
            jsonOption.put( "label", option.getLabel() );
            jsonOption.put( "value", option.getValue() );
        }
        return jsonConfig;
    }

    @Override
    public RadioButtonsConfig parseConfig( final JsonNode inputTypeConfigNode )
    {
        final RadioButtonsConfig.Builder builder = RadioButtonsConfig.create();

        final JsonNode optionsNode = inputTypeConfigNode.get( "options" );
        final Iterator<JsonNode> optionIterator = optionsNode.elements();
        while ( optionIterator.hasNext() )
        {
            JsonNode option = optionIterator.next();
            builder.addOption( getStringValue( "label", option ), getStringValue( "value", option ) );
        }
        return builder.build();
    }

    private static String getStringValue( String fieldName, JsonNode node )
    {
        JsonNode subNode = node.get( fieldName );
        if ( subNode == null )
        {
            throw new IllegalArgumentException( "Field [" + fieldName + "]  does not exist in: " + node.toString() );
        }
        return subNode.textValue();
    }
}
