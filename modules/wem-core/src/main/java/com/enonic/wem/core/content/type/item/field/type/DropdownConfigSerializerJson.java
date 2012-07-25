package com.enonic.wem.core.content.type.item.field.type;


import java.io.IOException;
import java.util.Iterator;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.core.content.JsonParserUtil;

public class DropdownConfigSerializerJson
    extends FieldTypeConfigSerializerJson
{
    public static final DropdownConfigSerializerJson DEFAULT = new DropdownConfigSerializerJson();

    public void generate( FieldTypeConfig config, JsonGenerator g )
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
    public FieldTypeConfig parse( final JsonNode node )
    {
        final DropdownConfig.Builder builder = DropdownConfig.newBuilder();
        final JsonNode optionsNode = node.get( "options" );
        final Iterator<JsonNode> optionIterator = optionsNode.getElements();
        while ( optionIterator.hasNext() )
        {
            JsonNode option = optionIterator.next();
            builder.addOption( JsonParserUtil.getStringValue( "label", option ), JsonParserUtil.getStringValue( "value", option ) );
        }
        return builder.build();
    }
}
