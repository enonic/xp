package com.enonic.wem.core.module;

import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.core.support.serializer.XmlParsingException;
import com.enonic.wem.core.support.util.JdomHelper;

public final class ModuleXmlSerializer
{
    private final JdomHelper jdomHelper = new JdomHelper();

    private boolean prettyPrint = true;

    public ModuleXmlSerializer prettyPrint( boolean value )
    {
        this.prettyPrint = value;
        return this;
    }

    public String toString( final Module module )
    {
        return this.jdomHelper.serialize( toJDomDocument( module ), this.prettyPrint );
    }

    public Document toJDomDocument( final Module module )
    {
        final Element typeEl = new Element( "module" );
        generate( module, typeEl );
        return new Document( typeEl );
    }

    private void generate( final Module module, final Element moduleEl )
    {
        moduleEl.addContent( new Element( "display-name" ).setText( module.getDisplayName() ) );
    }

    public void toModule( final String xml, final Module.Builder moduleBuilder )
        throws XmlParsingException
    {
        try
        {
            final Document document = this.jdomHelper.parse( xml );
            parse( document.getRootElement(), moduleBuilder );
        }
        catch ( JDOMException | IOException e )
        {
            throw new XmlParsingException( "Failed to read XML", e );
        }
    }

    private void parse( final Element moduleEl, final Module.Builder moduleBuilder )
        throws IOException
    {
        final String displayName = moduleEl.getChildText( "display-name" );
        moduleBuilder.
            displayName( displayName );
    }
}
