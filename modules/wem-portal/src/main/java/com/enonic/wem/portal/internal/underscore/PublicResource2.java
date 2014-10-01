package com.enonic.wem.portal.internal.underscore;

import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.util.MediaTypes;
import com.enonic.wem.portal.internal.base.BaseResource2;

@Path("{mode}/{workspace}/{contentPath:.+}/_/public/{module}/{resource:.+}")
public final class PublicResource2
    extends BaseResource2
{
    protected ModuleService moduleService;

    protected ModuleKey moduleKey;

    @PathParam("resource")
    protected String resourceName;

    @PathParam("module")
    public void setModule( final String module )
    {
        this.moduleKey = ModuleKey.from( module );
    }

    @GET
    public Response handleGet()
        throws Exception
    {
        final Module module = this.moduleService.getModule( this.moduleKey );
        if ( module == null )
        {
            throw notFound( "Module [%s] not found", this.moduleKey );
        }

        final URL resourceUrl = module.getResource( "public/" + this.resourceName );
        if ( resourceUrl == null )
        {
            throw notFound( "File [%s] not found in module [%s]", this.resourceName, this.moduleKey.toString() );
        }

        final String type = MediaTypes.instance().fromFile( this.resourceName ).toString();
        return Response.ok().type( type ).entity( resourceUrl.openStream() ).build();
    }
}
