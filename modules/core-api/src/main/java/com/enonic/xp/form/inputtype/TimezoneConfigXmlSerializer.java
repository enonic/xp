package com.enonic.xp.form.inputtype;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.xml.DomBuilder;
import com.enonic.xp.xml.DomHelper;

abstract class TimezoneConfigXmlSerializer<T extends TimezoneConfig>
    extends AbstractInputTypeConfigXmlSerializer<T>
{

    @Override
    protected void serializeConfig( final T TimezoneConfig, final DomBuilder builder )
    {
        builder.start( "with-timezone" );
        builder.text( String.valueOf( TimezoneConfig.isWithTimezone() ) );
        builder.end();
    }

    @Override
    public T parseConfig( final ModuleKey currentModule, final Element elem )
    {
        final T.Builder builder = T.newTimezoneConfig();
        final Element relationshipTypeEl = DomHelper.getChildElementByTagName( elem, "with-timezone" );

        final String text = DomHelper.getTextValue( relationshipTypeEl );
        if ( text != null && StringUtils.isNotBlank( text ) )
        {
            builder.withTimezone( Boolean.valueOf( text ) );
        }

        return (T) builder.build();
    }


    public void parseTimezone( final ModuleKey currentModule, final Element elem, T.Builder builder )
    {
        final Element relationshipTypeEl = DomHelper.getChildElementByTagName( elem, "with-timezone" );

        final String text = DomHelper.getTextValue( relationshipTypeEl );
        if ( text != null && StringUtils.isNotBlank( text ) )
        {
            builder.withTimezone( Boolean.valueOf( text ) );
        }
    }

}