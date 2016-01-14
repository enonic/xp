package com.enonic.xp.admin.impl.app;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.jaxrs.JaxRsComponent;

@Path("/")
@Component(immediate = true)
public final class MainResource
    implements JaxRsComponent
{

    private final ResourceHandler resourceHandler;

    public MainResource()
    {
        this.resourceHandler = new ResourceHandler();
    }

    @GET
    public Response redirectToLoginPage()
        throws Exception
    {
        return Response.temporaryRedirect( new URI( "/admin/tool" ) ).build();
    }

    @GET
    @Path("{path:.+}")
    public Response getResource( @PathParam("path") final String path )
        throws Exception
    {
        return this.resourceHandler.handle( path );
    }

    @GET
    @Path("admin/assets/{version}/{path:.+}")
    public Response getVersionedResource( @PathParam("version") final String version, @PathParam("path") final String path )
        throws Exception
    {
        return this.resourceHandler.handle( "admin/" + path, true );
    }

    @GET
    @Path("admin")
    public Response getAdminApp( @QueryParam("app") @DefaultValue("app-launcher") final String app )
        throws URISyntaxException
    {
        return Response.temporaryRedirect( new URI( "/admin/tool" ) ).build();
    }

    @Reference
    public void setResourceLocator( final ResourceLocator resourceLocator )
    {
        this.resourceHandler.setResourceLocator( resourceLocator );
    }
}
