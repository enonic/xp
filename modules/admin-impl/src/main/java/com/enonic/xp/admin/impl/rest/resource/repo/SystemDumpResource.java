package com.enonic.xp.admin.impl.rest.resource.repo;

import java.nio.file.Paths;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.AdminResource;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.export.NodeExportResultJson;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.export.ExportNodesParams;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.home.HomeDir;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.RoleKeys;

@Path(ResourceConstants.REST_ROOT + "system")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true)
public class SystemDumpResource
    implements AdminResource
{
    private ExportService exportService;

    private java.nio.file.Path getDumpDirectory( final String name )
    {
        return Paths.get( HomeDir.get().toString(), "data", "dump", name ).toAbsolutePath();
    }

    @POST
    @Path("dump")
    public NodeExportResultJson dump( final SystemDumpRequestJson request )
        throws Exception
    {
        // TODO: Fix result and clean this shit up

        exportRepoBranch( "cms-repo", "draft", request.getName() );
        exportRepoBranch( "cms-repo", "master", request.getName() );
        return NodeExportResultJson.from( exportRepoBranch( "system-repo", "master", request.getName() ) );
    }

    private NodeExportResult exportRepoBranch( final String repoName, final String branch, final String dumpName )
    {
        final java.nio.file.Path rootDir = getDumpDirectory( dumpName );
        final java.nio.file.Path exportPath = rootDir.resolve( repoName ).resolve( branch );

        return getContext( branch, repoName ).callWith( () -> exportService.exportNodes( ExportNodesParams.create().
            includeNodeIds( true ).
            rootDirectory( rootDir.toString() ).
            targetDirectory( exportPath.toString() ).
            sourceNodePath( NodePath.ROOT ).
            build() ) );
    }

    private Context getContext( final String branchName, final String repositoryName )
    {
        return ContextBuilder.from( ContextAccessor.current() ).
            branch( Branch.from( branchName ) ).
            repositoryId( RepositoryId.from( repositoryName ) ).
            build();
    }

    @SuppressWarnings("UnusedDeclaration")
    @Reference
    public void setExportService( final ExportService exportService )
    {
        this.exportService = exportService;
    }
}
