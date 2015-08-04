package com.enonic.xp.form.inputtype;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.annotations.Beta;

@Beta
public class RadioButtonsConfigJsonSerializer
    implements InputTypeConfigJsonSerializer<RadioButtonsConfig>
{
    public static final RadioButtonsConfigJsonSerializer DEFAULT = new RadioButtonsConfigJsonSerializer();

    @Override
    public JsonNode serializeConfig( final RadioButtonsConfig radioButtonsConfig, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonConfig = objectMapper.createObjectNode();

        final ArrayNode jsonArray = jsonConfig.putArray( "options" );
        for ( Option option : radioButtonsConfig.getOptions() )
        {
            final ObjectNode jsonOption = jsonArray.addObject();
            jsonOption.put( "label", option.getLabel() );
            jsonOption.put( "value", option.getValue() );
        }
        return jsonConfig;
    }
}
