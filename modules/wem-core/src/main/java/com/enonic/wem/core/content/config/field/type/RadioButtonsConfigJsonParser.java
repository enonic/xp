package com.enonic.wem.core.content.config.field.type;


import java.io.IOException;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import com.enonic.wem.core.content.JsonParserUtil;

public class RadioButtonsConfigJsonParser
    implements FieldTypeConfigJsonParser
{

    @Override
    public FieldTypeConfig parse( final JsonNode node )
    {
        final RadioButtonsConfig.Builder builder = RadioButtonsConfig.newBuilder();
        final JsonNode optionsNode = node.get( "options" );
        final Iterator<JsonNode> optionIterator = optionsNode.getElements();
        while ( optionIterator.hasNext() )
        {
            JsonNode option = optionIterator.next();
            builder.addOption( JsonParserUtil.getStringValue( "label", option ), JsonParserUtil.getStringValue( "value", option ) );
        }
        return builder.build();
    }

    public RadioButtonsConfig parse( final JsonParser jp )
        throws IOException
    {
        JsonToken token = jp.nextToken();

        RadioButtonsConfig.Builder builder = RadioButtonsConfig.newBuilder();

        while ( token != JsonToken.END_OBJECT )
        {
            if ( "options".equals( jp.getCurrentName() ) && token == JsonToken.FIELD_NAME )
            {
                jp.nextToken();

                while ( token != JsonToken.END_ARRAY )
                {
                    String optionLabel = null;
                    String optionValue = null;

                    while ( token != JsonToken.END_OBJECT )
                    {
                        if ( "value".equals( jp.getCurrentName() ) && token == JsonToken.FIELD_NAME )
                        {
                            jp.nextToken();
                            optionValue = jp.getText();
                        }
                        else if ( "label".equals( jp.getCurrentName() ) && token == JsonToken.FIELD_NAME )
                        {
                            jp.nextToken();
                            optionLabel = jp.getText();
                        }

                        token = jp.nextToken();
                    }

                    builder.addOption( optionLabel, optionValue );

                    token = jp.nextToken();
                }
            }

            token = jp.nextToken();
        }

        return builder.build();
    }
}
