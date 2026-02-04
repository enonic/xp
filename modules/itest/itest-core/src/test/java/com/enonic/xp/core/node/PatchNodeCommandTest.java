package com.enonic.xp.core.node;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventConstants;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.PatchNodeResult;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.NodeEvents;
import com.enonic.xp.repository.CreateBranchParams;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

class PatchNodeCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void timestamp_updated_when_modifying()
    {
        final PropertyTree data = new PropertyTree();

        final Node node = createNode( CreateNodeParams.create().name( "myNode" ).data( data ).parent( NodePath.ROOT ).build() );

        nodeService.patch( PatchNodeParams.create()
                               .id( node.id() )
                               .addBranches( Branches.from( ContextAccessor.current().getBranch() ) )
                               .editor( toBeEdited -> {
                                   toBeEdited.data.addString( "another", "stuff" );
                               } )
                               .build() );

        final Node modifiedNode = getNodeById( node.id() );

        assertTrue( modifiedNode.getTimestamp().isAfter( node.getTimestamp() ) );
    }

    @Test
    void modify_different_node_fields()
    {
        final ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );

        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );

        final Branch branch = ContextAccessor.current().getBranch();

        Node modifiedNode = nodeService.patch(
            PatchNodeParams.create().id( createdNode.id() ).addBranches( Branches.from( branch ) ).editor( toBeEdited -> {
                toBeEdited.data.addString( "another", "stuff" );
            } ).build() ).getResult( branch );

        assertNotEquals( createdNode.data(), modifiedNode.data() );

        modifiedNode = nodeService.patch(
            PatchNodeParams.create().id( createdNode.id() ).addBranches( Branches.from( branch ) ).editor( toBeEdited -> {
                toBeEdited.childOrder = ChildOrder.manualOrder();
            } ).build() ).getResult( branch );

        assertNotEquals( createdNode.getChildOrder(), modifiedNode.getChildOrder() );

        modifiedNode = nodeService.patch(
            PatchNodeParams.create().id( createdNode.id() ).addBranches( Branches.from( branch ) ).editor( toBeEdited -> {
                toBeEdited.indexConfigDocument = PatternIndexConfigDocument.create().defaultConfig( IndexConfig.FULLTEXT ).build();
            } ).build() ).getResult( branch );

        assertNotEquals( createdNode.getIndexConfigDocument(), modifiedNode.getIndexConfigDocument() );

        modifiedNode = nodeService.patch(
            PatchNodeParams.create().id( createdNode.id() ).addBranches( Branches.from( branch ) ).editor( toBeEdited -> {
                toBeEdited.manualOrderValue = -1L;
            } ).build() ).getResult( branch );

        assertNotEquals( createdNode.getManualOrderValue(), modifiedNode.getManualOrderValue() );

        modifiedNode = nodeService.patch(
            PatchNodeParams.create().id( createdNode.id() ).addBranches( Branches.from( branch ) ).editor( toBeEdited -> {
                toBeEdited.nodeType = NodeType.from( "NewType" );
            } ).build() ).getResult( branch );

        assertNotEquals( createdNode.getNodeType(), modifiedNode.getNodeType() );

        verify( eventPublisher, atLeastOnce() ).publish( captor.capture() );

        final List<Event> capturedEvents = captor.getAllValues();

        assertEquals( 5, capturedEvents.stream()
            .filter( event -> NodeEvents.NODE_UPDATED_EVENT.equals( event.getType() ) &&
                NodeId.from( ( (List<Map>) event.getData().get( EventConstants.NODES_FIELD ) ).getFirst().get( "id" ) )
                    .equals( createdNode.id() ) )
            .count() );
    }

    @Test
    void events_published_for_all_branches()
    {
        final ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );

        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );

        final Branch branch = ContextAccessor.current().getBranch();

        pushNodes( RepositoryConstants.MASTER_BRANCH, createdNode.id() );

        nodeService.patch( PatchNodeParams.create()
                               .id( createdNode.id() )
                               .addBranches( Branches.from( branch, RepositoryConstants.MASTER_BRANCH ) )
                               .editor( toBeEdited -> {
                                   toBeEdited.data.addString( "another", "stuff" );
                               } )
                               .build() );

        verify( eventPublisher, atLeastOnce() ).publish( captor.capture() );

        final List<Event> capturedEvents = captor.getAllValues();

        assertEquals( 1, capturedEvents.stream().filter( event -> {
            if ( !NodeEvents.NODE_UPDATED_EVENT.equals( event.getType() ) )
            {
                return false;
            }
            final Map node = ( (List<Map>) event.getData().get( EventConstants.NODES_FIELD ) ).getFirst();
            return NodeId.from( node.get( "id" ) ).equals( createdNode.id() ) && node.get( "branch" ).equals( branch.getValue() );
        } ).count() );

        assertEquals( 1, capturedEvents.stream().filter( event -> {
            if ( !NodeEvents.NODE_PUSHED_EVENT.equals( event.getType() ) )
            {
                return false;
            }
            final Map node = ( (List<Map>) event.getData().get( EventConstants.NODES_FIELD ) ).getFirst();
            return NodeId.from( node.get( "id" ) ).equals( createdNode.id() ) &&
                node.get( "branch" ).equals( RepositoryConstants.MASTER_BRANCH.getValue() );
        } ).count() );
    }

    @Test
    void modify_in_branches()
    {
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );

        final Branch branch = ContextAccessor.current().getBranch();

        final PatchNodeResult result = nodeService.patch( PatchNodeParams.create()
                                                              .id( createdNode.id() )
                                                              .addBranches( Branches.from( ContextAccessor.current().getBranch(),
                                                                                           RepositoryConstants.MASTER_BRANCH ) )
                                                              .editor( toBeEdited -> {
                                                                  toBeEdited.data.addString( "another", "stuff" );
                                                              } )
                                                              .build() );

        assertNotNull( result.getResult( branch ) );
        assertEquals( createdNode.id(), result.getNodeId() );
        assertNull( result.getResult( RepositoryConstants.MASTER_BRANCH ) );
    }

    @Test
    void modify_equal_versions()
    {
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );

        pushNodes( RepositoryConstants.MASTER_BRANCH, createdNode.id() );

        final Branch branch = ContextAccessor.current().getBranch();

        final PatchNodeResult result = nodeService.patch( PatchNodeParams.create()
                                                              .id( createdNode.id() )
                                                              .addBranches( Branches.from( branch, RepositoryConstants.MASTER_BRANCH ) )
                                                              .editor( toBeEdited -> {
                                                                  toBeEdited.data.addString( "another", "stuff" );
                                                              } )
                                                              .build() );

        assertEquals( result.getResult( branch ), result.getResult( RepositoryConstants.MASTER_BRANCH ) );
    }

    @Test
    void modify_not_equal_versions()
    {
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );

        pushNodes( RepositoryConstants.MASTER_BRANCH, createdNode.id() );

        updateNode( UpdateNodeParams.create().id( createdNode.id() ).editor( editableNode -> {
            editableNode.data.addString( "another", "stuff1" );
        } ).build() );

        final Branch branch = ContextAccessor.current().getBranch();

        final PatchNodeResult result = nodeService.patch( PatchNodeParams.create()
                                                              .id( createdNode.id() )
                                                              .addBranches( Branches.from( RepositoryConstants.MASTER_BRANCH ) )
                                                              .editor( toBeEdited -> {
                                                                  toBeEdited.data.addString( "another", "stuff2" );
                                                              } )
                                                              .build() );

        assertNotEquals( result.getResult( branch ), result.getResult( RepositoryConstants.MASTER_BRANCH ) );
    }

    @Test
    void modify_not_existing()
    {
        assertThrows( NodeNotFoundException.class, () -> nodeService.patch( PatchNodeParams.create()
                                                                                .id( NodeId.from( "non-existing" ) )
                                                                                .addBranches(
                                                                                    Branches.from( ContextAccessor.current().getBranch(),
                                                                                                   RepositoryConstants.MASTER_BRANCH ) )
                                                                                .editor( toBeEdited -> {
                                                                                    toBeEdited.data.addString( "another", "stuff2" );
                                                                                } )
                                                                                .build() ) );
    }

    @Test
    void modify_by_user_without_permissions_for_master()
    {
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );

        pushNodes( RepositoryConstants.MASTER_BRANCH, createdNode.id() );

        nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                          .nodeId( createdNode.id() )
                                          .addPermissions( AccessControlList.create()
                                                               .add( AccessControlEntry.create()
                                                                         .allowAll()
                                                                         .principal( User.anonymous().getKey() )
                                                                         .build() )
                                                               .build() )
                                          .build() );

        nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                          .nodeId( createdNode.id() )
                                          .addBranches( Branches.from( RepositoryConstants.MASTER_BRANCH ) )
                                          .addPermissions( AccessControlList.create()
                                                               .add( AccessControlEntry.create()
                                                                         .allow( Permission.READ )
                                                                         .principal( PrincipalKey.ofAnonymous() )
                                                                         .build() )
                                                               .build() )
                                          .build() );

        nodeService.refresh( RefreshMode.ALL );

        final Branch branch = ContextAccessor.current().getBranch();

        final Context userContext = ContextBuilder.from( ContextAccessor.current() )
            .authInfo( AuthenticationInfo.create().user( User.anonymous() ).build() )
            .build();

        final PatchNodeResult result = userContext.callWith( () -> nodeService.patch( PatchNodeParams.create()
                                                                                          .id( createdNode.id() )
                                                                                          .addBranches( Branches.from( branch,
                                                                                                                       RepositoryConstants.MASTER_BRANCH ) )
                                                                                          .editor( toBeEdited -> {
                                                                                              toBeEdited.data.addString( "another",
                                                                                                                         "stuff2" );
                                                                                          } )
                                                                                          .build() ) );

        assertEquals( "stuff2", result.getResult( branch ).data().getString( "another" ) );
        assertNull( result.getResult( RepositoryConstants.MASTER_BRANCH ) );
    }

    @Test
    void modify_by_user_without_permissions()
    {
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );

        final PrincipalKey userWithoutPermissions = PrincipalKey.from( "user:system:test" );

        nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                          .nodeId( createdNode.id() )
                                          .addPermissions( AccessControlList.create()
                                                               .add( AccessControlEntry.create()
                                                                         .allow( Permission.READ )
                                                                         .principal( userWithoutPermissions )
                                                                         .build() )
                                                               .build() )
                                          .build() );

        nodeService.refresh( RefreshMode.ALL );

        final Context userContext = ContextBuilder.from( ContextAccessor.current() )
            .authInfo( AuthenticationInfo.create()
                           .user( User.create().login( "test" ).displayName( "test" ).key( userWithoutPermissions ).build() )
                           .build() )
            .build();

        assertThrows( NodeAccessException.class, () -> userContext.callWith( () -> nodeService.patch( PatchNodeParams.create()
                                                                                                          .id( createdNode.id() )
                                                                                                          .addBranches( Branches.from(
                                                                                                              ContextAccessor.current()
                                                                                                                  .getBranch() ) )
                                                                                                          .editor( toBeEdited -> {
                                                                                                              toBeEdited.data.addString(
                                                                                                                  "another", "stuff2" );
                                                                                                          } )
                                                                                                          .build() ) ) );

    }

    @Test
    void modify_with_origin_branch_not_first()
    {
        final Branch branch1 = Branch.from( "branch1" );
        final Branch branch2 = Branch.from( "branch2" );
        final Branch branch3 = Branch.from( "branch3" );

        repositoryService.createBranch( CreateBranchParams.from( branch1.toString() ) );
        repositoryService.createBranch( CreateBranchParams.from( branch2.toString() ) );
        repositoryService.createBranch( CreateBranchParams.from( branch3.toString() ) );

        // Create node in branch1
        final Node createdNode = ContextBuilder.from( ContextAccessor.current() )
            .branch( branch1 )
            .build()
            .callWith( () -> createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() ) );

        // Push to branch2 and branch3
        ContextBuilder.from( ContextAccessor.current() )
            .branch( branch1 )
            .build()
            .runWith( () -> {
                pushNodes( branch2, createdNode.id() );
                pushNodes( branch3, createdNode.id() );
            } );

        // Modify in branch2 to make versions different
        ContextBuilder.from( ContextAccessor.current() )
            .branch( branch2 )
            .build()
            .runWith( () -> updateNode( UpdateNodeParams.create().id( createdNode.id() ).editor( editableNode -> {
                editableNode.data.addString( "field", "value_in_branch2" );
            } ).build() ) );

        // Now patch from branch2 (origin) to branch1 and branch3
        // branch2 is origin but not first in the list
        final PatchNodeResult result = ContextBuilder.from( ContextAccessor.current() )
            .branch( branch2 )
            .build()
            .callWith( () -> nodeService.patch( PatchNodeParams.create()
                                                   .id( createdNode.id() )
                                                   .addBranches( Branches.from( branch1, branch2, branch3 ) )
                                                   .editor( toBeEdited -> {
                                                       toBeEdited.data.addString( "patched", "from_branch2" );
                                                   } )
                                                   .build() ) );

        // Verify that branch1 and branch3 got the patched version from branch2
        final Node nodeInBranch1 = ContextBuilder.from( ContextAccessor.current() )
            .branch( branch1 )
            .build()
            .callWith( () -> getNodeById( createdNode.id() ) );

        final Node nodeInBranch3 = ContextBuilder.from( ContextAccessor.current() )
            .branch( branch3 )
            .build()
            .callWith( () -> getNodeById( createdNode.id() ) );

        // Both should have the patched field
        assertEquals( "from_branch2", nodeInBranch1.data().getString( "patched" ) );
        assertEquals( "from_branch2", nodeInBranch3.data().getString( "patched" ) );

        // And branch1 and branch3 should have the field from branch2 (because it was the origin)
        assertEquals( "value_in_branch2", nodeInBranch1.data().getString( "field" ) );
        assertEquals( "value_in_branch2", nodeInBranch3.data().getString( "field" ) );
    }
}
