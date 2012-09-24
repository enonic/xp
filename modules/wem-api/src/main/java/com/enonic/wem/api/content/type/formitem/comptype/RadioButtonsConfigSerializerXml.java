package com.enonic.wem.api.content.type.formitem.comptype;


import java.util.Iterator;

import org.jdom.Element;

public class RadioButtonsConfigSerializerXml
    extends AbstractComponentTypeConfigSerializerXml
{
    public static final RadioButtonsConfigSerializerXml DEFAULT = new RadioButtonsConfigSerializerXml();

    public void generateConfig( final ComponentTypeConfig config, final Element componentTypeConfigEl )
    {
        final Element optionsEl = new Element( "options" );
        componentTypeConfigEl.addContent( optionsEl );

        final RadioButtonsConfig radioButtonsConfig = (RadioButtonsConfig) config;

        for ( RadioButtonsConfig.Option option : radioButtonsConfig.getOptions() )
        {
            final Element optionEl = new Element( "option" );
            optionEl.addContent( new Element( "label" ).setText( option.getLabel() ) );
            optionEl.addContent( new Element( "value" ).setText( option.getValue() ) );
            optionsEl.addContent( optionEl );
        }
    }

    @Override
    public ComponentTypeConfig parseConfig( final Element componentTypeConfigEl )
    {
        final RadioButtonsConfig.Builder builder = RadioButtonsConfig.newBuilder();
        final Element optionsEl = componentTypeConfigEl.getChild( "options" );
        final Iterator optionIterator = optionsEl.getChildren( "option" ).iterator();
        while ( optionIterator.hasNext() )
        {
            Element optionEl = (Element) optionIterator.next();
            builder.addOption( optionEl.getChildText( "label" ), optionEl.getChildText( "value" ) );
        }
        return builder.build();
    }
}
