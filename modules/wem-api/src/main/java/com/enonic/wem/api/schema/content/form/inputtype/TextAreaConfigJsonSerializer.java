package com.enonic.wem.api.schema.content.form.inputtype;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class TextAreaConfigJsonSerializer
    extends AbstractInputTypeConfigJsonSerializer<TextAreaConfig>
{
    public static final TextAreaConfigJsonSerializer DEFAULT = new TextAreaConfigJsonSerializer();

    @Override
    public JsonNode serializeConfig( final TextAreaConfig config, final ObjectMapper objectMapper )
    {
        final ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put( "rows", config.getRows() );
        objectNode.put( "columns", config.getColumns() );
        return objectNode;
    }

    @Override
    public TextAreaConfig parseConfig( final JsonNode inputTypeConfigNode )
    {
        final TextAreaConfig.Builder builder = TextAreaConfig.newTextAreaConfig();
        final JsonNode rowsNode = inputTypeConfigNode.get( "rows" );
        if ( rowsNode != null )
        {
            builder.rows( rowsNode.asInt() );
        }
        final JsonNode columnsNode = inputTypeConfigNode.get( "columns" );
        if ( columnsNode != null )
        {
            builder.columns( columnsNode.asInt() );
        }

        return builder.build();
    }
}
