package com.enonic.xp.form.inputtype;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleRelativeResolver;
import com.enonic.xp.xml.DomBuilder;
import com.enonic.xp.xml.DomHelper;

final class DateTimeConfigXmlSerializer
    extends AbstractInputTypeConfigXmlSerializer<DateTimeConfig>
{
    public static final DateTimeConfigXmlSerializer DEFAULT = new DateTimeConfigXmlSerializer();

    @Override
    protected void serializeConfig( final DateTimeConfig dateTimeConfig, final DomBuilder builder )
    {
        builder.start( "with-timezone" );
        if ( dateTimeConfig.isWithTimezone() != null )
        {
            builder.text( dateTimeConfig.isWithTimezone().toString() );
        }

        builder.end();
    }

    @Override
    public DateTimeConfig parseConfig( final ModuleKey currentModule, final Element elem )
    {
        final DateTimeConfig.Builder builder = DateTimeConfig.newDateTimeConfig();
        final Element relationshipTypeEl = DomHelper.getChildElementByTagName( elem, "with-timezone" );

        final String text = DomHelper.getTextValue( relationshipTypeEl );
        if ( text != null && StringUtils.isNotBlank( text ) )
        {
            builder.withTimezone( Boolean.valueOf( text ) );
        }

        return builder.build();
    }

}
