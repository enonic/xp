package com.enonic.wem.core.content.type.formitem.comptype;


import java.util.Iterator;

import org.jdom.Element;

public class DropdownConfigSerializerXml
    extends AbstractComponentTypeConfigSerializerXml
{
    public static final DropdownConfigSerializerXml DEFAULT = new DropdownConfigSerializerXml();

    public void generateConfig( final ComponentTypeConfig config, final Element componentTypeConfigEl )
    {
        final Element optionsEl = new Element( "options" );
        componentTypeConfigEl.addContent( optionsEl );

        final DropdownConfig dropdownConfig = (DropdownConfig) config;

        for ( DropdownConfig.Option option : dropdownConfig.getOptions() )
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
        final DropdownConfig.Builder builder = DropdownConfig.newBuilder();
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
