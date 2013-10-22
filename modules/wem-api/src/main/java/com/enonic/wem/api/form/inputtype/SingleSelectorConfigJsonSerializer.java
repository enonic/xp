package com.enonic.wem.api.form.inputtype;


import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class SingleSelectorConfigJsonSerializer
    extends AbstractInputTypeConfigJsonSerializer<SingleSelectorConfig>
{
    public static final SingleSelectorConfigJsonSerializer DEFAULT = new SingleSelectorConfigJsonSerializer();

    @Override
    public JsonNode serializeConfig( final SingleSelectorConfig singleSelectorConfig, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonConfig = objectMapper.createObjectNode();

        jsonConfig.put( "selectorType", singleSelectorConfig.getType().toString() );
        final ArrayNode jsonArray = jsonConfig.putArray( "options" );
        for ( SingleSelectorConfig.Option option : singleSelectorConfig.getOptions() )
        {
            final ObjectNode jsonOption = jsonArray.addObject();
            jsonOption.put( "label", option.getLabel() );
            jsonOption.put( "value", option.getValue() );
        }
        return jsonConfig;
    }

    @Override
    public SingleSelectorConfig parseConfig( final JsonNode inputTypeConfigNode )
    {
        final SingleSelectorConfig.Builder builder = SingleSelectorConfig.newSingleSelectorConfig();
        final SingleSelectorConfig.SelectorType selectorType =
            SingleSelectorConfig.SelectorType.valueOf( getStringValue( "selectorType", inputTypeConfigNode ) );
        builder.type( selectorType );
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
