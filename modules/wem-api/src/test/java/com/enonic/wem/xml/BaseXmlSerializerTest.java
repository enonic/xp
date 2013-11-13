package com.enonic.wem.xml;

import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import static org.junit.Assert.*;
import static org.xmlmatchers.XmlMatchers.isEquivalentTo;
import static org.xmlmatchers.transform.XmlConverters.the;

public abstract class BaseXmlSerializerTest
{
    protected final String readFromFile( final String fileName )
        throws Exception
    {
        final URL url = getClass().getResource( fileName );
        if ( url == null )
        {
            throw new IllegalArgumentException( "Resource file [" + fileName + "] not found" );
        }
        return Resources.toString( url, Charsets.UTF_8 );
    }

    protected final void assertXml( final String expectedFileName, final String actualSerialization )
        throws Exception
    {
        final String expectedXml = readFromFile( expectedFileName );
        assertThat( "Serialization not as expected", the( expectedXml ), isEquivalentTo( the( actualSerialization ) ) );
    }
}
