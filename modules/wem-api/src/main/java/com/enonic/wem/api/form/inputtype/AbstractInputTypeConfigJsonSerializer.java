package com.enonic.wem.api.form.inputtype;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractInputTypeConfigJsonSerializer<T extends InputTypeConfig>
{
    public JsonNode serialize( final T config, final ObjectMapper objectMapper )
    {
        return serializeConfig( config, objectMapper );
    }

    public abstract JsonNode serializeConfig( final T config, final ObjectMapper objectMapper );

    public abstract T parseConfig( final JsonNode inputTypeConfigNode );
}
