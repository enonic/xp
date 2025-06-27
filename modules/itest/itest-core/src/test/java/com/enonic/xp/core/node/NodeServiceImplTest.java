package com.enonic.xp.core.node;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.Event;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.DeleteNodeResult;
import com.enonic.xp.node.DuplicateNodeParams;
import com.enonic.xp.node.FindNodesByMultiRepoQueryResult;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.MultiRepoNodeHit;
import com.enonic.xp.node.MultiRepoNodeHits;
import com.enonic.xp.node.MultiRepoNodeQuery;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionsMetadata;
import com.enonic.xp.node.OperationNotPermittedException;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.RenameNodeParams;
import com.enonic.xp.node.ReorderChildNodeParams;
import com.enonic.xp.node.ReorderChildNodesParams;
import com.enonic.xp.node.ReorderChildNodesResult;
import com.enonic.xp.node.RoutableNodeVersionId;
import com.enonic.xp.node.RoutableNodeVersionIds;
import com.enonic.xp.node.SearchTarget;
import com.enonic.xp.node.SearchTargets;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.repo.impl.NodeEvents;
import com.enonic.xp.repository.BranchNotFoundException;
import com.enonic.xp.repository.RepositoryNotFoundException;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.TraceManager;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.util.BinaryReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NodeServiceImplTest
    extends AbstractNodeTest
{
    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();

        final TraceManager manager = mock( TraceManager.class );
        final Trace trace = mock( Trace.class );
        when( manager.newTrace( any(), any() ) ).thenReturn( trace );
        Tracer.setManager( manager );
    }

    @AfterEach
    void tearDown()
    {
        Tracer.setManager( null );
    }

    @Test
    public void get_by_id()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );

        final Node fetchedNode = this.nodeService.getById( NodeId.from( createdNode.id() ) );

        assertEquals( createdNode, fetchedNode );
    }

    @Test
    public void get_by_id_non_existing()
        throws Exception
    {
        assertThrows( NodeNotFoundException.class, () -> this.nodeService.getById( NodeId.from( "a" ) ) );
    }

    @Test
    public void get_by_id_repo_non_existing()
        throws Exception
    {
        assertThrows( RepositoryNotFoundException.class, () -> ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( "missing-repo" )
            .build()
            .callWith( () -> this.nodeService.getById( NodeId.from( "a" ) ) ) );
    }

    @Test
    public void get_by_id_branch_non_existing()
        throws Exception
    {
        assertThrows( BranchNotFoundException.class, () -> ContextBuilder.from( ContextAccessor.current() )
            .branch( "missing-branch" )
            .build()
            .callWith( () -> this.nodeService.getById( NodeId.from( "a" ) ) ) );
    }

    @Test
    public void rename()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );

        nodeService.rename( RenameNodeParams.create().nodeName( NodeName.from( "my-node-edited" ) ).nodeId( createdNode.id() ).build() );

        final Node renamedNode = nodeService.getById( createdNode.id() );

        assertEquals( "my-node-edited", renamedNode.name().toString() );
    }

    @Test
    public void move_to_the_same_parent()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );

        final Node movedNode =
            nodeService.move( MoveNodeParams.create().nodeId( createdNode.id() ).parentNodePath( createdNode.parentPath() ).build() );

        assertEquals( createdNode, movedNode );
    }

    @Test
    public void create()
        throws Exception
    {

        final ChildOrder childOrder = ChildOrder.create()
            .add( FieldOrderExpr.create( NodeIndexPath.TIMESTAMP, OrderExpr.Direction.DESC ) )
            .add( FieldOrderExpr.create( NodeIndexPath.NAME, OrderExpr.Direction.ASC ) )
            .build();

        final AccessControlList aclList = AccessControlList.create()
            .add( AccessControlEntry.create().principal( PrincipalKey.from( "user:myidprovider:rmy" ) ).allow( Permission.READ ).build() )
            .build();

        final CreateNodeParams params =
            CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).permissions( aclList ).childOrder( childOrder ).build();

        final Node node = this.nodeService.create( params );

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

        final Node node = this.nodeService.create( CreateNodeParams.create()
                                                       .name( "my-node" )
                                                       .parent( NodePath.ROOT )
                                                       .data( data )
                                                       .attachBinary( binaryRef1, ByteSource.wrap( binarySource.getBytes() ) )
                                                       .build() );

        final Node duplicatedNode = this.nodeService.duplicate( DuplicateNodeParams.create().nodeId( node.id() ).build() );

        assertNotEquals( node, duplicatedNode );
        assertEquals( node.getAttachedBinaries(), duplicatedNode.getAttachedBinaries() );
    }

    @Test
    public void test_duplicate_with_children()
    {
        final PropertyTree data = new PropertyTree();

        final Node node_1 =
            this.nodeService.create( CreateNodeParams.create().name( "parent" ).parent( NodePath.ROOT ).data( data ).build() );

        final Node node_1_2 =
            this.nodeService.create( CreateNodeParams.create().name( "child" ).parent( node_1.path() ).data( data ).build() );

        final Node node_1_2_3 =
            this.nodeService.create( CreateNodeParams.create().name( "child_of_child" ).parent( node_1_2.path() ).data( data ).build() );

        this.nodeService.refresh( RefreshMode.SEARCH );

        final Node duplicatedNode = this.nodeService.duplicate( DuplicateNodeParams.create().nodeId( node_1.id() ).build() );

        final NodeId node_1_2_dup_id =
            this.nodeService.findByParent( FindNodesByParentParams.create().parentId( duplicatedNode.id() ).build() ).getNodeIds().first();

        final Node node_1_2_dup = this.nodeService.getById( node_1_2_dup_id );

        assertEquals( node_1_2.name(), node_1_2_dup.name() );

        final NodeId node_1_2_3_dup_id =
            this.nodeService.findByParent( FindNodesByParentParams.create().parentPath( node_1_2_dup.path() ).build() )
                .getNodeIds()
                .first();

        final Node node_1_2_3_dup = this.nodeService.getById( node_1_2_3_dup_id );

        assertEquals( node_1_2_3.name(), node_1_2_3_dup.name() );
    }

    @Test
    public void test_duplicate_without_children()
    {
        final PropertyTree data = new PropertyTree();

        final Node node_1 =
            this.nodeService.create( CreateNodeParams.create().name( "parent" ).parent( NodePath.ROOT ).data( data ).build() );

        final Node node_1_2 =
            this.nodeService.create( CreateNodeParams.create().name( "child" ).parent( node_1.path() ).data( data ).build() );

        this.nodeService.refresh( RefreshMode.SEARCH );

        final Node duplicatedNode =
            this.nodeService.duplicate( DuplicateNodeParams.create().includeChildren( false ).nodeId( node_1.id() ).build() );

        final Long childrenNumber =
            this.nodeService.findByParent( FindNodesByParentParams.create().parentId( duplicatedNode.id() ).build() ).getTotalHits();

        assertEquals( Long.valueOf( 0 ), childrenNumber );
    }

    @Test
    public void testDuplicateWithCustomParams()
    {
        final PropertyTree data = new PropertyTree();

        final Node node_1 =
            this.nodeService.create( CreateNodeParams.create().name( "parent" ).parent( NodePath.ROOT ).data( data ).build() );

        final Node node_1_1 =
            this.nodeService.create( CreateNodeParams.create().name( "child 1" ).parent( node_1.path() ).data( data ).build() );

        final Node node_1_2 =
            this.nodeService.create( CreateNodeParams.create().name( "child 2" ).parent( node_1.path() ).data( data ).build() );

        this.nodeService.refresh( RefreshMode.SEARCH );

        final Node duplicatedNode = this.nodeService.duplicate( DuplicateNodeParams.create()
                                                                    .nodeId( node_1_1.id() )
                                                                    .includeChildren( false )
                                                                    .name( "duplicated-of-child-1" )
                                                                    .parent( node_1_2.path() ).dataProcessor( ( originalData, path ) -> {
                                                                        originalData.setString( "extraProp", "extraPropValue" );
                                                                        return originalData;
                                                                    } )
                                                                    .build() );

        assertEquals( "duplicated-of-child-1", duplicatedNode.name().toString() );
        assertEquals( node_1_2.path() + "/duplicated-of-child-1", duplicatedNode.path().toString() );
        assertEquals( node_1_2.path(), duplicatedNode.parentPath() );
        assertEquals( "extraPropValue", duplicatedNode.data().getString( "extraProp" ) );
    }

    @Test
    public void test_get_binary_key()
    {

        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef1 = BinaryReference.from( "binary" );
        data.addBinaryReference( "my-binary-1", binaryRef1 );

        final String binarySource = "binary_source";

        final Node node = this.nodeService.create( CreateNodeParams.create()
                                                       .name( "my-node" )
                                                       .parent( NodePath.ROOT )
                                                       .data( data )
                                                       .attachBinary( binaryRef1, ByteSource.wrap( binarySource.getBytes() ) )
                                                       .build() );

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

        final Node node = this.nodeService.create( CreateNodeParams.create()
                                                       .name( "my-node" )
                                                       .parent( NodePath.ROOT )
                                                       .data( data )
                                                       .attachBinary( binaryRef1, ByteSource.wrap( binarySource.getBytes() ) )
                                                       .build() );

        final ByteSource source = this.nodeService.getBinary( node.id(), binaryRef1 );

        assertArrayEquals( source.read(), binarySource.getBytes() );
    }

    @Test
    public void test_commit()
    {
        //Create and update node
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );
        final NodeId nodeId = createdNode.id();

        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().id( nodeId ).editor( toBeEdited -> {
            toBeEdited.data.addString( "newField", "fisk" );
        } ).build();
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
        final NodeCommitEntry commitEntry = NodeCommitEntry.create().message( "Commit message" ).build();
        final NodeCommitEntry returnedCommitEntry = nodeService.commit( commitEntry, NodeIds.from( nodeId ) );
        nodeService.refresh( RefreshMode.STORAGE );

        //Check created commit entry
        final NodeCommitId nodeCommitId = returnedCommitEntry.getNodeCommitId();
        assertNotNull( nodeCommitId );
        assertEquals( "Commit message", returnedCommitEntry.getMessage() );
        assertNotNull( returnedCommitEntry.getTimestamp() );
        assertEquals( "user:system:test-user", returnedCommitEntry.getCommitter().toString() );

        //Check that only the latest version has a commit ID
        final NodeVersionsMetadata versionsMetadata2 = getVersionsMetadata( nodeId );
        assertEquals( 2, versionsMetadata2.size() );
        final Iterator<NodeVersionMetadata> versionMetadataIterator2 = versionsMetadata2.iterator();
        final NodeVersionMetadata latestVersionMetadata2 = versionMetadataIterator2.next();
        final NodeVersionMetadata firstVersionMetadata2 = versionMetadataIterator2.next();
        assertEquals( nodeCommitId, latestVersionMetadata2.getNodeCommitId() );
        assertNull( firstVersionMetadata2.getNodeCommitId() );

        //Call commit with the node version ID of the first version
        final NodeCommitEntry commitEntry2 = NodeCommitEntry.create().message( "Commit message 2" ).build();
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

    @Test
    public void testGetByIdAndVersionId()
    {
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );

        final Node fetchedNode = this.nodeService.getByIdAndVersionId( createdNode.id(), createdNode.getNodeVersionId() );

        assertEquals( createdNode, fetchedNode );
    }

    @Test
    public void testReorderChildren()
    {
        final Node parent = createNode(
            CreateNodeParams.create().name( "my-parent" ).parent( NodePath.ROOT ).childOrder( ChildOrder.manualOrder() ).build() );

        final Node child1 = createNode( CreateNodeParams.create().name( "my-child-1" ).parent( parent.path() ).build() );

        final Node child2 = createNode( CreateNodeParams.create().name( "my-child-2" ).parent( parent.path() ).build() );

        final Node child3 = createNode( CreateNodeParams.create().name( "my-child-3" ).parent( parent.path() ).build() );

        final ReorderChildNodesParams params = ReorderChildNodesParams.create()
            .add( ReorderChildNodeParams.create().nodeId( child1.id() ).moveBefore( child2.id() ).build() )
            .add( ReorderChildNodeParams.create().nodeId( child3.id() ).moveBefore( child1.id() ).build() ).processor( ( data, path ) -> {
                data.addString( "processedValue", "value" );
                return data;
            } )
            .build();

        final ReorderChildNodesResult result = this.nodeService.reorderChildren( params );

        assertThat( result.getNodeIds() ).containsExactly( child1.id(), child3.id() );
        assertThat( result.getParentNodes().getIds() ).containsExactly( parent.id() );
        assertThat(
            nodeService.findByParent( FindNodesByParentParams.create().parentId( parent.id() ).build() ).getNodeIds() ).containsExactly(
            child3.id(), child1.id(), child2.id() );

        final Node processedParent = this.nodeService.getById( parent.id() );

        assertEquals( "value", processedParent.data().getString( "processedValue" ) );
    }

    @Test
    void delete_tree_by_path()
    {
        final Node parent = createNode(
            CreateNodeParams.create().name( "my-parent" ).parent( NodePath.ROOT ).childOrder( ChildOrder.manualOrder() ).build() );

        final Node child1 = createNode( CreateNodeParams.create().name( "my-child-1" ).parent( parent.path() ).build() );

        final Node child2 = createNode( CreateNodeParams.create().name( "my-child-2" ).parent( parent.path() ).build() );

        final Node child3 = createNode( CreateNodeParams.create().name( "my-child-3" ).parent( parent.path() ).build() );

        final DeleteNodeResult result = nodeService.delete( DeleteNodeParams.create().nodePath( parent.path() ).build() );
        assertThat( result.getNodeBranchEntries() ).map( NodeBranchEntry::getNodeId, NodeBranchEntry::getNodePath )
            .containsExactlyInAnyOrder( tuple( child3.id(), child3.path() ), tuple( child2.id(), child2.path() ),
                                        tuple( child1.id(), child1.path() ), tuple( parent.id(), parent.path() ) );
    }

    @Test
    void delete_tree_by_id()
    {
        final Node parent = createNode(
            CreateNodeParams.create().name( "my-parent" ).parent( NodePath.ROOT ).childOrder( ChildOrder.manualOrder() ).build() );

        final Node child1 = createNode( CreateNodeParams.create().name( "my-child-1" ).parent( parent.path() ).build() );

        final Node child2 = createNode( CreateNodeParams.create().name( "my-child-2" ).parent( parent.path() ).build() );

        final Node child3 = createNode( CreateNodeParams.create().name( "my-child-3" ).parent( parent.path() ).build() );

        final DeleteNodeResult result = nodeService.delete( DeleteNodeParams.create().nodeId( parent.id() ).build() );
        assertThat( result.getNodeBranchEntries() ).map( NodeBranchEntry::getNodeId, NodeBranchEntry::getNodePath )
            .containsExactlyInAnyOrder( tuple( child3.id(), child3.path() ), tuple( child2.id(), child2.path() ),
                                        tuple( child1.id(), child1.path() ), tuple( parent.id(), parent.path() ) );
    }

    @Test
    void delete_root_path_fail()
    {

        assertThrows( OperationNotPermittedException.class,
                      () -> nodeService.delete( DeleteNodeParams.create().nodePath( NodePath.ROOT ).build() ) );
    }

    @Test
    void delete_root_id_fail()
    {

        assertThrows( OperationNotPermittedException.class,
                      () -> nodeService.delete( DeleteNodeParams.create().nodeId( Node.ROOT_UUID ).build() ) );
    }

    @Test
    void applyPermissions_shouldPublishCorrectEvents()
    {
        final Node parentNode = createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( "my-node-1" ).build() );
        final Node childNode = createNode( CreateNodeParams.create().parent( parentNode.path() ).name( "my-node-2" ).build() );

        pushNodes( ContentConstants.BRANCH_MASTER, parentNode.id(), childNode.id() );

        ContextAccessor.current().runWith( () -> {
            updateNode( UpdateNodeParams.create().id( childNode.id() ).editor( node -> node.data.addString( "test", "test" ) ).build() );
        } );

        final ApplyNodePermissionsParams params = ApplyNodePermissionsParams.create()
            .nodeId( parentNode.id() )
            .permissions( AccessControlList.of(
                AccessControlEntry.create().allowAll().principal( PrincipalKey.from( "user:myidprovider:rmy" ) ).build() ) )
            .addBranches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) )
            .build();

        Mockito.clearInvocations( eventPublisher );

        nodeService.applyPermissions( params );

        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );

        verify( eventPublisher, times( 2 ) ).publish( captor.capture() );

        List<Event> publishedEvents = captor.getAllValues();

        assertEquals( 1,
                      publishedEvents.stream().filter( event -> event.getType().equals( NodeEvents.NODE_PERMISSIONS_UPDATED ) ).count() );
        assertEquals( 1, publishedEvents.stream().filter( event -> event.getType().equals( NodeEvents.NODE_PUSHED_EVENT ) ).count() );
    }

    @Test
    void patch_shouldPublishCorrectEvents()
    {
        final Node nodeToPatch = createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( "my-node-1" ).build() );
        final Node editedNodeToPatch = createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( "my-node-2" ).build() );

        pushNodes( ContentConstants.BRANCH_MASTER, nodeToPatch.id(), editedNodeToPatch.id() );

        ContextAccessor.current().runWith( () -> {
            updateNode(
                UpdateNodeParams.create().id( editedNodeToPatch.id() ).editor( node -> node.data.addString( "test", "test1" ) ).build() );
        } );

        Mockito.clearInvocations( eventPublisher );

        nodeService.patch( PatchNodeParams.create()
                               .id( editedNodeToPatch.id() )
                               .editor( node -> node.data.addString( "test", "test2" ) )
                               .addBranches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) )
                               .build() );

        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );

        verify( eventPublisher, times( 2 ) ).publish( captor.capture() );

        List<Event> publishedEvents = captor.getAllValues();

        assertEquals( NodeEvents.NODE_UPDATED_EVENT, publishedEvents.get( 0 ).getType() );
        assertEquals( NodeEvents.NODE_UPDATED_EVENT, publishedEvents.get( 1 ).getType() );

        Mockito.clearInvocations( eventPublisher );

        nodeService.patch( PatchNodeParams.create()
                               .id( nodeToPatch.id() )
                               .editor( node -> node.data.addString( "test", "test2" ) )
                               .addBranches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) )
                               .build() );

        verify( eventPublisher, times( 2 ) ).publish( captor.capture() );

        publishedEvents = captor.getAllValues();

        assertEquals( 4, publishedEvents.size() );
        assertEquals( NodeEvents.NODE_UPDATED_EVENT, publishedEvents.get( 2 ).getType() );
        assertEquals( NodeEvents.NODE_PUSHED_EVENT, publishedEvents.get( 3 ).getType() );
    }

    @Test
    public void testFindByQuery()
    {
        final Node node = this.nodeService.create(
            CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).data( new PropertyTree() ).build() );

        nodeService.refresh( RefreshMode.SEARCH );

        final NodeQuery nodeQuery =
            NodeQuery.create().parent( NodePath.ROOT ).path( NodePath.create().addElement( "my-node" ).build() ).build();

        final SearchTargets searchTargets = SearchTargets.create()
            .add( SearchTarget.create()
                      .repositoryId( ContextAccessor.current().getRepositoryId() )
                      .branch( ContextAccessor.current().getBranch() )
                      .principalKeys( ContextAccessor.current().getAuthInfo().getPrincipals() )
                      .build() )
            .build();

        final MultiRepoNodeQuery multiRepoNodeQuery = new MultiRepoNodeQuery( searchTargets, nodeQuery );

        final FindNodesByMultiRepoQueryResult queryResult = this.nodeService.findByQuery( multiRepoNodeQuery );

        assertEquals( 1, queryResult.getHits() );

        final MultiRepoNodeHits nodeHits = queryResult.getNodeHits();
        assertEquals( 1, nodeHits.getSize() );

        final MultiRepoNodeHit multiRepoNodeHit = nodeHits.get( 0 );
        assertEquals( node.id(), multiRepoNodeHit.getNodeId() );
        assertEquals( ContextAccessor.current().getRepositoryId(), multiRepoNodeHit.getRepositoryId() );
        assertEquals( ContextAccessor.current().getBranch(), multiRepoNodeHit.getBranch() );
    }


    private NodeVersionsMetadata getVersionsMetadata( NodeId nodeId )
    {
        final GetNodeVersionsParams params = GetNodeVersionsParams.create().nodeId( nodeId ).build();
        return nodeService.findVersions( params ).getNodeVersionsMetadata();
    }
}
