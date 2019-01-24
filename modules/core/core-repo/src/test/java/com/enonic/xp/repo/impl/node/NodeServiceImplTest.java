package com.enonic.xp.repo.impl.node;

import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.DuplicateNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionsMetadata;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.RenameNodeParams;
import com.enonic.xp.node.RoutableNodeVersionId;
import com.enonic.xp.node.RoutableNodeVersionIds;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.repository.BranchNotFoundException;
import com.enonic.xp.repository.RepositoryNotFoundException;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.BinaryReference;

import static org.junit.Assert.*;

public class NodeServiceImplTest
    extends AbstractNodeTest
{
    private NodeServiceImpl nodeService;

    @Before
    public void setUp()
        throws Exception
    {

        super.setUp();
        this.nodeService = new NodeServiceImpl();
        this.nodeService.setIndexServiceInternal( indexServiceInternal );
        this.nodeService.setNodeStorageService( this.storageService );
        this.nodeService.setNodeSearchService( this.searchService );
        this.nodeService.setRepositoryService( this.repositoryService );
        this.nodeService.setBinaryService( this.binaryService );
        this.nodeService.setEventPublisher( Mockito.mock( EventPublisher.class ) );

        this.createDefaultRootNode();
    }

    @Test
    public void get_by_id()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        final Node fetchedNode = this.nodeService.getById( NodeId.from( createdNode.id() ) );

        assertEquals( createdNode, fetchedNode );
    }

    @Test(expected = NodeNotFoundException.class)
    public void get_by_id_non_existing()
        throws Exception
    {
        this.nodeService.getById( NodeId.from( "a" ) );
    }

    @Test(expected = RepositoryNotFoundException.class)
    public void get_by_id_repo_non_existing()
        throws Exception
    {
        ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( "missing-repo" ).
            build().
            callWith( () -> this.nodeService.getById( NodeId.from( "a" ) ) );
    }

    @Test(expected = BranchNotFoundException.class)
    public void get_by_id_branch_non_existing()
        throws Exception
    {
        ContextBuilder.from( ContextAccessor.current() ).
            branch( "missing-branch" ).
            build().
            callWith( () -> this.nodeService.getById( NodeId.from( "a" ) ) );
    }

    @Test
    public void createRootNode()
    {
        final User user = User.create().
            key( PrincipalKey.ofUser( IdProviderKey.system(), "user1" ) ).
            displayName( "User 1" ).
            modifiedTime( Instant.now() ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();

        this.nodeService.createRootNode( CreateRootNodeParams.create().
            childOrder( ChildOrder.from( "_name ASC" ) ).
            permissions( AccessControlList.of( AccessControlEntry.create().
                allowAll().
                principal( user.getKey() ).
                build() ) ).
            build() );

        printContentRepoIndex();

        final Context context = ContextBuilder.create().
            authInfo( AuthenticationInfo.create().
                user( user ).
                principals( RoleKeys.CONTENT_MANAGER_ADMIN ).
                build() ).
            branch( WS_DEFAULT ).
            repositoryId( TEST_REPO.getId() ).
            build();

        context.runWith( () -> assertNotNull( this.nodeService.getByPath( NodePath.ROOT ) ) );
        context.runWith( () -> assertNotNull( this.nodeService.getRoot() ) );
    }

    @Test
    public void rename()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        doRename( createdNode.id(), "my-node-edited" );

        final Node renamedNode = nodeService.getById( createdNode.id() );

        assertEquals( "my-node-edited", renamedNode.name().toString() );
    }

    @Test
    public void create()
        throws Exception
    {

        final ChildOrder childOrder = ChildOrder.create().
            add( FieldOrderExpr.create( NodeIndexPath.TIMESTAMP, OrderExpr.Direction.DESC ) ).
            add( FieldOrderExpr.create( NodeIndexPath.NAME, OrderExpr.Direction.ASC ) ).
            build();

        final AccessControlList aclList = AccessControlList.create().
            add( AccessControlEntry.create().
                principal( PrincipalKey.from( "user:myidprovider:rmy" ) ).
                allow( Permission.READ ).
                build() ).
            build();

        final CreateNodeParams params = CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            permissions( aclList ).
            childOrder( childOrder ).
            build();

        final Node node = this.nodeService.create( params );

        refresh();

        assertTrue( node.getPermissions() != null );
        assertEquals( aclList, node.getPermissions() );
        assertEquals( childOrder, node.getChildOrder() );
    }

    @Test
    public void test_duplicate_binary()
    {
        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef1 = BinaryReference.from( "binary" );
        data.addBinaryReference( "my-binary-1", binaryRef1 );

        final String binarySource = "binary_source";

        final Node node = this.nodeService.create( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            data( data ).
            attachBinary( binaryRef1, ByteSource.wrap( binarySource.getBytes() ) ).
            build() );

        this.nodeService.refresh( RefreshMode.SEARCH );

        final Node duplicatedNode = this.nodeService.duplicate( DuplicateNodeParams.create().nodeId( node.id() ).build() );

        assertNotEquals( node, duplicatedNode );
        assertEquals( node.getAttachedBinaries(), duplicatedNode.getAttachedBinaries() );
    }

    @Test
    public void test_duplicate_with_children()
    {
        final PropertyTree data = new PropertyTree();

        final Node node_1 = this.nodeService.create( CreateNodeParams.create().
            name( "parent" ).
            parent( NodePath.ROOT ).
            data( data ).
            build() );

        final Node node_1_2 = this.nodeService.create( CreateNodeParams.create().
            name( "child" ).
            parent( node_1.path() ).
            data( data ).
            build() );

        final Node node_1_2_3 = this.nodeService.create( CreateNodeParams.create().
            name( "child_of_child" ).
            parent( node_1_2.path() ).
            data( data ).
            build() );

        this.nodeService.refresh( RefreshMode.SEARCH );

        final Node duplicatedNode = this.nodeService.duplicate( DuplicateNodeParams.create().nodeId( node_1.id() ).build() );

        final NodeId node_1_2_dup_id =
            this.nodeService.findByParent( FindNodesByParentParams.create().parentId( duplicatedNode.id() ).build() ).getNodeIds().first();

        final Node node_1_2_dup = this.nodeService.getById( node_1_2_dup_id );

        assertEquals( node_1_2.name(), node_1_2_dup.name() );

        final NodeId node_1_2_3_dup_id = this.nodeService.findByParent(
            FindNodesByParentParams.create().parentPath( node_1_2_dup.path() ).build() ).getNodeIds().first();

        final Node node_1_2_3_dup = this.nodeService.getById( node_1_2_3_dup_id );

        assertEquals( node_1_2_3.name(), node_1_2_3_dup.name() );
    }

    @Test
    public void test_duplicate_without_children()
    {
        final PropertyTree data = new PropertyTree();

        final Node node_1 = this.nodeService.create( CreateNodeParams.create().
            name( "parent" ).
            parent( NodePath.ROOT ).
            data( data ).
            build() );

        final Node node_1_2 = this.nodeService.create( CreateNodeParams.create().
            name( "child" ).
            parent( node_1.path() ).
            data( data ).
            build() );

        this.nodeService.refresh( RefreshMode.SEARCH );

        final Node duplicatedNode =
            this.nodeService.duplicate( DuplicateNodeParams.create().includeChildren( false ).nodeId( node_1.id() ).build() );

        final Long childrenNumber =
            this.nodeService.findByParent( FindNodesByParentParams.create().parentId( duplicatedNode.id() ).build() ).getTotalHits();

        assertEquals( Long.valueOf( 0 ), childrenNumber );
    }

    @Test
    public void test_get_binary_key()
    {

        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef1 = BinaryReference.from( "binary" );
        data.addBinaryReference( "my-binary-1", binaryRef1 );

        final String binarySource = "binary_source";

        final Node node = this.nodeService.create( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            data( data ).
            attachBinary( binaryRef1, ByteSource.wrap( binarySource.getBytes() ) ).
            build() );

        final String key = this.nodeService.getBinaryKey( node.id(), binaryRef1 );

        assertNotNull( key );
    }

    @Test
    public void test_get_binary()
        throws IOException
    {

        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef1 = BinaryReference.from( "binary" );
        data.addBinaryReference( "my-binary-1", binaryRef1 );

        final String binarySource = "binary_source";

        final Node node = this.nodeService.create( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            data( data ).
            attachBinary( binaryRef1, ByteSource.wrap( binarySource.getBytes() ) ).
            build() );

        final ByteSource source = this.nodeService.getBinary( node.id(), binaryRef1 );

        assertArrayEquals( source.read(), binarySource.getBytes() );
    }

    @Test
    public void test_commit()
    {
        //Create and update node
        final Node createdNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );
        final NodeId nodeId = createdNode.id();

        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            id( nodeId ).
            editor( toBeEdited -> {
                toBeEdited.data.addString( "newField", "fisk" );
            } ).
            build();
        final Node updatedNode = updateNode( updateNodeParams );
        nodeService.refresh( RefreshMode.STORAGE );

        //Check that the two versions have no commit ID by default
        final NodeVersionsMetadata versionsMetadata = getVersionsMetadata( nodeId );
        assertEquals( 2, versionsMetadata.size() );
        final Iterator<NodeVersionMetadata> versionMetadataIterator = versionsMetadata.iterator();
        final NodeVersionMetadata latestVersionMetadata = versionMetadataIterator.next();
        final NodeVersionMetadata firstVersionMetadata = versionMetadataIterator.next();
        assertNull( latestVersionMetadata.getNodeCommitId() );
        assertNull( firstVersionMetadata.getNodeCommitId() );

        //Call commit with node ID
        final NodeCommitEntry commitEntry = NodeCommitEntry.create().
            message( "Commit message" ).
            committer( "Committer" ).
            build();
        final NodeCommitEntry returnedCommitEntry = nodeService.commit( commitEntry, NodeIds.from( nodeId ) );
        nodeService.refresh( RefreshMode.STORAGE );

        //Check created commit entry
        final NodeCommitId nodeCommitId = returnedCommitEntry.getNodeCommitId();
        assertNotNull( nodeCommitId );
        assertEquals( "Commit message", returnedCommitEntry.getMessage() );
        assertNotNull( returnedCommitEntry.getTimestamp() );
        assertEquals( "Committer", returnedCommitEntry.getCommitter() );

        //Check that only the latest version has a commit ID
        final NodeVersionsMetadata versionsMetadata2 = getVersionsMetadata( nodeId );
        assertEquals( 2, versionsMetadata2.size() );
        final Iterator<NodeVersionMetadata> versionMetadataIterator2 = versionsMetadata2.iterator();
        final NodeVersionMetadata latestVersionMetadata2 = versionMetadataIterator2.next();
        final NodeVersionMetadata firstVersionMetadata2 = versionMetadataIterator2.next();
        assertEquals( nodeCommitId, latestVersionMetadata2.getNodeCommitId() );
        assertNull( firstVersionMetadata2.getNodeCommitId() );

        //Call commit with the node version ID of the first version
        final NodeCommitEntry commitEntry2 = NodeCommitEntry.create().
            message( "Commit message 2" ).
            committer( "Committer" ).
            build();
        final RoutableNodeVersionId routableNodeVersionId = RoutableNodeVersionId.from( nodeId, firstVersionMetadata2.getNodeVersionId() );
        final NodeCommitEntry returnedCommitEntry2 =
            nodeService.commit( commitEntry, RoutableNodeVersionIds.from( routableNodeVersionId ) );
        nodeService.refresh( RefreshMode.STORAGE );

        //Check that only the first version has been impacted
        final NodeVersionsMetadata versionsMetadata3 = getVersionsMetadata( nodeId );
        assertEquals( 2, versionsMetadata3.size() );
        final Iterator<NodeVersionMetadata> versionMetadataIterator3 = versionsMetadata3.iterator();
        final NodeVersionMetadata latestVersionMetadata3 = versionMetadataIterator3.next();
        final NodeVersionMetadata firstVersionMetadata3 = versionMetadataIterator3.next();
        assertEquals( nodeCommitId, latestVersionMetadata3.getNodeCommitId() );
        assertEquals( returnedCommitEntry2.getNodeCommitId(), firstVersionMetadata3.getNodeCommitId() );
    }

    private NodeVersionsMetadata getVersionsMetadata( NodeId nodeId )
    {
        final GetNodeVersionsParams params = GetNodeVersionsParams.create().
            nodeId( nodeId ).
            build();
        return nodeService.findVersions( params ).getNodeVersionsMetadata();
    }

    private Node doRename( final NodeId nodeId, final String newName )
    {
        return nodeService.rename( RenameNodeParams.create().
            nodeName( NodeName.from( newName ) ).
            nodeId( nodeId ).
            build() );
    }

}
