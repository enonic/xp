package com.enonic.xp.portal.impl.resource.service;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.portal.impl.resource.base.BaseSubResource;

public final class ServiceResource
    extends BaseSubResource
{
    @Path("{module}/{service}")
    public ServiceControllerResource controller( @PathParam("module") final String module, @PathParam("service") final String service )
    {
        final ModuleKey moduleKey = ModuleKey.from( module );
        final ResourceKey scriptDir = ResourceKey.from( moduleKey, "cms/services/" + service );

        final ServiceControllerResource resource = initResource( new ServiceControllerResource() );
        resource.scriptDir = scriptDir;
        return resource;
    }
}
