package com.enonic.wem.portal.internal.v2;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;

import org.osgi.service.component.annotations.Component;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.xp.web.jaxrs.JaxRsComponent;

@Path("/v2")
@Component(immediate = true)
public final class PortalResource
    implements JaxRsComponent
{
    @Context
    protected ResourceContext resourceContext;

    @Path("{workspace}")
    public PageResource rootPage( @PathParam("workspace") final String workspace )
    {
        final PageResource resource = this.resourceContext.getResource( PageResource.class );
        resource.contentPath = ContentPath.from( "/" );
        resource.workspace = Workspace.from( workspace );
        resource.editMode = false;
        return resource;
    }
}
