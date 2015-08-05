package com.enonic.xp.form.inputtype;

import org.w3c.dom.Element;

import com.enonic.xp.app.ApplicationKey;

final class DateConfigSerializer
    extends TimezoneConfigSerializer<DateConfig>
{
    public static final DateConfigSerializer INSTANCE = new DateConfigSerializer();

    @Override
    public DateConfig parseConfig( final ApplicationKey currentApplication, final Element elem )
    {
        final DateConfig.Builder builder = DateConfig.create();
        parseTimezone( elem, builder );
        return builder.build();
    }
}
