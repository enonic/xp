package com.enonic.xp.core.export;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.impl.export.NodeExporter;
import com.enonic.xp.core.impl.export.NodeImporter;
import com.enonic.xp.core.impl.export.reader.ZipVirtualFile;
import com.enonic.xp.core.impl.export.writer.ZipExportWriter;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.SortNodeParams;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.repo.impl.node.SortNodeCommand;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderKeyExportImportTest
    extends AbstractNodeTest
{
    @TempDir
    Path temporaryFolder;

    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void round_trip_preserves_order_and_mints_fresh_keys()
        throws Exception
    {
        final Node parent = createNode( NodePath.ROOT, "parent" );
        createNode( parent.path(), "child-a" );
        createNode( parent.path(), "child-b" );
        createNode( parent.path(), "child-c" );

        // a keyless child, stored the way a version pushed from a branch that predates order keys arrives
        storeKeyless( parent.path(), "legacy-child" );

        SortNodeCommand.create()
            .params( SortNodeParams.create().nodeId( parent.id() ).childOrder( ChildOrder.orderKeyOrder() ).build() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();
        refresh();

        final Map<String, String> sourceOrder = childOrderSnapshot( parent.path() );
        assertEquals( "legacy-child", List.copyOf( sourceOrder.keySet() ).get( 3 ), "keyless child must close the source order" );

        final Path exportDir = Files.createDirectories( temporaryFolder.resolve( "export" ) );
        try (ZipExportWriter writer = ZipExportWriter.create( exportDir, "order-key-export" ))
        {
            NodeHelper.runAsAdmin( NodeExporter.create()
                                       .nodeService( this.nodeService )
                                       .nodeExportWriter( writer )
                                       .sourceNodePath( parent.path() )
                                       .targetDirectory( exportDir.resolve( "order-key-export" ) )
                                       .xpVersion( "1.0.0" )
                                       .build()::execute );
        }

        nodeService.delete( DeleteNodeParams.create().nodeId( parent.id() ).refresh( RefreshMode.ALL ).build() );

        final NodeImportResult importResult = NodeHelper.runAsAdmin( () -> NodeImporter.create()
            .nodeService( this.nodeService )
            .sourceDirectory( ZipVirtualFile.from( exportDir.resolve( "order-key-export.zip" ) ) )
            .targetNodePath( NodePath.ROOT )
            .build()
            .execute() );
        assertThat( importResult.getImportErrors() ).isEmpty();
        refresh();

        final Node importedParent = this.nodeService.getByPath( parent.path() );
        assertEquals( ChildOrder.orderKeyOrder(), importedParent.getChildOrder() );

        final Map<String, String> importedOrder = childOrderSnapshot( parent.path() );
        assertThat( importedOrder.keySet() ).containsExactlyElementsOf( sourceOrder.keySet() );

        for ( final Map.Entry<String, String> entry : importedOrder.entrySet() )
        {
            assertNotNull( entry.getValue(), "every imported child must hold a key, the once keyless one included" );
            assertNotEquals( sourceOrder.get( entry.getKey() ), entry.getValue(),
                             "keys are minted at import, not copied from the export" );
        }
    }

    private Map<String, String> childOrderSnapshot( final NodePath parentPath )
    {
        final Map<String, String> order = new LinkedHashMap<>();
        for ( final NodeId id : findByParent( parentPath ).getNodeIds() )
        {
            final Node node = this.nodeService.getById( id );
            order.put( node.name().toString(), node.getOrderKey() );
        }
        return order;
    }

    private void storeKeyless( final NodePath parent, final String name )
    {
        final Node node = Node.create()
            .id( NodeId.from( name ) )
            .parentPath( parent )
            .name( name )
            .timestamp( java.time.Instant.ofEpochSecond( 1000 ) )
            .permissions( AccessControlList.of(
                AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().build() ) )
            .build();
        this.storageService.store( StoreNodeParams.newVersion( node ), InternalContext.from( ContextAccessor.current() ) );
    }
}
