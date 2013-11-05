package com.enonic.wem.core.content.page;

import java.io.IOException;

import org.jdom.Document;
import org.jdom.JDOMException;

import com.enonic.wem.api.content.page.PartDescriptor;
import com.enonic.wem.core.support.serializer.XmlParsingException;
import com.enonic.wem.core.support.util.JdomHelper;

public final class PartDescriptorXmlSerializer
{
    private final JdomHelper jdomHelper = new JdomHelper();

    private boolean prettyPrint = true;

    public PartDescriptorXmlSerializer prettyPrint( boolean value )
    {
        this.prettyPrint = value;
        return this;
    }

    public String toString( final PartDescriptor partDescriptor )
    {
        return this.jdomHelper.serialize( toJDomDocument( partDescriptor ), this.prettyPrint );
    }

    public Document toJDomDocument( final PartDescriptor partDescriptor )
    {
        return null; // TODO
    }

    public PartDescriptor toPartDescriptor( final String xml )
        throws XmlParsingException
    {
        try
        {
            final Document document = this.jdomHelper.parse( xml );
            // TODO
            return PartDescriptor.newPartDescriptor().build();
        }
        catch ( JDOMException | IOException e )
        {
            throw new XmlParsingException( "Failed to read XML", e );
        }
    }

}
