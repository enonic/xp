package com.enonic.xp.core.impl.export.xml.serializer;

import java.net.URL;

import org.w3c.dom.Document;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.wem.api.xml.DomHelper;

import static org.junit.Assert.*;

public abstract class BaseXmlSerializerTest
{
    final String readFromFile( final String fileName )
        throws Exception
    {
        final URL url = getClass().getResource( fileName );
        if ( url == null )
        {
            throw new IllegalArgumentException( "Resource file [" + fileName + "] not found" );
        }

        final String xml = Resources.toString( url, Charsets.UTF_8 );
        return normalizeXml( xml );
    }

    final void assertXml( final String expectedFileName, final String actualSerialization )
        throws Exception
    {
        final String expectedXml = readFromFile( expectedFileName );
        assertEquals( expectedXml, normalizeXml( actualSerialization ) );
    }

    private String normalizeXml( final String xml )
    {
        final Document doc = DomHelper.parse( xml );
        return DomHelper.serialize( doc );
    }
}
