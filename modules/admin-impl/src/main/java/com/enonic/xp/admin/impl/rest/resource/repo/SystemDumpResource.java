package com.enonic.xp.admin.impl.rest.resource.repo;

import java.nio.file.Paths;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Lists;

import com.enonic.xp.admin.AdminResource;
import com.enonic.xp.admin.impl.rest.resource.export.NodeExportResultsJson;
import com.enonic.xp.admin.impl.rest.resource.export.NodeImportResultsJson;
import com.enonic.xp.admin.rest.resource.ResourceConstants;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.export.ExportNodesParams;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.home.HomeDir;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.vfs.VirtualFiles;

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

    private java.nio.file.Path getDataHome()
    {
        return Paths.get( HomeDir.get().toString(), "data" );
    }

    @POST
    @Path("dump")
    public NodeExportResultsJson dump( final SystemDumpRequestJson request )
        throws Exception
    {
        final List<NodeExportResult> results = Lists.newArrayList();

        results.add( exportRepoBranch( "cms-repo", "draft", request.getName() ) );
        results.add( exportRepoBranch( "cms-repo", "master", request.getName() ) );
        results.add( exportRepoBranch( "system-repo", "master", request.getName() ) );

        return NodeExportResultsJson.from( results );
    }

    @POST
    @Path("load")
    public NodeImportResultsJson load( final SystemLoadRequestJson request )
    {
        final List<NodeImportResult> results = Lists.newArrayList();

        results.add( importRepoBranch( "cms-repo", "draft", request.getName() ) );
        results.add( importRepoBranch( "cms-repo", "master", request.getName() ) );
        results.add( importRepoBranch( "system-repo", "master", request.getName() ) );

        return NodeImportResultsJson.from( results );
    }

    private NodeImportResult importRepoBranch( final String repoName, final String branch, final String dumpName )
    {
        final java.nio.file.Path rootDir = getDumpDirectory( dumpName );

        if ( !rootDir.toFile().exists() )
        {
            throw new IllegalArgumentException( "No dump with name '" + dumpName + "' found in " + getDataHome() );
        }

        final java.nio.file.Path importPath = rootDir.resolve( repoName ).resolve( branch );

        return getContext( branch, repoName ).callWith( () -> this.exportService.importNodes( ImportNodesParams.create().
            source( VirtualFiles.from( importPath ) ).
            targetNodePath( NodePath.ROOT ).
            includeNodeIds( true ).
            includePermissions( true ).
            build() ) );
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


