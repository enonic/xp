package com.enonic.wem.api.schema.content.form.inputtype;


import java.util.Iterator;

import org.jdom.Element;

import static com.enonic.wem.api.schema.content.form.inputtype.ComboBoxConfig.newComboBoxConfig;

public class ComboBoxConfigXmlSerializer
    extends AbstractInputTypeConfigXmlSerializer<ComboBoxConfig>
{
    public static final ComboBoxConfigXmlSerializer DEFAULT = new ComboBoxConfigXmlSerializer();

    public void serializeConfig( final ComboBoxConfig config, final Element inputTypeConfigEl )
    {
        final Element optionsEl = new Element( "options" );
        inputTypeConfigEl.addContent( optionsEl );

        for ( ComboBoxConfig.Option option : config.getOptions() )
        {
            final Element optionEl = new Element( "option" );
            optionEl.addContent( new Element( "label" ).setText( option.getLabel() ) );
            optionEl.addContent( new Element( "value" ).setText( option.getValue() ) );
            optionsEl.addContent( optionEl );
        }
    }

    @Override
    public ComboBoxConfig parseConfig( final Element inputTypeConfigEl )
    {
        final ComboBoxConfig.Builder builder = newComboBoxConfig();
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
