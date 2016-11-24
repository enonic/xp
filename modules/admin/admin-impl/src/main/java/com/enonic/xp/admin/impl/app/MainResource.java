package com.enonic.xp.admin.impl.app;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

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
        final URI uri = new URI( ServletRequestUrlHelper.createUri( "/admin/tool" ) );
        return Response.temporaryRedirect( uri ).build();
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
    public Response getAdminApp()
        throws Exception
    {
        return redirectToLoginPage();
    }

    @Reference
    public void setResourceLocator( final ResourceLocator resourceLocator )
    {
        this.resourceHandler.setResourceLocator( resourceLocator );
    }
}
