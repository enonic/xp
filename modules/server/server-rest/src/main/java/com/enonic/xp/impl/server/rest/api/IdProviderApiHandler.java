package com.enonic.xp.impl.server.rest.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

/**
 * {@code server:idprovider} - the id providers, as vhost mappings refer to them. The provider configuration (which
 * holds client secrets for OIDC and similar providers) is never serialized.
 */
@Component(service = UniversalApiHandler.class, property = {"key=server:idprovider", "title=ID Provider API", "mount=management",
    "allowedPrincipals=role:system.admin"})
public class IdProviderApiHandler
    extends ManagementApiHandler
{
    static final String KEY = "server:idprovider";

    private final SecurityService securityService;

    @Activate
    public IdProviderApiHandler( @Reference final SecurityService securityService )
    {
        super( KEY );
        this.securityService = securityService;

        route( HttpMethod.GET, "/", "list", this::list );
    }

    private WebResponse list( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final List<Map<String, Object>> idProviders = securityService.getIdProviders().stream().map( IdProviderApiHandler::map ).toList();
        return json( Map.of( "idProviders", idProviders ) );
    }

    static Map<String, Object> map( final IdProvider idProvider )
    {
        final Map<String, Object> json = new LinkedHashMap<>();
        json.put( "key", idProvider.getKey().toString() );
        json.put( "displayName", idProvider.getDisplayName() );
        json.put( "description", idProvider.getDescription() );
        if ( idProvider.getIdProviderConfig() != null && idProvider.getIdProviderConfig().getApplicationKey() != null )
        {
            json.put( "applicationKey", idProvider.getIdProviderConfig().getApplicationKey().toString() );
        }
        return json;
    }
}
