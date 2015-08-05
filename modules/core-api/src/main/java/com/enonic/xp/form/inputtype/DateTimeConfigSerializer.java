package com.enonic.xp.form.inputtype;

import org.w3c.dom.Element;

import com.enonic.xp.app.ApplicationKey;

final class DateTimeConfigSerializer
    extends TimezoneConfigSerializer<DateTimeConfig>
{
    public static final DateTimeConfigSerializer INSTANCE = new DateTimeConfigSerializer();

    @Override
    public DateTimeConfig parseConfig( final ApplicationKey currentApplication, final Element elem )
    {
        final DateTimeConfig.Builder builder = DateTimeConfig.create();
        parseTimezone( elem, builder );
        return builder.build();
    }
}
