package com.enonic.wem.api.content.type.formitem.comptype;


import java.util.Iterator;

import org.jdom.Element;

import static com.enonic.wem.api.content.type.formitem.comptype.SingleSelectorConfig.newSingleSelectorConfig;

public class SingleSelectorConfigSerializerXml
    extends AbstractComponentTypeConfigSerializerXml
{
    public static final SingleSelectorConfigSerializerXml DEFAULT = new SingleSelectorConfigSerializerXml();

    public void generateConfig( final ComponentTypeConfig config, final Element componentTypeConfigEl )
    {
        final Element optionsEl = new Element( "options" );
        componentTypeConfigEl.addContent( optionsEl );

        final SingleSelectorConfig singleSelectorConfig = (SingleSelectorConfig) config;
        componentTypeConfigEl.addContent( new Element( "selector-type" ).setText( singleSelectorConfig.getType().toString() ) );
        for ( SingleSelectorConfig.Option option : singleSelectorConfig.getOptions() )
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
        final SingleSelectorConfig.Builder builder = newSingleSelectorConfig();
        final Element selectorTypeEl = componentTypeConfigEl.getChild( "selector-type" );
        builder.type( SingleSelectorConfig.SelectorType.valueOf( selectorTypeEl.getText() ) );
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
