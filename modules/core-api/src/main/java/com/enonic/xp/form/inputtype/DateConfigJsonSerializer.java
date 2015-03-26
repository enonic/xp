package com.enonic.xp.form.inputtype;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class DateConfigJsonSerializer
    extends AbstractInputTypeConfigJsonSerializer<DateConfig>
{
    public static final DateConfigJsonSerializer DEFAULT = new DateConfigJsonSerializer();

    @Override
    public JsonNode serializeConfig( final DateConfig config, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonConfig = objectMapper.createObjectNode();
        if ( config.isWithTimezone() != null )
        {
            jsonConfig.put( "withTimezone", config.isWithTimezone().toString() );
        }
        else
        {
            jsonConfig.put( "withTimezone", false );
        }
        return jsonConfig;
    }

    @Override
    public DateConfig parseConfig( final JsonNode inputTypeConfigNode )
    {
        final DateConfig.Builder builder = DateConfig.newDateConfig();
        final JsonNode withTimeZoneNode = inputTypeConfigNode.get( "withTimezone" );
        if ( withTimeZoneNode != null && !withTimeZoneNode.isNull() )
        {
            builder.withTimezone( Boolean.valueOf( withTimeZoneNode.textValue() ) );
        }
        return builder.build();
    }
}