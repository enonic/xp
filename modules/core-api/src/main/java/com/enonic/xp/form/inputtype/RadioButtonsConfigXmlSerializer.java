package com.enonic.xp.form.inputtype;

import org.w3c.dom.Element;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.xml.DomHelper;

final class RadioButtonsConfigXmlSerializer
    implements InputTypeConfigXmlSerializer
{
    public static final RadioButtonsConfigXmlSerializer DEFAULT = new RadioButtonsConfigXmlSerializer();

    @Override
    public InputTypeConfig parseConfig( final ApplicationKey currentApplication, final Element elem )
    {
        final RadioButtonsConfig.Builder builder = RadioButtonsConfig.create();

        final Element optionsEl = DomHelper.getChildElementByTagName( elem, "options" );
        for ( final Element optionEl : DomHelper.getChildElementsByTagName( optionsEl, "option" ) )
        {
            final String label = DomHelper.getChildElementValueByTagName( optionEl, "label" );
            final String value = DomHelper.getChildElementValueByTagName( optionEl, "value" );
            builder.addOption( label, value );
        }

        return builder.build();
    }
}
