package com.enonic.xp.xml.parser;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.common.io.CharSource;
import com.google.common.io.Resources;

public abstract class XmlModelParserTest
{
    private URL findResource( final String suffix )
    {
        final String name = getClass().getSimpleName() + suffix;
        final URL url = getClass().getResource( name );

        if ( url == null )
        {
            throw new IllegalArgumentException( "Could not find resource [" + name + "]" );
        }

        return url;
    }

    private String removeNs( final String xml )
    {
        return xml.replace( "xmlns=\"urn:enonic:xp:model:1.0\"", "" );
    }

    protected final void parse( final XmlModelParser parser, final String suffix )
        throws Exception
    {
        final URL resource = findResource( suffix );
        parser.systemId( resource.toString() );
        parser.source( Resources.asCharSource( resource, StandardCharsets.UTF_8 ) );
        parser.parse();
    }

    protected final void parseRemoveNs( final XmlModelParser parser, final String suffix )
        throws Exception
    {
        final URL url = findResource( suffix );
        final String xml;
        try (InputStream stream = url.openStream())
        {
            xml = new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }

        parser.systemId( url.toString() );
        parser.source( CharSource.wrap( removeNs( xml ) ) );
        parser.parse();
    }
}
