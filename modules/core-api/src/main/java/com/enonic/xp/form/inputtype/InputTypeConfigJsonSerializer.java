package com.enonic.xp.form.inputtype;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.Beta;

@Beta
public interface InputTypeConfigJsonSerializer<T extends InputTypeConfig>
{
    JsonNode serializeConfig( T config, ObjectMapper objectMapper );

    T parseConfig( JsonNode inputTypeConfigNode );
}
