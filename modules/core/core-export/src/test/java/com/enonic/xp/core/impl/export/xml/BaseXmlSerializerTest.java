package com.enonic.xp.core.impl.export.xml;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.w3c.dom.Document;

import com.enonic.xp.xml.DomHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class BaseXmlSerializerTest
{
    final String readFromFile( final String fileName )
        throws Exception
    {
        final InputStream stream =
            Objects.requireNonNull( getClass().getResourceAsStream( fileName ), "Resource file [" + fileName + "] not found" );
        try (stream)
        {
            final String xml = new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
            return normalizeXml( xml );
        }
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
