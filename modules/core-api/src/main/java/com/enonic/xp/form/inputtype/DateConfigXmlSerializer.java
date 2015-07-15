package com.enonic.xp.form.inputtype;

import org.w3c.dom.Element;

import com.enonic.xp.app.ApplicationKey;

final class DateConfigXmlSerializer
    extends TimezoneConfigXmlSerializer<DateConfig>
{
    public static final DateConfigXmlSerializer DEFAULT = new DateConfigXmlSerializer();

    @Override
    public DateConfig parseConfig( final ApplicationKey currentModule, final Element elem )
    {
        final DateConfig.Builder builder = DateConfig.create();
        parseTimezone( currentModule, elem, builder );
        return builder.build();
    }

}