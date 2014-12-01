package com.enonic.wem.export.internal;

import java.nio.file.Path;
import java.util.stream.Stream;

import com.enonic.wem.api.export.NodeImportResult;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.export.internal.builder.NodeXmlBuilder;
import com.enonic.wem.export.internal.reader.ExportReader;
import com.enonic.wem.export.internal.writer.NodeExportPathResolver;
import com.enonic.wem.export.internal.xml.XmlNode;
import com.enonic.wem.export.internal.xml.serializer.XmlNodeSerializer;

public class NodeImporter
{
    private final NodePath importRoot;

    private final NodeService nodeService;

    private final ExportReader exportReader;

    private final XmlNodeSerializer xmlNodeSerializer;

    private final Path exportRootPath;


    private NodeImporter( final Builder builder )
    {
        this.nodeService = builder.nodeService;
        this.exportReader = builder.exportReader;
        this.exportRootPath = NodeExportPathResolver.resolveExportRoot( builder.exportHome, builder.exportName );
        this.xmlNodeSerializer = builder.xmlNodeSerializer;
        this.importRoot = builder.importRoot;
    }

    public NodeImportResult execute()
    {
        // Get & check export root directory
        // Read node, deserialize and create
        // Get children, for each:
        //  - Read node, deserialize and create
        // If node.childOrder.isManual:
        //  - synchronize order with content in manualChildOrder.txt

        doImport( this.exportRootPath );

        return new NodeImportResult();
    }

    private Stream<Path> getChildPaths( final Path path )
    {
        return this.exportReader.getChildrenPaths( path );
    }

    private void doImport( final Path path )
    {
        final Stream<Path> children = getChildPaths( path );

        children.forEach( ( child ) -> {

            final String serializedNode = this.exportReader.getItem( child );

            final XmlNode xmlNode = this.xmlNodeSerializer.parse( serializedNode );

            // Check if exists

            final CreateNodeParams createNodeParams = NodeXmlBuilder.build( xmlNode );

            final Node createdNode = this.nodeService.create( createNodeParams );

            doImport( child );
        } );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodePath importRoot;

        private NodeService nodeService;

        private ExportReader exportReader;

        private XmlNodeSerializer xmlNodeSerializer;

        private Path exportHome;

        private String exportName;

        private Builder()
        {
        }

        public Builder importRoot( NodePath nodePath )
        {
            this.importRoot = nodePath;
            return this;
        }

        public Builder exportHome( Path basePath )
        {
            this.exportHome = basePath;
            return this;
        }

        public Builder exportName( final String exportName )
        {
            this.exportName = exportName;
            return this;
        }

        public Builder nodeService( NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        public Builder exportReader( ExportReader exportReader )
        {
            this.exportReader = exportReader;
            return this;
        }

        public Builder xmlNodeSerializer( XmlNodeSerializer xmlNodeSerializer )
        {
            this.xmlNodeSerializer = xmlNodeSerializer;
            return this;
        }

        public NodeImporter build()
        {
            return new NodeImporter( this );
        }
    }

}

