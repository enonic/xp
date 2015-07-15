package com.enonic.xp.form.inputtype;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.xml.DomBuilder;

@Beta
public abstract class AbstractInputTypeConfigXmlSerializer<T extends InputTypeConfig>
{
    public final Document generate( final T config )
    {
        final DomBuilder builder = DomBuilder.create( "config" );
        serializeConfig( config, builder );
        return builder.getDocument();
    }

    protected abstract void serializeConfig( T config, DomBuilder builder );

    public final T parseConfig( ApplicationKey currentApp, final Document doc )
    {
        return parseConfig( currentApp, doc.getDocumentElement() );
    }

    public abstract T parseConfig( ApplicationKey currentApp, Element elem );
}
