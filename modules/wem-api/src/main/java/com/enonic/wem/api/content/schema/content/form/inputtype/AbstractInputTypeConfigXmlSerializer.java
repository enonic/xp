package com.enonic.wem.api.content.schema.content.form.inputtype;


import org.jdom.Element;

public abstract class AbstractInputTypeConfigXmlSerializer
{
    public final Element generate( final InputTypeConfig config )
    {
        final Element inputTypeConfigEl = new Element( "config" );
        generateConfig( config, inputTypeConfigEl );
        return inputTypeConfigEl;
    }

    public abstract void generateConfig( InputTypeConfig config, Element inputTypeConfigEl );


    public abstract InputTypeConfig parseConfig( final Element inputTypeConfigEl );
}
