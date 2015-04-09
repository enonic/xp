package com.enonic.xp.form.inputtype;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.annotations.Beta;

import com.enonic.xp.module.ModuleKey;
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

    public final T parseConfig( ModuleKey currentModule, final Document doc )
    {
        return parseConfig( currentModule, doc.getDocumentElement() );
    }

    public abstract T parseConfig( ModuleKey currentModule, Element elem );
}
