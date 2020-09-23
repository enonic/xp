package com.enonic.xp.portal.impl.handler.asset;

import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.handler.EndpointHandler;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class)
public final class AssetHandler
    extends EndpointHandler
{
    private static final Pattern PATTERN = Pattern.compile( "([^/^:]+)(:[^/]+)?/(.+)" );

    private ResourceService resourceService;

    public AssetHandler()
    {
        super( EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS ), "asset" );
    }

    @Override
    protected PortalResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        final String restPath = findRestPath( webRequest );

        final Matcher matcher = PATTERN.matcher( restPath );

        if ( !matcher.find() )
        {
            throw WebException.notFound( "Not a valid asset url pattern" );
        }

        final AssetHandlerWorker worker = new AssetHandlerWorker( webRequest );
        final ApplicationKey applicationKey = ApplicationKey.from( matcher.group( 1 ) );
        worker.cacheable = matcher.group( 2 ) != null;
        worker.applicationKey = applicationKey;
        worker.name = matcher.group( 3 );
        worker.resourceService = this.resourceService;

        return worker.execute();
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
