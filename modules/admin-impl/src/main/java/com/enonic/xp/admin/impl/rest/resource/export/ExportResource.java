package com.enonic.xp.admin.impl.rest.resource.export;

import java.nio.file.Paths;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.AdminResource;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.export.ExportNodesParams;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.home.HomeDir;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.vfs.VirtualFiles;

@Path(ResourceConstants.REST_ROOT + "export")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true)
public class ExportResource
    implements AdminResource
{
    private ExportService exportService;

    private java.nio.file.Path getExportDirectory( final String exportName )
    {
        return Paths.get( HomeDir.get().toString(), "data", "export", exportName ).toAbsolutePath();
    }

    @POST
    @Path("export")
    public NodeExportResultJson exportNodes( final ExportNodesRequestJson request )
        throws Exception
    {
        final NodeExportResult result =
            getContext( request.getSourceRepoPath() ).callWith( () -> this.exportService.exportNodes( ExportNodesParams.create().
                sourceNodePath( request.getSourceRepoPath().getNodePath() ).
                targetDirectory( getExportDirectory( request.getExportName() ).toString() ).
                dryRun( request.isDryRun() ).
                includeNodeIds( request.isExportWithIds() ).
                build() ) );

        return NodeExportResultJson.from( result );
    }

    @POST
    @Path("import")
    public NodeImportResultJson importNodes( final ImportNodesRequestJson request )
        throws Exception
    {
        final NodeImportResult result =
            getContext( request.getTargetRepoPath() ).callWith( () -> this.exportService.importNodes( ImportNodesParams.create().
                source( VirtualFiles.from( getExportDirectory( request.getExportName() ) ) ).
                targetNodePath( request.getTargetRepoPath().getNodePath() ).
                dryRun( request.isDryRun() ).
                includeNodeIds( request.isImportWithIds() ).
                includePermissions( request.isImportWithPermissions() ).
                build() ) );

        return NodeImportResultJson.from( result );
    }

    private Context getContext( final RepoPath repoPath )
    {
        return ContextBuilder.from( ContextAccessor.current() ).
            branch( repoPath.getBranch() ).
            repositoryId( repoPath.getRepositoryId() ).
            build();
    }

    @SuppressWarnings("UnusedDeclaration")
    @Reference
    public void setExportService( final ExportService exportService )
    {
        this.exportService = exportService;
    }
}

