package com.enonic.wem.core.content.type.configitem.fieldtype;


import java.io.IOException;
import java.util.Iterator;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.core.content.JsonParserUtil;

public class RadioButtonsConfigSerializerJson
    extends AbstractFieldTypeConfigSerializerJson
{
    public static final RadioButtonsConfigSerializerJson DEFAULT = new RadioButtonsConfigSerializerJson();

    public void generateConfig( FieldTypeConfig config, JsonGenerator g )
        throws IOException
    {
        RadioButtonsConfig radioButtonsConfig = (RadioButtonsConfig) config;
        g.writeStartObject();
        g.writeArrayFieldStart( "options" );
        for ( RadioButtonsConfig.Option option : radioButtonsConfig.getOptions() )
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
    public FieldTypeConfig parseConfig( final JsonNode fieldTypeConfigNode )
    {
        final RadioButtonsConfig.Builder builder = RadioButtonsConfig.newBuilder();
        final JsonNode optionsNode = fieldTypeConfigNode.get( "options" );
        final Iterator<JsonNode> optionIterator = optionsNode.getElements();
        while ( optionIterator.hasNext() )
        {
            JsonNode option = optionIterator.next();
            builder.addOption( JsonParserUtil.getStringValue( "label", option ), JsonParserUtil.getStringValue( "value", option ) );
        }
        return builder.build();
    }
}
