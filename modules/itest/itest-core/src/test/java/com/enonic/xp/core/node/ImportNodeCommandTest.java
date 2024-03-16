package com.enonic.xp.core.node;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.ImportNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.node.CompareNodeCommand;
import com.enonic.xp.repo.impl.node.ImportNodeCommand;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImportNodeCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    public void setUp()
        throws Exception
    {
        ctxDefault().callWith( this::createDefaultRootNode );
        ctxOther().callWith( this::createDefaultRootNode );
    }

    @Test
    public void no_timestamp()
        throws Exception
    {
        final Node importNode = Node.create().
            name( "myNode" ).
            parentPath( NodePath.ROOT ).
            data( new PropertyTree() ).
            build();

        final ImportNodeResult importNodeResult = importNode( importNode );

        assertNotNull( importNodeResult.getNode().getTimestamp() );
    }

    @Test
    public void created_nodes_with_id_and_timestamp_should_be_equal()
        throws Exception
    {
        ctxDefault().callWith( () -> importNode( Node.create().
            id( NodeId.from( "abc" ) ).
            name( "myNode" ).
            parentPath( NodePath.ROOT ).
            data( new PropertyTree() ).
            timestamp( Instant.parse( "2014-01-01T10:00:00Z" ) ).
            build() ) );

        ctxOther().callWith( () -> importNode( Node.create().
            id( NodeId.from( "abc" ) ).
            name( "myNode" ).
            parentPath( NodePath.ROOT ).
            data( new PropertyTree() ).
            timestamp( Instant.parse( "2014-01-01T10:00:00Z" ) ).
            build() ) );

        final NodeComparison comparison = CompareNodeCommand.create().
            nodeId( NodeId.from( "abc" ) ).
            target( WS_OTHER ).
            storageService( this.storageService ).
            build().
            execute();

        assertEquals( CompareStatus.EQUAL, comparison.getCompareStatus() );
    }

    @Test
    public void import_with_id()
        throws Exception
    {
        final Node importNode = Node.create().
            id( NodeId.from( "abc" ) ).
            name( "myNode" ).
            parentPath( NodePath.ROOT ).
            data( new PropertyTree() ).
            build();

        importNode( importNode );

        final Node abc = getNodeById( NodeId.from( "abc" ) );

        assertNotNull( abc );
    }

    @Test
    public void permissions_on_root_create()
        throws Exception
    {
        final AccessControlList aclList = AccessControlList.create().
            add( AccessControlEntry.create().
                principal( PrincipalKey.ofAnonymous() ).
                allowAll().
                deny( Permission.DELETE ).
                build() ).
            add( AccessControlEntry.create().
                principal( TEST_DEFAULT_USER.getKey() ).
                allowAll().
                deny( Permission.DELETE ).
                build() ).
            build();

        final Node rootNode = Node.create().
            id( Node.ROOT_UUID ).
            name( "rootNode" ).
            parentPath( NodePath.ROOT ).
            data( new PropertyTree() ).
            permissions( aclList ).
            build();

        this.nodeRepositoryService.create( CreateRepositoryParams.create().
            repositoryId( RepositoryId.from( "test" ) ).
            build() );

        final ImportNodeResult importNodeResult = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( "test" ).
            build().
            callWith( () -> importNode( rootNode, false, true ) );

        assertEquals( aclList, importNodeResult.getNode().getPermissions() );
    }


    @Test
    public void keep_permissions_on_create()
        throws Exception
    {
        final AccessControlList aclList = AccessControlList.create().
            add( AccessControlEntry.create().
                principal( PrincipalKey.ofAnonymous() ).
                allowAll().
                deny( Permission.DELETE ).
                build() ).
            add( AccessControlEntry.create().
                principal( TEST_DEFAULT_USER.getKey() ).
                allowAll().
                deny( Permission.DELETE ).
                build() ).
            build();

        final Node importNode = Node.create().
            id( NodeId.from( "abc" ) ).
            name( "myNode" ).
            parentPath( NodePath.ROOT ).
            data( new PropertyTree() ).
            permissions( aclList ).
            build();

        final ImportNodeResult importNodeResult = importNode( importNode );
        final Node abc = getNodeById( NodeId.from( "abc" ) );
        assertNotNull( abc );
        assertEquals( aclList, abc.getPermissions() );
        assertEquals( importNodeResult.getNode().getPermissions(), abc.getPermissions() );
        assertFalse( importNodeResult.isPreExisting() );
    }

    @Test
    public void merge_permissions_on_create()
        throws Exception
    {
        final AccessControlList aclList = AccessControlList.create().
            add( AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allowAll().build() ).
            add( AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).deny( Permission.DELETE ).build() ).
            build();

        final Node importNode = Node.create().
            id( NodeId.from( "abc" ) ).
            name( "myNode" ).
            parentPath( NodePath.ROOT ).
            data( new PropertyTree() ).
            permissions( aclList ).
            build();

        final ImportNodeResult importNodeResult = importNode( importNode, true, false );

        assertTrue( importNodeResult.getNode()
                        .getPermissions()
                        .isAllowedFor( TEST_DEFAULT_USER.getKey(), Permission.READ, Permission.CREATE, Permission.MODIFY,
                                       Permission.PUBLISH ) );
        assertFalse( importNodeResult.getNode().getPermissions().isAllowedFor( TEST_DEFAULT_USER.getKey(), Permission.DELETE ) );
        assertTrue( importNodeResult.getNode()
                        .getPermissions()
                        .isAllowedFor( PrincipalKey.ofAnonymous(), Permission.CREATE, Permission.READ, Permission.MODIFY,
                                       Permission.PUBLISH, Permission.DELETE, Permission.WRITE_PERMISSIONS, Permission.READ_PERMISSIONS ) );
    }

    @Test
    public void keep_permissions_on_update()
        throws Exception
    {
        importNode( Node.create().
            id( NodeId.from( "abc" ) ).
            name( "myNode" ).
            parentPath( NodePath.ROOT ).
            data( new PropertyTree() ).
            build() );

        final AccessControlList aclList = AccessControlList.create().
            add( AccessControlEntry.create().
                principal( TEST_DEFAULT_USER.getKey() ).
                allowAll().
                deny( Permission.DELETE ).
                build() ).
            build();

        final Node updatedNode = importNode( Node.create().
            id( NodeId.from( "abc" ) ).
            name( "myNode" ).
            parentPath( NodePath.ROOT ).
            data( new PropertyTree() ).
            permissions( aclList ).
            build(), true, true ).
            getNode();

        assertEquals( aclList, updatedNode.getPermissions() );
    }

    @Test
    public void skip_permissions_on_update()
        throws Exception
    {
        final Node createdNode = importNode( Node.create().
            id( NodeId.from( "abc" ) ).
            name( "myNode" ).
            parentPath( NodePath.ROOT ).
            data( new PropertyTree() ).
            build() ).
            getNode();

        final AccessControlList aclList = AccessControlList.create().
            add( AccessControlEntry.create().
                principal( TEST_DEFAULT_USER.getKey() ).
                allowAll().
                deny( Permission.DELETE ).
                build() ).
            build();

        final Node updatedNode = importNode( Node.create().
            id( NodeId.from( "abc" ) ).
            name( "myNode" ).
            parentPath( NodePath.ROOT ).
            data( new PropertyTree() ).
            permissions( aclList ).
            build() ).
            getNode();

        assertEquals( createdNode.getPermissions(), updatedNode.getPermissions() );

    }

    @Test
    public void import_existing_node()
    {
        PropertyTree data = new PropertyTree();
        data.addString( "name", "value" );

        final Node importNode = Node.create().
            id( NodeId.from( "abc" ) ).
            name( "myNode" ).
            parentPath( NodePath.ROOT ).
            data( data ).
            build();

        importNode( importNode );
        final Node abc = getNodeById( NodeId.from( "abc" ) );
        assertNotNull( abc );
        assertEquals( data, abc.data() );

        PropertyTree data2 = new PropertyTree();
        data2.addString( "name", "valueUpdated" );

        final Node importNode2 = Node.create().
            id( NodeId.from( "abc" ) ).
            name( "myNode" ).
            parentPath( NodePath.ROOT ).
            data( data2 ).
            build();

        final ImportNodeResult importNodeResult = importNode( importNode2 );
        final Node abc2 = getNodeById( NodeId.from( "abc" ) );
        assertNotNull( abc2 );
        assertEquals( data2, abc2.data() );
        assertEquals( importNodeResult.getNode().data(), abc2.data() );
        assertTrue( importNodeResult.isPreExisting() );
    }


    private ImportNodeResult importNode( final Node importNode )
    {
        return ImportNodeCommand.create().
            importNode( importNode ).
            binaryBlobStore( this.binaryService ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    private ImportNodeResult importNode( final Node importNode, final boolean importPermissionsOnUpdate,
                                         final boolean importPermissionsOnCreate )
    {
        return ImportNodeCommand.create().
            importNode( importNode ).
            binaryBlobStore( this.binaryService ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            importPermissions( importPermissionsOnUpdate ).
            importPermissionsOnCreate( importPermissionsOnCreate ).
            build().
            execute();
    }
}
