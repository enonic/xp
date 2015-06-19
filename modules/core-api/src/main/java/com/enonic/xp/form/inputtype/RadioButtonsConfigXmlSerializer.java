package com.enonic.xp.form.inputtype;

import org.w3c.dom.Element;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.xml.DomBuilder;
import com.enonic.xp.xml.DomHelper;

final class RadioButtonsConfigXmlSerializer
    extends AbstractInputTypeConfigXmlSerializer
{
    public static final RadioButtonsConfigXmlSerializer DEFAULT = new RadioButtonsConfigXmlSerializer();

    @Override
    protected void serializeConfig( final InputTypeConfig config, final DomBuilder builder )
    {
        final RadioButtonsConfig radioButtonsConfig = (RadioButtonsConfig) config;
        builder.start( "options" );
        for ( final Option option : radioButtonsConfig.getOptions() )
        {
            builder.start( "option" );
            builder.start( "label" ).text( option.getLabel() ).end();
            builder.start( "value" ).text( option.getValue() ).end();
            builder.end();
        }
        builder.end();
    }

    @Override
    public InputTypeConfig parseConfig( final ModuleKey currentModule, final Element elem )
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
