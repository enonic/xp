package com.enonic.wem.core.content.page;

import java.io.IOException;

import org.jdom.Document;
import org.jdom.JDOMException;

import com.enonic.wem.api.content.page.LayoutDescriptor;
import com.enonic.wem.core.support.serializer.XmlParsingException;
import com.enonic.wem.core.support.util.JdomHelper;

public final class LayoutDescriptorXmlSerializer
{
    private final JdomHelper jdomHelper = new JdomHelper();

    private boolean prettyPrint = true;

    public LayoutDescriptorXmlSerializer prettyPrint( boolean value )
    {
        this.prettyPrint = value;
        return this;
    }

    public String toString( final LayoutDescriptor layoutDescriptor )
    {
        return this.jdomHelper.serialize( toJDomDocument( layoutDescriptor ), this.prettyPrint );
    }

    public Document toJDomDocument( final LayoutDescriptor layoutDescriptor )
    {
        return null; // TODO
    }

    public LayoutDescriptor toLayoutDescriptor( final String xml )
        throws XmlParsingException
    {
        try
        {
            final Document document = this.jdomHelper.parse( xml );
            // TODO
            return LayoutDescriptor.newLayoutDescriptor().build();
        }
        catch ( JDOMException | IOException e )
        {
            throw new XmlParsingException( "Failed to read XML", e );
        }
    }

}
