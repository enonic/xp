package com.enonic.xp.form.inputtype;

import org.w3c.dom.Element;

import com.enonic.xp.module.ModuleKey;

final class DateTimeConfigXmlSerializer
    extends TimezoneConfigXmlSerializer<DateTimeConfig>
{
    public static final DateTimeConfigXmlSerializer DEFAULT = new DateTimeConfigXmlSerializer();

    @Override
    public DateTimeConfig parseConfig( final ModuleKey currentModule, final Element elem )
    {
        final DateTimeConfig.Builder builder = DateTimeConfig.newDateTimeConfig();
        parseTimezone( currentModule, elem, builder );
        return builder.build();
    }

}