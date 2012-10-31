package com.enonic.wem.api.content.type.formitem.inputtype;


import org.jdom.Element;

public abstract class AbstractInputTypeConfigSerializerXml
{
    public final Element generate( final InputTypeConfig config )
    {
        final Element inputTypeConfigEl = new Element( "input-type-config" );
        inputTypeConfigEl.setAttribute( "name", config.getClass().getName() );
        generateConfig( config, inputTypeConfigEl );
        return inputTypeConfigEl;
    }

    public abstract void generateConfig( InputTypeConfig config, Element inputTypeConfigEl );


    public abstract InputTypeConfig parseConfig( final Element inputTypeConfigEl );
}
