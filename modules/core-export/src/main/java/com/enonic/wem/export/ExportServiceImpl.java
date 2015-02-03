package com.enonic.wem.export;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Strings;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.export.ExportNodesParams;
import com.enonic.wem.api.export.ExportService;
import com.enonic.wem.api.export.ImportNodesParams;
import com.enonic.wem.api.export.NodeExportResult;
import com.enonic.wem.api.export.NodeImportResult;
import com.enonic.wem.api.home.HomeDir;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.api.security.RoleKeys;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.export.internal.BatchedNodeExportCommand;
import com.enonic.wem.export.internal.NodeImportCommand;
import com.enonic.wem.export.internal.writer.FileExportWriter;
import com.enonic.wem.export.internal.xml.serializer.XmlNodeSerializer;

@Component(immediate = true)
public class ExportServiceImpl
    implements ExportService
{
    private static final String NODE_EXPORT_NAME_PREFIX = "node_";

    private static final String EXPORT_LOCATION_ROOT = "exports";

    private NodeService nodeService;

    private final XmlNodeSerializer xmlNodeSerializer = new XmlNodeSerializer();

    private final AuthenticationInfo EXPORT_AUTH_INFO = AuthenticationInfo.create().
        user( User.ANONYMOUS ).
        principals( RoleKeys.CONTENT_MANAGER ).
        build();

    private final Context EXPORT_CONTEXT = ContextBuilder.create().
        repositoryId( ContentConstants.CONTENT_REPO.getId() ).
        workspace( ContentConstants.WORKSPACE_DRAFT ).
        authInfo( EXPORT_AUTH_INFO ).
        build();

    @Override
    public NodeExportResult exportNodes( final ExportNodesParams params )
    {
        return EXPORT_CONTEXT.callWith( () -> BatchedNodeExportCommand.create().
            xmlNodeSerializer( xmlNodeSerializer ).
            exportRootNode( params.getExportRoot() ).
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            exportHomePath( Paths.get( HomeDir.get().toString(), EXPORT_LOCATION_ROOT ) ).
            exportName( Strings.isNullOrEmpty( params.getExportName() ) ? generateExportName() : params.getExportName() ).
            build().
            execute() );
    }

    private String generateExportName()
    {
        return NODE_EXPORT_NAME_PREFIX + LocalDateTime.now().format( DateTimeFormatter.ISO_LOCAL_DATE_TIME );
    }

    @Override
    public NodeImportResult importNodes( final ImportNodesParams params )
    {
        return EXPORT_CONTEXT.callWith( () -> NodeImportCommand.create().
            xmlNodeSerializer( this.xmlNodeSerializer ).
            nodeService( this.nodeService ).
            exportRoot( params.getSource() ).
            importRoot( params.getTargetPath() ).
            build().
            execute() );
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}