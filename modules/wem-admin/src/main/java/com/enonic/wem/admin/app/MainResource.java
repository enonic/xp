package com.enonic.wem.admin.app;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.enonic.xp.web.jaxrs.JaxRsComponent;

@Path("/")
public final class MainResource
    implements JaxRsComponent
{
    @Context
    UriInfo uriInfo;

    private final AppHtmlHandler appHtmlHandler;

    private final ResourceHandler resourceHandler;

    public MainResource()
    {
        this.appHtmlHandler = new AppHtmlHandler();
        this.resourceHandler = new ResourceHandler();
    }

    @GET
    public Response getIndexHtml()
        throws Exception
    {
        return getResource( "index.html" );
    }

    @GET
    @Path("{path:.+}")
    public Response getResource( @PathParam("path") final String path )
        throws Exception
    {
        return this.resourceHandler.handle( path );
    }

    @GET
    @Path("admin")
    @Produces("text/html")
    public String getAdminApp( @QueryParam("app") @DefaultValue("app-launcher") final String app )
    {
        return this.appHtmlHandler.render( app, uriInfo );
    }

    public void setResourceLocator( final ResourceLocator resourceLocator )
    {
        this.resourceHandler.setResourceLocator( resourceLocator );
    }
}
