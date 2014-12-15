package com.enonic.wem.export;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.export.ExportService;
import com.enonic.wem.api.export.NodeExportResult;
import com.enonic.wem.api.export.NodeImportResult;
import com.enonic.wem.api.home.HomeDir;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.export.internal.BatchedNodeExportCommand;
import com.enonic.wem.export.internal.NodeImportCommand;
import com.enonic.wem.export.internal.reader.FileExportReader;
import com.enonic.wem.export.internal.writer.FileExportWriter;
import com.enonic.wem.export.internal.xml.serializer.XmlNodeSerializer;

@Component(immediate = true)
public class ExportServiceImpl
    implements ExportService
{
    private NodeService nodeService;

    private final XmlNodeSerializer xmlNodeSerializer = new XmlNodeSerializer();

    @Override
    public NodeExportResult exportNodes( final NodePath nodePath )
    {
        return BatchedNodeExportCommand.create().
            xmlNodeSerializer( xmlNodeSerializer ).
            exportRootNode( nodePath ).
            nodeService( this.nodeService ).

            nodeExportWriter( new FileExportWriter() ).
            exportHomePath( Paths.get( HomeDir.get().toString(), "/exports" ) ).
            exportName( "node_" + LocalDateTime.now().format( DateTimeFormatter.ISO_LOCAL_DATE_TIME ) ).
            build().
            execute();
    }

    @Override
    public NodeImportResult importNodes( final String exportName, final NodePath importRootPath )
    {

        return NodeImportCommand.create().
            xmlNodeSerializer( this.xmlNodeSerializer ).
            nodeService( this.nodeService ).
            exportReader( new FileExportReader() ).
            exportHome( Paths.get( HomeDir.get().toString(), "/exports" ) ).
            exportName( exportName ).
            importRoot( importRootPath ).
            build().
            execute();
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}