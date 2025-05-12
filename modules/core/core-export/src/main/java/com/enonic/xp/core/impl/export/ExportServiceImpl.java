package com.enonic.xp.core.impl.export;

import java.nio.file.Path;
import java.util.Optional;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.export.writer.FileExportWriter;
import com.enonic.xp.export.ExportNodesParams;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.internal.InternalRepositoryService;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.server.VersionInfo;
import com.enonic.xp.vfs.VirtualFile;
import com.enonic.xp.vfs.VirtualFiles;

@Component(immediate = true)
@SuppressWarnings("UnusedDeclaration")
public class ExportServiceImpl
    implements ExportService
{
    private final String xpVersion;

    private final NodeService nodeService;

    private final ExportConfigurationDynamic exportConfiguration;

    private final InternalRepositoryService repositoryService;

    @Activate
    public ExportServiceImpl( @Reference final ExportConfigurationDynamic exportConfiguration, @Reference final NodeService nodeService,
                              @Reference final InternalRepositoryService repositoryService)
    {
        this.xpVersion = VersionInfo.get().getVersion();
        this.nodeService = nodeService;
        this.exportConfiguration = exportConfiguration;
        this.repositoryService = repositoryService;
    }

    @Override
    public NodeExportResult exportNodes( final ExportNodesParams params )
    {
        final Path targetDirectory = exportConfiguration.getExportsDir().resolve( params.getExportName() );

        return NodeExporter.create()
            .sourceNodePath( params.getSourceNodePath() )
            .nodeService( this.nodeService )
            .nodeExportWriter( new FileExportWriter() )
            .targetDirectory( targetDirectory )
            .xpVersion( xpVersion )
            .exportNodeIds( params.isIncludeNodeIds() )
            .exportVersions( params.isIncludeVersions() )
            .nodeExportListener( params.getNodeExportListener() )
            .build()
            .execute();
    }

    @Override
    public NodeImportResult importNodes( final ImportNodesParams params )
    {
        VirtualFile source = Optional.ofNullable( params.getSource() )
            .orElseGet( () -> VirtualFiles.from( exportConfiguration.getExportsDir().resolve( params.getExportName() ) ) );

        final NodeImportResult result = NodeImporter.create()
            .nodeService( this.nodeService )
            .sourceDirectory( source )
            .targetNodePath( params.getTargetNodePath() )
            .importNodeIds( params.isImportNodeids() )
            .importPermissions( params.isImportPermissions() )
            .xslt( params.getXslt() )
            .xsltParams( params.getXsltParams() )
            .nodeImportListener( params.getNodeImportListener() )
            .build()
            .execute();

        if ( targetIsSystemRepo() )
        {
            repositoryService.recreateMissing();
        }

        return result;
    }

    private boolean targetIsSystemRepo()
    {
        final RepositoryId repositoryId = ContextAccessor.current().getRepositoryId();
        final Branch branch = ContextAccessor.current().getBranch();
        return SystemConstants.SYSTEM_REPO_ID.equals( repositoryId ) && SystemConstants.BRANCH_SYSTEM.equals( branch );
    }
}
