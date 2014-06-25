package com.enonic.wem.admin.rest.resource.module;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.osgi.framework.BundleContext;

import com.enonic.wem.admin.json.module.ModuleJson;
import com.enonic.wem.admin.rest.resource.module.json.ListModuleJson;
import com.enonic.wem.admin.rest.resource.module.json.ModuleListParams;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;

@Path("module")
@Produces(MediaType.APPLICATION_JSON)
public final class ModuleResource
{
    @Inject
    protected ModuleService moduleService;

    @Inject
    protected BundleContext bundleContext;

    @GET
    @Path("list")
    public ListModuleJson list()
    {
        final Modules modules = this.moduleService.getAllModules();
        return new ListModuleJson( modules );
    }

    @GET
    public ModuleJson getByKey( @QueryParam("moduleKey") String moduleKey )
    {
        final Module module = this.moduleService.getModule( ModuleKey.from( moduleKey ) );
        return new ModuleJson( module );
    }

    // @POST
    public void start( final ModuleListParams params )
        throws Exception
    {
        for ( final ModuleKey key : params.getModuleKeys() )
        {
            this.moduleService.getModule( key ).getBundle().start();
        }
    }

    // @POST
    public void stop( final ModuleListParams params )
        throws Exception
    {
        for ( final ModuleKey key : params.getModuleKeys() )
        {
            this.moduleService.getModule( key ).getBundle().stop();
        }
    }

    // @POST
    public void update( final ModuleListParams params )
        throws Exception
    {
        for ( final ModuleKey key : params.getModuleKeys() )
        {
            this.moduleService.getModule( key ).getBundle().update();
        }
    }

    // @POST
    public void install( final String url )
        throws Exception
    {
        this.bundleContext.installBundle( url );
    }
}
