package com.enonic.xp.core.impl.export;

import java.nio.file.Paths;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.core.impl.export.writer.FileExportWriter;
import com.enonic.xp.export.ExportNodesParams;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.node.NodeService;

@Component(immediate = true)
@SuppressWarnings("UnusedDeclaration")
public class ExportServiceImpl
    implements ExportService
{
    private NodeService nodeService;

    @Override
    public NodeExportResult exportNodes( final ExportNodesParams params )
    {
        return BatchedNodeExportCommand.create().
            sourceNodePath( params.getSourceNodePath() ).
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            targetDirectory( Paths.get( params.getTargetDirectory() ) ).
            dryRun( params.isDryRun() ).
            exportNodeIds( params.isIncludeNodeIds() ).
            build().
            execute();
    }

    @Override
    public NodeImportResult importNodes( final ImportNodesParams params )
    {
        return NodeImporter.create().
            nodeService( this.nodeService ).
            sourceDirectory( params.getSource() ).
            targetNodePath( params.getTargetNodePath() ).
            dryRun( params.isDryRun() ).
            importNodeIds( params.isImportNodeids() ).
            build().
            execute();
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}