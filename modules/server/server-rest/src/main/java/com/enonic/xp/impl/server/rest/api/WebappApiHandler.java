package com.enonic.xp.impl.server.rest.api;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.impl.server.rest.WebappJsonFactory;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

/**
 * {@code server:webapp} - the installed applications that ship a webapp.
 */
@Component(service = UniversalApiHandler.class, property = {"key=server:webapp", "title=Webapp API", "mount=management",
    "allowedPrincipals=role:system.admin"})
public class WebappApiHandler
    extends ManagementApiHandler
{
    static final String KEY = "server:webapp";

    private final WebappJsonFactory webappJsonFactory;

    @Activate
    public WebappApiHandler( @Reference final ApplicationService applicationService, @Reference final ResourceService resourceService )
    {
        super( KEY );
        this.webappJsonFactory = new WebappJsonFactory( applicationService, resourceService );

        route( HttpMethod.GET, "/", "list", this::list );
    }

    private WebResponse list( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        return json( Map.of( "webapps", webappJsonFactory.list() ) );
    }
}
