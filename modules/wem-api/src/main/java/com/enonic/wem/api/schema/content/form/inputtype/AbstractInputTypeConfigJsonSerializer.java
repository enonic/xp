package com.enonic.wem.api.schema.content.form.inputtype;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public abstract class AbstractInputTypeConfigJsonSerializer<T extends InputTypeConfig>
{
    public JsonNode serialize( final T config, final ObjectMapper objectMapper )
    {
        return serializeConfig( config, objectMapper );
    }

    public abstract JsonNode serializeConfig( final T config, final ObjectMapper objectMapper );

    public abstract T parseConfig( final JsonNode inputTypeConfigNode );
}
