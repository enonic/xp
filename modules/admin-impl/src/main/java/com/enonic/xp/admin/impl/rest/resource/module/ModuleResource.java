package com.enonic.xp.admin.impl.rest.resource.module;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.AdminResource;
import com.enonic.xp.admin.impl.json.module.ModuleJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.module.json.ListModuleJson;
import com.enonic.xp.admin.impl.rest.resource.module.json.ModuleInstallParams;
import com.enonic.xp.admin.impl.rest.resource.module.json.ModuleListParams;
import com.enonic.xp.admin.impl.rest.resource.module.json.ModuleSuccessJson;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.module.Modules;
import com.enonic.xp.security.RoleKeys;

@Path(ResourceConstants.REST_ROOT + "module")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public final class ModuleResource
    implements AdminResource
{
    private ModuleService moduleService;

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

    @POST
    @Path("start")
    @Consumes(MediaType.APPLICATION_JSON)
    public ModuleSuccessJson start( final ModuleListParams params )
        throws Exception
    {
        params.getModuleKeys().forEach( this.moduleService::startModule );
        return new ModuleSuccessJson();
    }

    @POST
    @Path("stop")
    @Consumes(MediaType.APPLICATION_JSON)
    public ModuleSuccessJson stop( final ModuleListParams params )
        throws Exception
    {
        params.getModuleKeys().forEach( this.moduleService::stopModule );
        return new ModuleSuccessJson();
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public ModuleSuccessJson update( final ModuleListParams params )
        throws Exception
    {
        params.getModuleKeys().forEach( this.moduleService::updateModule );
        return new ModuleSuccessJson();
    }

    @POST
    @Path("uninstall")
    @Consumes(MediaType.APPLICATION_JSON)
    public ModuleSuccessJson uninstall( final ModuleListParams params )
        throws Exception
    {
        params.getModuleKeys().forEach( this.moduleService::uninstallModule );
        return new ModuleSuccessJson();
    }

    @POST
    @Path("install")
    @Consumes(MediaType.APPLICATION_JSON)
    public ModuleSuccessJson install( final ModuleInstallParams params )
        throws Exception
    {
        this.moduleService.installModule( params.getUrl() );
        return new ModuleSuccessJson();
    }

    @Reference
    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }
}
