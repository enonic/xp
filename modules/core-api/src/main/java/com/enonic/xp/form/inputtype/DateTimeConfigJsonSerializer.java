package com.enonic.xp.form.inputtype;


import com.fasterxml.jackson.databind.JsonNode;


public class DateTimeConfigJsonSerializer
    extends TimezoneConfigJsonSerializer<DateTimeConfig>
{
    public static final DateTimeConfigJsonSerializer DEFAULT = new DateTimeConfigJsonSerializer();

    @Override
    public DateTimeConfig parseConfig( final JsonNode inputTypeConfigNode )
    {
        final DateTimeConfig.Builder builder = DateTimeConfig.create();
        parseTimezone( inputTypeConfigNode, builder );
        return builder.build();
    }
}