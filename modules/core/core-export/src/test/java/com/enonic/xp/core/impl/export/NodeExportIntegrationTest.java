package com.enonic.xp.core.impl.export;

import java.io.File;
import java.nio.file.Path;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.core.impl.export.writer.FileExportWriter;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RenameNodeParams;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.util.BinaryReference;

import static com.enonic.xp.core.impl.export.writer.NodeExportPathResolver.BINARY_FOLDER;
import static com.enonic.xp.core.impl.export.writer.NodeExportPathResolver.NODE_XML_EXPORT_NAME;
import static com.enonic.xp.core.impl.export.writer.NodeExportPathResolver.SYSTEM_FOLDER_NAME;
import static com.enonic.xp.core.impl.export.writer.NodeExportPathResolver.VERSION_FOLDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeExportIntegrationTest
    extends AbstractNodeTest
{
    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    public void single_node()
        throws Exception
    {
        final Node myNode = createNode( NodePath.ROOT, "myNode" );

        final NodeExportResult result = doExportRoot( false );

        assertEquals( 2, result.size() );
        assertExported( myNode );
    }

    @Test
    public void single_node_with_binary()
        throws Exception
    {
        final BinaryReference binaryRef = BinaryReference.from( "myFile" );
        final PropertyTree data = new PropertyTree();
        data.addBinaryReference( "myBinary", binaryRef );

        final Node myNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "myNode" ).
            data( data ).
            attachBinary( binaryRef, ByteSource.wrap( "this is a binary file".getBytes() ) ).
            build() );

        final NodeExportResult result = doExportRoot( false );

        assertEquals( 2, result.size() );
        assertEquals( 1, result.getExportedBinaries().size() );
        assertExported( myNode );
        assertBinaryExported( myNode, binaryRef );
    }

    @Test
    public void single_node_multiple_versions()
        throws Exception
    {
        final Node node = createNode( NodePath.ROOT, "myNode" );
        final Node updatedNode = updateNode( UpdateNodeParams.create().
            id( node.id() ).
            editor( ( n ) -> n.data.addInstant( "myInstant", Instant.now() ) ).
            build() );

        final NodeExportResult result = doExportRoot( true );

        assertEquals( 2, result.size() );

        printPaths();

        assertExported( updatedNode );
        assertVersionExported( updatedNode, node );
    }

    @Test
    public void single_node_changed_name()
        throws Exception
    {
        final Node originalNode = createNode( NodePath.ROOT, "initial-name" );

        final Node renamedNode = this.nodeService.rename( RenameNodeParams.create().
            nodeId( originalNode.id() ).
            nodeName( NodeName.from( "new-node-name" ) ).
            build() );

        final NodeExportResult result = doExportRoot( true );

        assertEquals( 2, result.size() );

        printPaths();
        assertExported( renamedNode );
        assertVersionExported( renamedNode, originalNode );
    }

    @Test
    public void single_node_with_binary_changed()
        throws Exception
    {
        final BinaryReference binaryRef = BinaryReference.from( "myFile" );
        final BinaryReference binaryRefUpdated = BinaryReference.from( "myOtherFile" );

        final PropertyTree data = new PropertyTree();
        data.addBinaryReference( "myBinary", binaryRef );

        final Node myNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "myNode" ).
            data( data ).
            attachBinary( binaryRef, ByteSource.wrap( "this is a binary file".getBytes() ) ).
            build() );

        final Node updatedNode = updateNode( UpdateNodeParams.create().
            id( myNode.id() ).
            editor( ( n ) -> n.data.setBinaryReference( "myBinary", binaryRefUpdated ) ).
            attachBinary( binaryRefUpdated, ByteSource.wrap( "this is another binary file".getBytes() ) ).
            build() );

        final NodeExportResult result = doExportRoot( true );

        printPaths();

        assertEquals( 2, result.size() );
        assertExported( myNode );
        assertBinaryExported( updatedNode, binaryRefUpdated );
        assertVersionBinaryExported( updatedNode, myNode, binaryRef );
    }

    // Asserts and Utils

    private NodeExportResult doExportRoot( final boolean exportVersions )
    {
        return NodeExporter.create().
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            sourceNodePath( NodePath.ROOT ).
            targetDirectory( this.temporaryFolder.resolve( "myExport" ) ).
            exportVersions( exportVersions ).
            build().
            execute();
    }

    private void assertExported( final Node node )
    {
        assertThat( getBaseFolder( node ).resolve( NODE_XML_EXPORT_NAME ) ).exists();
    }

    private void assertVersionExported( final Node exportedNode, final Node exportedVersion )
    {
        assertThat( getBaseFolder( exportedNode ).
            resolve( VERSION_FOLDER ).
            resolve( exportedVersion.getNodeVersionId().toString() ).
            resolve( exportedVersion.name().toString() ).resolve( NODE_XML_EXPORT_NAME ) ).exists();
    }

    private void assertBinaryExported( final Node node, final BinaryReference ref )
    {
        final Path baseFolder = getBaseFolder( node );
        assertThat( baseFolder.resolve( BINARY_FOLDER ).resolve( ref.toString() ) ).exists();
    }

    private void assertVersionBinaryExported( final Node exportedNode, final Node exportedVersion, final BinaryReference ref )
    {
        assertThat( getBaseFolder( exportedNode ).
            resolve( VERSION_FOLDER ).
            resolve( exportedVersion.getNodeVersionId().toString() ).
            resolve( exportedVersion.name().toString() ).resolve( BINARY_FOLDER ).resolve( ref.toString() ) ).exists();
    }

    private Path getBaseFolder( final Node node )
    {
        return temporaryFolder.resolve( "myExport" ).resolve( node.path().toString().substring( 1 ) ).resolve( SYSTEM_FOLDER_NAME );
    }

    private void printPaths()
    {
        final File file = this.temporaryFolder.toFile();

        doPrintPaths( file );
    }

    private void doPrintPaths( final File file )
    {
        if ( file.isDirectory() )
        {
            final File[] children = file.listFiles();

            for ( final File child : children )
            {
                doPrintPaths( child );
            }
        }
        else
        {
            System.out.println( file.toPath() );
        }
    }

}
