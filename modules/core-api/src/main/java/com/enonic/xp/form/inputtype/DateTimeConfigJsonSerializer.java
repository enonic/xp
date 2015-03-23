package com.enonic.xp.form.inputtype;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.schema.relationship.RelationshipTypeName;


public class DateTimeConfigJsonSerializer
    extends AbstractInputTypeConfigJsonSerializer<DateTimeConfig>
{
    public static final DateTimeConfigJsonSerializer DEFAULT = new DateTimeConfigJsonSerializer();

    @Override
    public JsonNode serializeConfig( final DateTimeConfig config, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonConfig = objectMapper.createObjectNode();
        if ( config.isWithTimezone() != null )
        {
            jsonConfig.put( "withTimezone", config.isWithTimezone().toString() );
        }
        else
        {
            jsonConfig.putNull( "withTimezone" );
        }
        return jsonConfig;
    }

    @Override
    public DateTimeConfig parseConfig( final JsonNode inputTypeConfigNode )
    {
        final DateTimeConfig.Builder builder = DateTimeConfig.newDateTimeConfig();
        final JsonNode withTimeZoneNode = inputTypeConfigNode.get( "withTimezone" );
        if ( withTimeZoneNode != null && !withTimeZoneNode.isNull() )
        {
            builder.withTimezone( Boolean.valueOf( withTimeZoneNode.textValue() ) );
        }
        return builder.build();
    }
}
