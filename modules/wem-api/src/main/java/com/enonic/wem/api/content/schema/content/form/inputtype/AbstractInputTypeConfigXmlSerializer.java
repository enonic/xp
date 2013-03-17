package com.enonic.wem.api.content.schema.content.form.inputtype;


import org.jdom.Element;

public abstract class AbstractInputTypeConfigXmlSerializer<T extends InputTypeConfig>
{
    public final Element generate( final T config )
    {
        final Element inputTypeConfigEl = new Element( "config" );
        serializeConfig( config, inputTypeConfigEl );
        return inputTypeConfigEl;
    }

    public abstract void serializeConfig( T config, Element inputTypeConfigEl );


    public abstract T parseConfig( final Element inputTypeConfigEl );
}
