package com.enonic.wem.core.content.type.formitem.comptype;


import java.io.IOException;
import java.util.Iterator;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.core.content.JsonParserUtil;

public class DropdownConfigSerializerJson
    extends AbstractComponentTypeConfigSerializerJson
{
    public static final DropdownConfigSerializerJson DEFAULT = new DropdownConfigSerializerJson();

    public void generateConfig( ComponentTypeConfig config, JsonGenerator g )
        throws IOException
    {
        DropdownConfig dropdownConfig = (DropdownConfig) config;
        g.writeStartObject();
        g.writeArrayFieldStart( "options" );
        for ( DropdownConfig.Option option : dropdownConfig.getOptions() )
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
    public ComponentTypeConfig parseConfig( final JsonNode componentTypeConfigNode )
    {
        final DropdownConfig.Builder builder = DropdownConfig.newBuilder();
        final JsonNode optionsNode = componentTypeConfigNode.get( "options" );
        final Iterator<JsonNode> optionIterator = optionsNode.getElements();
        while ( optionIterator.hasNext() )
        {
            JsonNode option = optionIterator.next();
            builder.addOption( JsonParserUtil.getStringValue( "label", option ), JsonParserUtil.getStringValue( "value", option ) );
        }
        return builder.build();
    }
}
