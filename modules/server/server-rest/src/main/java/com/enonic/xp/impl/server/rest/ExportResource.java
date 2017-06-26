package com.enonic.xp.impl.server.rest;

import java.nio.file.Paths;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.export.ExportNodesParams;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.home.HomeDir;
import com.enonic.xp.impl.server.rest.model.ExportNodesRequestJson;
import com.enonic.xp.impl.server.rest.model.ImportNodesRequestJson;
import com.enonic.xp.impl.server.rest.model.NodeExportResultJson;
import com.enonic.xp.impl.server.rest.model.NodeImportResultJson;
import com.enonic.xp.impl.server.rest.model.RepoPath;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.vfs.VirtualFile;
import com.enonic.xp.vfs.VirtualFiles;

@Path("/api/repo")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class ExportResource
    implements JaxRsComponent
{
    private ExportService exportService;

    private RepositoryService repositoryService;

    private NodeRepositoryService nodeRepositoryService;

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
                includeVersions( request.isIncludeVersions() ).
                build() ) );

        return NodeExportResultJson.from( result );
    }

    @POST
    @Path("import")
    public NodeImportResultJson importNodes( final ImportNodesRequestJson request )
        throws Exception
    {
        final String xsl = request.getXslSource();
        final VirtualFile xsltFile = xsl != null ? VirtualFiles.from( getExportDirectory( xsl ) ) : null;
        final RepoPath targetRepoPath = request.getTargetRepoPath();

        final NodeImportResult result =
            getContext( request.getTargetRepoPath() ).callWith( () -> this.exportService.importNodes( ImportNodesParams.create().
                source( VirtualFiles.from( getExportDirectory( request.getExportName() ) ) ).
                targetNodePath( targetRepoPath.getNodePath() ).
                dryRun( request.isDryRun() ).
                includeNodeIds( request.isImportWithIds() ).
                includePermissions( request.isImportWithPermissions() ).
                xslt( xsltFile ).
                xsltParams( request.getXslParams() ).
                build() ) );

        if ( targetIsSystemRepo( targetRepoPath ) )
        {
            initializeStoredRepositories();
        }

        return NodeImportResultJson.from( result );
    }

    private boolean targetIsSystemRepo( final RepoPath targetRepoPath )
    {
        return SystemConstants.SYSTEM_REPO.getId().equals( targetRepoPath.getRepositoryId() ) &&
            SystemConstants.BRANCH_SYSTEM.equals( targetRepoPath.getBranch() );
    }

    private void initializeStoredRepositories()
    {
        this.repositoryService.invalidateAll();
        for ( Repository repository : repositoryService.list() )
        {
            if ( !this.nodeRepositoryService.isInitialized( repository.getId() ) )
            {
                final CreateRepositoryParams createRepositoryParams = CreateRepositoryParams.create().
                    repositoryId( repository.getId() ).
                    repositorySettings( repository.getSettings() ).
                    build();
                this.nodeRepositoryService.create( createRepositoryParams );
            }
        }
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

    @Reference
    public void setRepositoryService( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }

    @Reference
    public void setNodeRepositoryService( final NodeRepositoryService nodeRepositoryService )
    {
        this.nodeRepositoryService = nodeRepositoryService;
    }
}
