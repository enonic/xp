package com.enonic.xp.support;

import java.net.URL;

import org.w3c.dom.Document;

import com.enonic.xp.xml.DomHelper;

public final class XmlTestHelper
{
    private final ResourceTestHelper resourceTestHelper;

    public XmlTestHelper( final Object testInstance )
    {
        this.resourceTestHelper = new ResourceTestHelper( testInstance );
    }

    public Document parseXml( final String fileName )
    {
        return DomHelper.parse( loadTestFile( fileName ) );
    }

    public String loadTestFile( final String fileName )
    {
        return resourceTestHelper.loadTestFile( fileName );
    }

    public String loadTestXml( final String fileName )
    {
        try
        {
            final URL resource = resourceTestHelper.getTestResource( fileName );
            final Document document = DomHelper.parse( resource.openStream() );
            return DomHelper.serialize( document );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }
}
