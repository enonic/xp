package com.enonic.wem.api.content.type.formitem.inputtype;


import java.io.IOException;
import java.util.Iterator;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;


public class SingleSelectorConfigSerializerJson
    extends AbstractInputTypeConfigSerializerJson
{
    public static final SingleSelectorConfigSerializerJson DEFAULT = new SingleSelectorConfigSerializerJson();

    public void generateConfig( InputTypeConfig config, JsonGenerator g )
        throws IOException
    {
        SingleSelectorConfig singleSelectorConfig = (SingleSelectorConfig) config;
        g.writeStartObject();
        g.writeStringField( "selectorType", singleSelectorConfig.getType().toString() );
        g.writeArrayFieldStart( "options" );
        for ( SingleSelectorConfig.Option option : singleSelectorConfig.getOptions() )
        {
            g.writeStartObject();
            g.writeStringField( "label", option.getLabel() );
            g.writeStringField( "value", option.getValue() );
            g.writeEndObject();
        }
        g.writeEndArray();
        g.writeEndObject();
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
