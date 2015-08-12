package com.enonic.xp.lib.xslt;

import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

final class UriResolverImpl
    implements URIResolver
{
    private ResourceService resourceService;

    @Override
    public Source resolve( final String href, final String base )
        throws TransformerException
    {
        try
        {
            final URL url = new URL( base );
            return resolve( href, ResourceKey.from( url.getPath() ) );
        }
        catch ( final Exception e )
        {
            throw new TransformerException( e );
        }
    }

    private Source resolve( final String href, final ResourceKey base )
        throws TransformerException
    {
        final ResourceKey resolvedResourceKey = base.resolve( "../" + href );
        final Resource resolvedResource = resourceService.getResource( resolvedResourceKey );
        return resolvedResource.exists() ? new StreamSource( resolvedResource.getUrl().toString() ) : null;
    }

    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
