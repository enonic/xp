package com.enonic.wem.api.form.inputtype;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.xml.DomBuilder;

public abstract class AbstractInputTypeConfigXmlSerializer<T extends InputTypeConfig>
{
    public final Document generate( final T config )
    {
        final DomBuilder builder = DomBuilder.create( "config" );
        serializeConfig( config, builder );
        return builder.getDocument();
    }

    protected abstract void serializeConfig( T config, DomBuilder builder );

    public final T parseConfig( ModuleKey currentModule, final Document doc )
    {
        return parseConfig( currentModule, doc.getDocumentElement() );
    }

    public abstract T parseConfig( ModuleKey currentModule, Element elem );
}
