package com.enonic.xp.core.node;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.ImportNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCompareStatus;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.node.CompareNodeCommand;
import com.enonic.xp.repo.impl.node.ImportNodeCommand;
import com.enonic.xp.repo.impl.repository.RepositorySettings;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImportNodeCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        ctxDefault().callWith( this::createDefaultRootNode );
        ctxOther().callWith( this::createDefaultRootNode );
    }

    @Test
    void no_timestamp()
    {
        final Node importNode = Node.create().name( "myNode" ).parentPath( NodePath.ROOT ).data( new PropertyTree() ).build();

        final ImportNodeResult importNodeResult = importNode( importNode );

        assertNotNull( importNodeResult.getNode().getTimestamp() );
    }

    @Test
    void imported_timestamp_is_not_used_as_version_timestamp()
    {
        final Instant nodeTimestamp = Instant.parse( "2014-01-01T10:00:00Z" );
        final Instant beforeImport = Instant.now().minusSeconds( 60 );

        final ImportNodeResult importNodeResult = importNode( Node.create()
                                                                  .name( "myNode" )
                                                                  .parentPath( NodePath.ROOT )
                                                                  .data( new PropertyTree() )
                                                                  .timestamp( nodeTimestamp )
                                                                  .build() );

        final Instant versionTimestamp = importNodeResult.getNode().getTimestamp();
        assertNotEquals( nodeTimestamp, versionTimestamp );
        assertTrue( versionTimestamp.isAfter( beforeImport ), "version timestamp should be the import time, not the imported value" );
    }

    @Test
    void pushed_node_is_equal_in_both_branches()
    {
        final NodeId nodeId = NodeId.from( "abc" );

        // Branches are kept in sync by sharing the same version (push), not by importing the same
        // content separately into each branch - separate imports now get independent version timestamps.
        ctxDefault().callWith( () -> {
            importNode( Node.create()
                            .id( nodeId )
                            .name( "myNode" )
                            .parentPath( NodePath.ROOT )
                            .data( new PropertyTree() )
                            .build() );
            return pushNodes( WS_OTHER, nodeId );
        } );

        final NodeComparison comparison = ctxDefault().callWith( () -> CompareNodeCommand.create()
            .nodeId( nodeId )
            .target( WS_OTHER )
            .storageService( this.storageService )
            .build()
            .execute() );

        assertEquals( NodeCompareStatus.EQUAL, comparison.getCompareStatus() );
    }

    @Test
    void import_with_id()
    {
        final Node importNode =
            Node.create().id( NodeId.from( "abc" ) ).name( "myNode" ).parentPath( NodePath.ROOT ).data( new PropertyTree() ).build();

        importNode( importNode );

        final Node abc = getNodeById( NodeId.from( "abc" ) );

        assertNotNull( abc );
    }

    @Test
    void permissions_on_root_create()
    {
        final AccessControlList aclList = AccessControlList.create()
            .add( AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allowAll().deny( Permission.DELETE ).build() )
            .add( AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().deny( Permission.DELETE ).build() )
            .build();

        final Node rootNode = Node.create()
            .id( NodeId.ROOT )
            .name( "rootNode" )
            .parentPath( NodePath.ROOT )
            .data( new PropertyTree() )
            .permissions( aclList )
            .build();

        this.nodeRepositoryService.create( RepositoryId.from( "test" ), RepositorySettings.create().build() );

        final ImportNodeResult importNodeResult = ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( "test" )
            .build()
            .callWith( () -> importNode( rootNode, false, true ) );

        assertEquals( aclList, importNodeResult.getNode().getPermissions() );
    }


    @Test
    void keep_permissions_on_create()
    {
        final AccessControlList aclList = AccessControlList.create()
            .add( AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allowAll().deny( Permission.DELETE ).build() )
            .add( AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().deny( Permission.DELETE ).build() )
            .build();

        final Node importNode = Node.create()
            .id( NodeId.from( "abc" ) )
            .name( "myNode" )
            .parentPath( NodePath.ROOT )
            .data( new PropertyTree() )
            .permissions( aclList )
            .build();

        final ImportNodeResult importNodeResult = importNode( importNode );
        final Node abc = getNodeById( NodeId.from( "abc" ) );
        assertNotNull( abc );
        assertEquals( aclList, abc.getPermissions() );
        assertEquals( importNodeResult.getNode().getPermissions(), abc.getPermissions() );
        assertFalse( importNodeResult.isPreExisting() );
    }

    @Test
    void skip_permissions_on_create()
    {
        final AccessControlList aclList = AccessControlList.create()
            .add( AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allowAll().deny( Permission.DELETE ).build() )
            .add( AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().deny( Permission.DELETE ).build() )
            .build();

        final Node importNode = Node.create()
            .id( NodeId.from( "abc" ) )
            .name( "myNode" )
            .parentPath( NodePath.ROOT )
            .data( new PropertyTree() )
            .permissions( aclList )
            .build();

        final ImportNodeResult importNodeResult = importNode( importNode, true, false );
        assertNotEquals( aclList, importNodeResult.getNode().getPermissions() );
    }

    @Test
    void keep_permissions_on_update()
    {
        importNode(
            Node.create().id( NodeId.from( "abc" ) ).name( "myNode" ).parentPath( NodePath.ROOT ).data( new PropertyTree() ).build() );

        final AccessControlList aclList = AccessControlList.create()
            .add( AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().deny( Permission.DELETE ).build() )
            .build();

        final Node updatedNode = importNode( Node.create()
                                                 .id( NodeId.from( "abc" ) )
                                                 .name( "myNode" )
                                                 .parentPath( NodePath.ROOT )
                                                 .data( new PropertyTree() )
                                                 .permissions( aclList )
                                                 .build(), true, true ).getNode();

        assertEquals( aclList, updatedNode.getPermissions() );
    }

    @Test
    void skip_permissions_on_update()
    {
        final Node createdNode = importNode( Node.create()
                                                 .id( NodeId.from( "abc" ) )
                                                 .name( "myNode" )
                                                 .parentPath( NodePath.ROOT )
                                                 .data( new PropertyTree() )
                                                 .build() ).getNode();

        final AccessControlList aclList = AccessControlList.create()
            .add( AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().deny( Permission.DELETE ).build() )
            .build();

        final Node updatedNode = importNode( Node.create()
                                                 .id( NodeId.from( "abc" ) )
                                                 .name( "myNode" )
                                                 .parentPath( NodePath.ROOT )
                                                 .data( new PropertyTree() )
                                                 .permissions( aclList )
                                                 .build() ).getNode();

        assertEquals( createdNode.getPermissions(), updatedNode.getPermissions() );

    }

    @Test
    void import_existing_node()
    {
        PropertyTree data = new PropertyTree();
        data.addString( "name", "value" );

        final Node importNode = Node.create().id( NodeId.from( "abc" ) ).name( "myNode" ).parentPath( NodePath.ROOT ).data( data ).build();

        importNode( importNode );
        final Node abc = getNodeById( NodeId.from( "abc" ) );
        assertNotNull( abc );
        assertEquals( data, abc.data() );

        PropertyTree data2 = new PropertyTree();
        data2.addString( "name", "valueUpdated" );

        final Node importNode2 =
            Node.create().id( NodeId.from( "abc" ) ).name( "myNode" ).parentPath( NodePath.ROOT ).data( data2 ).build();

        final ImportNodeResult importNodeResult = importNode( importNode2 );
        final Node abc2 = getNodeById( NodeId.from( "abc" ) );
        assertNotNull( abc2 );
        assertEquals( data2, abc2.data() );
        assertEquals( importNodeResult.getNode().data(), abc2.data() );
        assertTrue( importNodeResult.isPreExisting() );
    }


    private ImportNodeResult importNode( final Node importNode )
    {
        return ImportNodeCommand.create()
            .importNode( importNode )
            .binaryBlobStore( this.binaryService )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();
    }

    private ImportNodeResult importNode( final Node importNode, final boolean importPermissionsOnUpdate,
                                         final boolean importPermissionsOnCreate )
    {
        return ImportNodeCommand.create()
            .importNode( importNode )
            .binaryBlobStore( this.binaryService )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .importPermissions( importPermissionsOnUpdate )
            .importPermissionsOnCreate( importPermissionsOnCreate )
            .build()
            .execute();
    }
}
