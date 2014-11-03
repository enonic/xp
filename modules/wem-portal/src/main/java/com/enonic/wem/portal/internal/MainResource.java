package com.enonic.wem.portal.internal;

import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;

import com.enonic.wem.portal.internal.base.ResourceFactory;
import com.enonic.wem.portal.internal.base.ResourceFactoryMap;
import com.enonic.wem.portal.internal.content.ComponentResource;
import com.enonic.wem.portal.internal.content.ContentResource;
import com.enonic.wem.portal.internal.underscore.ImageResource;
import com.enonic.wem.portal.internal.underscore.PublicResource;
import com.enonic.wem.portal.internal.underscore.ServiceResource;
import com.enonic.wem.servlet.jaxrs.JaxRsComponent;

@Path("/portal/{mode}/{workspace}")
public final class MainResource
    implements JaxRsComponent
{
    @Context
    private ResourceContext resourceContext;

    private ResourceFactoryMap factoryMap;

    @Path("{contentPath:.+}")
    public ContentResource contentResource()
    {
        return newResource( ContentResource.class );
    }

    @Path("{contentPath:.+}/_/image")
    public ImageResource imageResource()
    {
        return newResource( ImageResource.class );
    }

    @Path("{contentPath:.+}/_/public/{module}/{resource:.+}")
    public PublicResource publicResource()
    {
        return newResource( PublicResource.class );
    }

    @Path("{contentPath:.+}/_/service/{module}/{service}")
    public ServiceResource serviceResource()
    {
        return newResource( ServiceResource.class );
    }

    @Path("{contentPath:.+}/_/component/{component:.+}")
    public ComponentResource componentResource()
    {
        return newResource( ComponentResource.class );
    }

    private <T> T newResource( final Class<T> type )
    {
        final T resource = this.factoryMap.newResource( type );
        return resource != null ? this.resourceContext.initResource( resource ) : null;
    }

    public void setFactories( final List<ResourceFactory> factories )
    {
        this.factoryMap = new ResourceFactoryMap( factories );
    }
}
