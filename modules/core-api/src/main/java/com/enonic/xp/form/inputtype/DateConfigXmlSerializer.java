package com.enonic.xp.form.inputtype;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.xml.DomBuilder;
import com.enonic.xp.xml.DomHelper;

final class DateConfigXmlSerializer
    extends AbstractInputTypeConfigXmlSerializer<DateConfig>
{
    public static final DateConfigXmlSerializer DEFAULT = new DateConfigXmlSerializer();

    @Override
    protected void serializeConfig( final DateConfig DateConfig, final DomBuilder builder )
    {
        builder.start( "with-timezone" );
        if ( DateConfig.isWithTimezone() != null )
        {
            builder.text( DateConfig.isWithTimezone().toString() );
        }

        builder.end();
    }

    @Override
    public DateConfig parseConfig( final ModuleKey currentModule, final Element elem )
    {
        final DateConfig.Builder builder = DateConfig.newDateConfig();
        final Element relationshipTypeEl = DomHelper.getChildElementByTagName( elem, "with-timezone" );

        final String text = DomHelper.getTextValue( relationshipTypeEl );
        if ( text != null && StringUtils.isNotBlank( text ) )
        {
            builder.withTimezone( Boolean.valueOf( text ) );
        }

        return builder.build();
    }

}