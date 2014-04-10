package com.enonic.wem.admin.rest.resource.module;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.module.ModuleJson;
import com.enonic.wem.admin.rest.resource.module.json.ListModuleJson;
import com.enonic.wem.admin.rest.resource.module.json.ModuleDeleteParams;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;

@javax.ws.rs.Path("module")
@Produces(MediaType.APPLICATION_JSON)
public final class ModuleResource
{
    @Inject
    protected ModuleService moduleService;

    @GET
    @javax.ws.rs.Path("list")
    public ListModuleJson list()
    {
        final Modules modules = this.moduleService.getAllModules();
        return new ListModuleJson( modules );
    }

    @POST
    @javax.ws.rs.Path("delete")
    public ModuleJson delete( ModuleDeleteParams params )
    {
        final Module deleted = this.moduleService.deleteModule( params.getModuleKey() );
        return new ModuleJson( deleted );
    }

    @GET
    public ModuleJson getByKey( @QueryParam("moduleKey") String moduleKey )
    {
        final Module module = this.moduleService.getModule( ModuleKey.from( moduleKey ) );
        return new ModuleJson( module );
    }
}
