package com.enonic.wem.admin.rest.resource.export;

import java.nio.file.Paths;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.admin.AdminResource;
import com.enonic.wem.admin.rest.resource.ResourceConstants;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.export.ExportNodesParams;
import com.enonic.wem.api.export.ExportService;
import com.enonic.wem.api.export.ImportNodesParams;
import com.enonic.wem.api.export.NodeExportResult;
import com.enonic.wem.api.export.NodeImportResult;
import com.enonic.wem.api.security.RoleKeys;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.vfs.VirtualFiles;

@Path(ResourceConstants.REST_ROOT + "export")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public class ExportResource
    implements AdminResource
{
    private final AuthenticationInfo EXPORT_AUTH_INFO = AuthenticationInfo.create().
        user( User.ANONYMOUS ).
        principals( RoleKeys.CONTENT_MANAGER_ADMIN ).
        build();

    private ExportService exportService;

    private Logger LOG = LoggerFactory.getLogger( ExportResource.class );

    @POST
    @Path("export")
    public NodeExportResultJson exportNodes( final ExportNodesRequestJson request )
        throws Exception
    {

        final Context runContext = ContextBuilder.create().
            authInfo( EXPORT_AUTH_INFO ).
            branch( request.getSourceRepoPath().getBranch() ).
            repositoryId( request.getSourceRepoPath().getRepositoryId() ).
            build();

        final NodeExportResult result = runContext.callWith( () -> this.exportService.exportNodes( ExportNodesParams.create().
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
        final NodeImportResult result = this.exportService.importNodes( ImportNodesParams.create().
            targetNodePath( request.getTargetNodePath() ).
            source( VirtualFiles.from( Paths.get( request.getExportFilePath() ) ) ).
            dryRun( request.isDryRun() ).
            includeNodeIds( request.isImportWithIds() ).
            build() );

        return NodeImportResultJson.from( result );
    }

    @SuppressWarnings("UnusedDeclaration")
    @Reference
    public void setExportService( final ExportService exportService )
    {
        this.exportService = exportService;
    }
}

