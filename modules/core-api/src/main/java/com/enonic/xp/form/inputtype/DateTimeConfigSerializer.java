package com.enonic.xp.form.inputtype;

import org.w3c.dom.Element;

import com.enonic.xp.app.ApplicationKey;

final class DateTimeConfigSerializer
    extends TimezoneConfigSerializer<DateTimeConfig>
{
    @Override
    public DateTimeConfig parseConfig( final ApplicationKey currentApplication, final Element elem )
    {
        final DateTimeConfig.Builder builder = DateTimeConfig.create();
        parseTimezone( elem, builder );
        return builder.build();
    }
}
