package com.enonic.wem.api.content.schema.content.form.inputtype;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public abstract class AbstractInputTypeConfigJsonSerializer
{
    public JsonNode serialize( final InputTypeConfig config, final ObjectMapper objectMapper )
    {
        return serializeConfig( config, objectMapper );
    }

    public abstract JsonNode serializeConfig( final InputTypeConfig config, final ObjectMapper objectMapper );

    public abstract InputTypeConfig parseConfig( final JsonNode inputTypeConfigNode );
}
