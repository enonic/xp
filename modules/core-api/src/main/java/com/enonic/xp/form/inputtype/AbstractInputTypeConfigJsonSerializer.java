package com.enonic.xp.form.inputtype;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.Beta;

@Beta
public abstract class AbstractInputTypeConfigJsonSerializer<T extends InputTypeConfig>
{
    public JsonNode serialize( final T config, final ObjectMapper objectMapper )
    {
        return serializeConfig( config, objectMapper );
    }

    public abstract JsonNode serializeConfig( final T config, final ObjectMapper objectMapper );

    public abstract T parseConfig( final JsonNode inputTypeConfigNode );
}
