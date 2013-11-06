package com.enonic.wem.core.content.page;

import java.io.IOException;

import org.jdom.Document;
import org.jdom.JDOMException;

import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.support.serializer.XmlParsingException;
import com.enonic.wem.core.support.util.JdomHelper;

public final class PageDescriptorXmlSerializer
{
    private final JdomHelper jdomHelper = new JdomHelper();

    private boolean prettyPrint = true;

    public PageDescriptorXmlSerializer prettyPrint( boolean value )
    {
        this.prettyPrint = value;
        return this;
    }

    public String toString( final PageDescriptor pageDescriptor )
    {
        return this.jdomHelper.serialize( toJDomDocument( pageDescriptor ), this.prettyPrint );
    }

    public Document toJDomDocument( final PageDescriptor pageDescriptor )
    {
        return null; // TODO
    }

    public PageDescriptor toPageDescriptor( final String xml )
        throws XmlParsingException
    {
        try
        {
            final Document document = this.jdomHelper.parse( xml );
            // TODO
            return PageDescriptor.newPageDescriptor().build();
        }
        catch ( JDOMException | IOException e )
        {
            throw new XmlParsingException( "Failed to read XML", e );
        }
    }

}
