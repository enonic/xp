package com.enonic.xp.core.impl.export;

import java.nio.file.Path;
import java.util.Optional;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.core.impl.export.writer.FileExportWriter;
import com.enonic.xp.export.ExportNodesParams;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.node.NodeService;
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

    private final Path exportsDir;

    @Activate
    public ExportServiceImpl( @Reference final ExportConfiguration exportConfiguration,
                              @Reference final NodeService nodeService )
    {
        this.xpVersion = VersionInfo.get().getVersion();
        this.exportsDir = exportConfiguration.getExportsDir();
        this.nodeService = nodeService;
    }

    @Override
    public NodeExportResult exportNodes( final ExportNodesParams params )
    {
        final Path targetDirectory = Optional.ofNullable( params.getTargetDirectory() )
            .map( Path::of )
            .orElseGet( () -> exportsDir.resolve( params.getExportName() ) );

        final Path rootDirectory = Optional.ofNullable( params.getRootDirectory() ).map( Path::of ).orElse( targetDirectory );

        return NodeExporter.create()
            .sourceNodePath( params.getSourceNodePath() )
            .nodeService( this.nodeService )
            .nodeExportWriter( new FileExportWriter() )
            .rootDirectory( rootDirectory )
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
        VirtualFile source =
            Optional.ofNullable( params.getSource() ).orElseGet( () -> VirtualFiles.from( exportsDir.resolve( params.getExportName() ) ) );

        return NodeImporter.create()
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
    }
}
