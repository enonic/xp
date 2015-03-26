package com.enonic.xp.form.inputtype;


import com.fasterxml.jackson.databind.JsonNode;


public class DateConfigJsonSerializer
    extends TimezoneConfigJsonSerializer<DateConfig>
{
    public static final DateConfigJsonSerializer DEFAULT = new DateConfigJsonSerializer();

    @Override
    public DateConfig parseConfig( final JsonNode inputTypeConfigNode )
    {
        final DateConfig.Builder builder = DateConfig.newDateConfig();
        parseTimezone( inputTypeConfigNode, builder );
        return builder.build();
    }
}