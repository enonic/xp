package com.enonic.wem.api.schema.content.form.inputtype;


import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class ComboBoxConfigJsonSerializer
    extends AbstractInputTypeConfigJsonSerializer<ComboBoxConfig>
{
    public static final ComboBoxConfigJsonSerializer DEFAULT = new ComboBoxConfigJsonSerializer();

    @Override
    public JsonNode serializeConfig( final ComboBoxConfig config, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonConfig = objectMapper.createObjectNode();

        final ArrayNode jsonArray = jsonConfig.putArray( "options" );
        for ( ComboBoxConfig.Option option : config.getOptions() )
        {
            final ObjectNode jsonOption = jsonArray.addObject();
            jsonOption.put( "label", option.getLabel() );
            jsonOption.put( "value", option.getValue() );
        }
        return jsonConfig;
    }

    @Override
    public ComboBoxConfig parseConfig( final JsonNode inputTypeConfigNode )
    {
        final ComboBoxConfig.Builder builder = ComboBoxConfig.newComboBoxConfig();
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
