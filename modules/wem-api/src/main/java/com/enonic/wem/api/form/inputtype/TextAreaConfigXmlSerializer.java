package com.enonic.wem.api.form.inputtype;

import org.jdom.Element;

public class TextAreaConfigXmlSerializer
    extends AbstractInputTypeConfigXmlSerializer<TextAreaConfig>
{

    public static final TextAreaConfigXmlSerializer DEFAULT = new TextAreaConfigXmlSerializer();

    @Override
    public void serializeConfig( final TextAreaConfig config, final Element inputTypeConfigEl )
    {
        inputTypeConfigEl.addContent( new Element( "rows" ).setText( String.valueOf( config.getRows() ) ) );
        inputTypeConfigEl.addContent( new Element( "columns" ).setText( String.valueOf( config.getColumns() ) ) );
    }

    @Override
    public TextAreaConfig parseConfig( final Element inputTypeConfigEl )
    {
        final TextAreaConfig.Builder builder = TextAreaConfig.newTextAreaConfig();
        final Element rowsEl = inputTypeConfigEl.getChild( "rows" );
        if ( rowsEl != null )
        {
            builder.rows( Integer.valueOf( rowsEl.getText() ) );
        }
        final Element columnsEl = inputTypeConfigEl.getChild( "columns" );
        if ( columnsEl != null )
        {
            builder.columns( Integer.valueOf( columnsEl.getText() ) );
        }
        return builder.build();
    }
}
