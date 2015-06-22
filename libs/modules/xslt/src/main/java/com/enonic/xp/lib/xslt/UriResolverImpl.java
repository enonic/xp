package com.enonic.xp.lib.xslt;

import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import com.enonic.xp.resource.ResourceKey;

final class UriResolverImpl
    implements URIResolver
{
    @Override
    public Source resolve( final String href, final String base )
        throws TransformerException
    {
        try
        {
            final URL url = new URL( base );
            return resolve( href, ResourceKey.from( url ) );
        }
        catch ( final Exception e )
        {
            throw new TransformerException( e );
        }
    }

    private Source resolve( final String href, final ResourceKey base )
        throws TransformerException
    {
        final ResourceKey resolved = base.resolve( "../" + href );
        return new StreamSource( "module:" + resolved.getUri() );
    }
}
