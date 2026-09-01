package com.enonic.xp.impl.server.rest.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostService;

/**
 * {@code server:vhost} - every configured virtual host mapping, in matching order. The projection is fixed: a
 * mapping's {@code context} (which carries the API policies of this very endpoint) and {@code allow} list are never
 * serialized.
 */
@Component(service = UniversalApiHandler.class, property = {"key=server:vhost", "title=Virtual Host API", "mount=management",
    "allowedPrincipals=role:system.admin"})
public class VirtualHostApiHandler
    extends ManagementApiHandler
{
    static final String KEY = "server:vhost";

    private static final Map<String, String> ENDPOINTS =
        Map.of( DispatchConstants.WEB_CONNECTOR, "web", DispatchConstants.MANAGEMENT_CONNECTOR, "management",
                DispatchConstants.STATISTICS_CONNECTOR, "statistics" );

    private final VirtualHostService virtualHostService;

    @Activate
    public VirtualHostApiHandler( @Reference final VirtualHostService virtualHostService )
    {
        super( KEY );
        this.virtualHostService = virtualHostService;

        route( HttpMethod.GET, "/", "list", this::list );
    }

    private WebResponse list( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final List<Map<String, Object>> vhosts =
            virtualHostService.isEnabled() ? virtualHostService.getVirtualHosts().stream().map( VirtualHostApiHandler::map ).toList() : List.of();
        final Map<String, Object> json = new LinkedHashMap<>();
        json.put( "enabled", virtualHostService.isEnabled() );
        json.put( "vhosts", vhosts );
        return json( json );
    }

    static Map<String, Object> map( final VirtualHost virtualHost )
    {
        final Map<String, Object> json = new LinkedHashMap<>();
        json.put( "name", virtualHost.getName() );
        json.put( "host", virtualHost.getHost() );
        json.put( "source", virtualHost.getSource() );
        json.put( "target", virtualHost.getTarget() );
        json.put( "endpoint", ENDPOINTS.getOrDefault( virtualHost.getConnector(), virtualHost.getConnector() ) );
        if ( virtualHost.getOrder() != Integer.MAX_VALUE )
        {
            json.put( "order", virtualHost.getOrder() );
        }
        if ( virtualHost.getDefaultIdProviderKey() != null )
        {
            json.put( "defaultIdProviderKey", virtualHost.getDefaultIdProviderKey().toString() );
        }
        final List<Map<String, Object>> idProviders = new ArrayList<>();
        virtualHost.getIdProviders().forEach( ( key, idProvider ) -> {
            final Map<String, Object> entry = new LinkedHashMap<>();
            entry.put( "key", key.toString() );
            entry.put( "flows", idProvider.getFlows().stream().sorted().toList() );
            idProviders.add( entry );
        } );
        json.put( "idProviders", idProviders );
        return json;
    }
}
