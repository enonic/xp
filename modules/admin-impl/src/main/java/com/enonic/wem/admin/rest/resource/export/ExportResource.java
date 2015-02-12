package com.enonic.wem.admin.rest.resource.export;

import java.nio.file.Paths;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.admin.AdminResource;
import com.enonic.wem.admin.rest.resource.ResourceConstants;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.export.ExportNodesParams;
import com.enonic.wem.api.export.ExportService;
import com.enonic.wem.api.export.ImportNodesParams;
import com.enonic.wem.api.export.NodeExportResult;
import com.enonic.wem.api.export.NodeImportResult;
import com.enonic.wem.api.security.RoleKeys;
import com.enonic.wem.api.vfs.VirtualFiles;

@Path(ResourceConstants.REST_ROOT + "export")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public class ExportResource
    implements AdminResource
{
    private ExportService exportService;

    @POST
    @Path("export")
    public NodeExportResultJson exportNodes( final ExportNodesRequestJson request )
        throws Exception
    {
        final NodeExportResult result =
            getContext( request.getSourceRepoPath() ).callWith( () -> this.exportService.exportNodes( ExportNodesParams.create().
                sourceNodePath( request.getSourceRepoPath().getNodePath() ).
                targetDirectory( request.getTargetDirectory() ).
                dryRun( request.isDryRun() ).
                includeNodeIds( request.isIncludeIds() ).
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
                source( VirtualFiles.from( Paths.get( request.getSourceDirectory() ) ) ).
                targetNodePath( request.getTargetRepoPath().getNodePath() ).
                dryRun( request.isDryRun() ).
                includeNodeIds( request.isImportWithIds() ).
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

