package com.enonic.wem.api.content.type.component.inputtype;


import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;


public class SingleSelectorConfigSerializerJson
    extends AbstractInputTypeConfigSerializerJson
{
    public static final SingleSelectorConfigSerializerJson DEFAULT = new SingleSelectorConfigSerializerJson();

    @Override
    public JsonNode generateConfig( final InputTypeConfig config, final ObjectMapper objectMapper )
    {
        final SingleSelectorConfig singleSelectorConfig = (SingleSelectorConfig) config;
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
    public InputTypeConfig parseConfig( final JsonNode inputTypeConfigNode )
    {
        final SingleSelectorConfig.Builder builder = SingleSelectorConfig.newSingleSelectorConfig();
        final SingleSelectorConfig.SelectorType selectorType =
            SingleSelectorConfig.SelectorType.valueOf( getStringValue( "selectorType", inputTypeConfigNode ) );
        builder.type( selectorType );
        final JsonNode optionsNode = inputTypeConfigNode.get( "options" );
        final Iterator<JsonNode> optionIterator = optionsNode.getElements();
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
        return subNode.getTextValue();
    }
}
