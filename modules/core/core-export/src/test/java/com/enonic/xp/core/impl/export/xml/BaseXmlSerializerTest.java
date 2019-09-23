package com.enonic.xp.core.impl.export.xml;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.w3c.dom.Document;

import com.google.common.io.Resources;

import com.enonic.xp.xml.DomHelper;

import static org.junit.jupiter.api.Assertions.*;

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

        final String xml = Resources.toString( url, StandardCharsets.UTF_8 );
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
