package com.enonic.xp.form.inputtype;

import org.w3c.dom.Element;

import com.enonic.xp.module.ModuleKey;

final class DateConfigXmlSerializer
    extends TimezoneConfigXmlSerializer<DateConfig>
{
    public static final DateConfigXmlSerializer DEFAULT = new DateConfigXmlSerializer();

    @Override
    public DateConfig parseConfig( final ModuleKey currentModule, final Element elem )
    {
        final DateConfig.Builder builder = DateConfig.newDateConfig();
        parseTimezone( currentModule, elem, builder );
        return builder.build();
    }

}