package com.enonic.xp.core.impl.export.xml;

import java.net.URL;

import org.junit.BeforeClass;
import org.w3c.dom.Document;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexValueProcessor;
import com.enonic.xp.index.IndexValueProcessorRegistry;
import com.enonic.xp.xml.DomHelper;

import static org.junit.Assert.*;

public abstract class BaseXmlSerializerTest
{
    @BeforeClass
    public static void registerIndexValueProcessors()
    {
        final IndexValueProcessor indexValueProcessor = new IndexValueProcessor()
        {
            @Override
            public Value process( final Value value )
            {
                return value;
            }

            @Override
            public String getName()
            {
                return "indexValueProcessor";
            }
        };
        IndexValueProcessorRegistry.register( indexValueProcessor );
    }

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
