package com.enonic.wem.api.content.schema.content.form.inputtype;


import java.util.Iterator;

import org.jdom.Element;

import static com.enonic.wem.api.content.schema.content.form.inputtype.SingleSelectorConfig.newSingleSelectorConfig;

public class SingleSelectorConfigXmlSerializer
    extends AbstractInputTypeConfigXmlSerializer
{
    public static final SingleSelectorConfigXmlSerializer DEFAULT = new SingleSelectorConfigXmlSerializer();

    public void generateConfig( final InputTypeConfig config, final Element inputTypeConfigEl )
    {
        final Element optionsEl = new Element( "options" );
        inputTypeConfigEl.addContent( optionsEl );

        final SingleSelectorConfig singleSelectorConfig = (SingleSelectorConfig) config;
        inputTypeConfigEl.addContent( new Element( "selector-type" ).setText( singleSelectorConfig.getType().toString() ) );
        for ( SingleSelectorConfig.Option option : singleSelectorConfig.getOptions() )
        {
            final Element optionEl = new Element( "option" );
            optionEl.addContent( new Element( "label" ).setText( option.getLabel() ) );
            optionEl.addContent( new Element( "value" ).setText( option.getValue() ) );
            optionsEl.addContent( optionEl );
        }
    }

    @Override
    public InputTypeConfig parseConfig( final Element inputTypeConfigEl )
    {
        final SingleSelectorConfig.Builder builder = newSingleSelectorConfig();
        final Element selectorTypeEl = inputTypeConfigEl.getChild( "selector-type" );
        builder.type( SingleSelectorConfig.SelectorType.valueOf( selectorTypeEl.getText() ) );
        final Element optionsEl = inputTypeConfigEl.getChild( "options" );
        final Iterator optionIterator = optionsEl.getChildren( "option" ).iterator();
        while ( optionIterator.hasNext() )
        {
            Element optionEl = (Element) optionIterator.next();
            builder.addOption( optionEl.getChildText( "label" ), optionEl.getChildText( "value" ) );
        }
        return builder.build();
    }
}
