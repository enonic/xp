package com.enonic.xp.form.inputtype;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class TimezoneConfigJsonSerializer<T extends TimezoneConfig>
    implements InputTypeConfigJsonSerializer<T>
{
    @Override
    public JsonNode serializeConfig( final T config, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonConfig = objectMapper.createObjectNode();
        jsonConfig.put( "withTimezone", config.isWithTimezone() );
        return jsonConfig;
    }
}
