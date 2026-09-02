package com.enonic.xp.impl.server.rest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.dump.DumpService;
import com.enonic.xp.impl.server.rest.model.SystemDumpListJson;
import com.enonic.xp.impl.server.rest.model.SystemDumpRequestJson;
import com.enonic.xp.impl.server.rest.model.SystemDumpUpgradeRequestJson;
import com.enonic.xp.impl.server.rest.model.SystemLoadRequestJson;
import com.enonic.xp.impl.server.rest.model.TaskResultJson;
import com.enonic.xp.impl.server.rest.model.VacuumRequestJson;
import com.enonic.xp.impl.server.rest.task.SystemTasks;
import com.enonic.xp.impl.server.rest.task.VacuumCommand;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

@Path("/system")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class SystemResource
    implements JaxRsComponent
{
    private final DumpService dumpService;

    private final TaskService taskService;

    @Activate
    public SystemResource( @Reference final DumpService dumpService, @Reference final TaskService taskService )
    {
        this.dumpService = dumpService;
        this.taskService = taskService;
    }

    @GET
    @Path("dump")
    public SystemDumpListJson list()
    {
        return SystemDumpListJson.from( dumpService.list() );
    }

    @POST
    @Path("dump")
    public TaskResultJson dump( final SystemDumpRequestJson params )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "name", params.getName() );
        data.addBoolean( "includeVersions", params.isIncludeVersions() );
        if ( params.getMaxAge() != null )
        {
            data.addLong( "maxAge", params.getMaxAge().longValue() );
        }
        if ( params.getMaxVersions() != null )
        {
            data.addLong( "maxVersions", params.getMaxVersions().longValue() );
        }
        if ( params.getRepositories() != null )
        {
            data.addStrings( "repositories", params.getRepositories() );
        }
        return new TaskResultJson( taskService.submitTask( SubmitTaskParams.create().descriptorKey( SystemTasks.DUMP ).data( data ).build() ) );
    }

    @POST
    @Path("load")
    public TaskResultJson load( final SystemLoadRequestJson params )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "name", params.getName() );
        data.addBoolean( "upgrade", params.isUpgrade() );
        if ( params.getRepositories() != null )
        {
            data.addStrings( "repositories", params.getRepositories() );
        }
        return new TaskResultJson( taskService.submitTask( SubmitTaskParams.create().descriptorKey( SystemTasks.LOAD ).data( data ).build() ) );
    }

    @POST
    @Path("vacuum")
    public TaskResultJson vacuum( final VacuumRequestJson params )
    {
        final TaskId taskId = VacuumCommand.create()
            .ageThreshold( params.getAgeThreshold() )
            .tasks( params.getTasks() )
            .taskService( taskService )
            .build()
            .execute();
        return new TaskResultJson( taskId );
    }

    @POST
    @Path("upgrade")
    public TaskResultJson upgrade( final SystemDumpUpgradeRequestJson params )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "name", params.getName() );
        return new TaskResultJson( taskService.submitTask( SubmitTaskParams.create().descriptorKey( SystemTasks.UPGRADE ).data( data ).build() ) );
    }
}
