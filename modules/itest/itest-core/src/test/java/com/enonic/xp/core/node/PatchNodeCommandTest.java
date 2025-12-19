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
                                                                         .principal( User.ANONYMOUS.getKey() )
                                                                         .build() )
                                                               .build() )
                                          .build() );

        nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                          .nodeId( createdNode.id() )
                                          .addBranches( Branches.from( RepositoryConstants.MASTER_BRANCH ) )
                                          .addPermissions( AccessControlList.create()
                                                               .add( AccessControlEntry.create()
                                                                         .allow( Permission.READ )
                                                                         .principal( User.ANONYMOUS.getKey() )
                                                                         .build() )
                                                               .build() )
                                          .build() );

        nodeService.refresh( RefreshMode.ALL );

        final Branch branch = ContextAccessor.current().getBranch();

        final Context userContext =
            ContextBuilder.from( ContextAccessor.current() ).authInfo( AuthenticationInfo.create().user( User.ANONYMOUS ).build() ).build();

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
    void patch_three_branches_with_shared_versions()
    {
        // Create a third branch
        final Branch thirdBranch = Branch.from( "third-branch" );
        ctxDefaultAdmin().callWith( () -> {
            repositoryService.createBranch( CreateBranchParams.from( thirdBranch.getValue() ) );
            return null;
        } );

        final Branch draftBranch = ContextAccessor.current().getBranch();

        // Create node in draft
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );

        // Push to master and third branch - now all three have the same NodeVersionId
        pushNodes( RepositoryConstants.MASTER_BRANCH, createdNode.id() );
        pushNodes( thirdBranch, createdNode.id() );

        nodeService.refresh( RefreshMode.ALL );

        // Patch from draft context to all three branches
        // Since all branches share the same NodeVersionId, the patched version should be created once and reused
        final PatchNodeResult result = nodeService.patch( PatchNodeParams.create()
                                                              .id( createdNode.id() )
                                                              .addBranches( Branches.from( draftBranch, RepositoryConstants.MASTER_BRANCH, thirdBranch ) )
                                                              .editor( toBeEdited -> {
                                                                  toBeEdited.data.addString( "patched", "value" );
                                                              } )
                                                              .build() );

        // All branches should have the same patched result
        assertEquals( result.getResult( draftBranch ), result.getResult( RepositoryConstants.MASTER_BRANCH ) );
        assertEquals( result.getResult( draftBranch ), result.getResult( thirdBranch ) );
        assertEquals( "value", result.getResult( draftBranch ).data().getString( "patched" ) );
        assertEquals( "value", result.getResult( RepositoryConstants.MASTER_BRANCH ).data().getString( "patched" ) );
        assertEquals( "value", result.getResult( thirdBranch ).data().getString( "patched" ) );
    }

    @Test
    void patch_three_branches_origin_not_first_with_shared_versions()
    {
        // Create a third branch
        final Branch thirdBranch = Branch.from( "third-branch-2" );
        ctxDefaultAdmin().callWith( () -> {
            repositoryService.createBranch( CreateBranchParams.from( thirdBranch.getValue() ) );
            return null;
        } );

        final Branch draftBranch = ContextAccessor.current().getBranch();

        // Create node in draft
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );

        // Push to third branch - draft and third have the same NodeVersionId
        pushNodes( thirdBranch, createdNode.id() );

        // Update node in draft - now draft has a different version than third
        updateNode( UpdateNodeParams.create().id( createdNode.id() ).editor( editableNode -> {
            editableNode.data.addString( "updated", "in-draft" );
        } ).build() );

        // Push updated version to master - now draft and master share the same NodeVersionId
        pushNodes( RepositoryConstants.MASTER_BRANCH, createdNode.id() );

        nodeService.refresh( RefreshMode.ALL );

        // Patch from draft context with branches in order: [third, draft, master]
        // third is first but draft is the origin (context branch)
        // draft and master share version A, third has version B
        // The fix ensures that when patching master, it uses the origin from where the patch was created (draft),
        // not the first branch in the list (third)
        final PatchNodeResult result = nodeService.patch( PatchNodeParams.create()
                                                              .id( createdNode.id() )
                                                              .addBranches( Branches.from( thirdBranch, draftBranch, RepositoryConstants.MASTER_BRANCH ) )
                                                              .editor( toBeEdited -> {
                                                                  toBeEdited.data.addString( "patched", "from-draft" );
                                                              } )
                                                              .build() );

        // All branches should be patched
        assertNotNull( result.getResult( thirdBranch ) );
        assertNotNull( result.getResult( draftBranch ) );
        assertNotNull( result.getResult( RepositoryConstants.MASTER_BRANCH ) );

        // draft and master should have the same patched result (they shared the same original version)
        assertEquals( result.getResult( draftBranch ), result.getResult( RepositoryConstants.MASTER_BRANCH ) );

        // third had a different original version, so it gets a different patched result
        assertNotEquals( result.getResult( thirdBranch ), result.getResult( draftBranch ) );

        // All should have the patched value
        assertEquals( "from-draft", result.getResult( thirdBranch ).data().getString( "patched" ) );
        assertEquals( "from-draft", result.getResult( draftBranch ).data().getString( "patched" ) );
        assertEquals( "from-draft", result.getResult( RepositoryConstants.MASTER_BRANCH ).data().getString( "patched" ) );

        // draft and master should also have the "updated" value from the earlier update
        assertEquals( "in-draft", result.getResult( draftBranch ).data().getString( "updated" ) );
        assertEquals( "in-draft", result.getResult( RepositoryConstants.MASTER_BRANCH ).data().getString( "updated" ) );

        // third should NOT have the "updated" value (it had the older version)
        assertNull( result.getResult( thirdBranch ).data().getString( "updated" ) );
    }

    @Test
    void patch_three_branches_cached_version_from_non_first_branch()
    {
        // This test specifically covers the bug where cached patched versions used branches.first() as origin
        // instead of the actual branch where the version was created

        // Create third and fourth branches
        final Branch thirdBranch = Branch.from( "third-branch-3" );
        final Branch fourthBranch = Branch.from( "fourth-branch" );
        ctxDefaultAdmin().callWith( () -> {
            repositoryService.createBranch( CreateBranchParams.from( thirdBranch.getValue() ) );
            repositoryService.createBranch( CreateBranchParams.from( fourthBranch.getValue() ) );
            return null;
        } );

        final Branch draftBranch = ContextAccessor.current().getBranch();

        // Create node in draft
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );

        // Push to master - draft and master have version A
        pushNodes( RepositoryConstants.MASTER_BRANCH, createdNode.id() );

        // Update in draft
        updateNode( UpdateNodeParams.create().id( createdNode.id() ).editor( editableNode -> {
            editableNode.data.addString( "version", "B" );
        } ).build() );

        // Push to third and fourth - third and fourth have version B (same as draft)
        pushNodes( thirdBranch, createdNode.id() );
        pushNodes( fourthBranch, createdNode.id() );

        nodeService.refresh( RefreshMode.ALL );

        // Now: master has version A, draft/third/fourth have version B
        // Patch with order: [master, draft, third, fourth]
        // - master (version A) gets patched first, creates new patched version, cached with origin=master
        // - draft (version B) gets patched, creates new patched version, cached with origin=draft
        // - third (version B) finds cached version from draft, should push with origin=draft (not master!)
        // - fourth (version B) same as third

        final PatchNodeResult result = nodeService.patch( PatchNodeParams.create()
                                                              .id( createdNode.id() )
                                                              .addBranches( Branches.from( RepositoryConstants.MASTER_BRANCH, draftBranch, thirdBranch, fourthBranch ) )
                                                              .editor( toBeEdited -> {
                                                                  toBeEdited.data.addString( "patched", "yes" );
                                                              } )
                                                              .build() );

        // All should be patched
        assertNotNull( result.getResult( RepositoryConstants.MASTER_BRANCH ) );
        assertNotNull( result.getResult( draftBranch ) );
        assertNotNull( result.getResult( thirdBranch ) );
        assertNotNull( result.getResult( fourthBranch ) );

        // master had version A - different patched result
        assertNotEquals( result.getResult( RepositoryConstants.MASTER_BRANCH ), result.getResult( draftBranch ) );

        // draft, third, fourth all had version B - same patched result
        assertEquals( result.getResult( draftBranch ), result.getResult( thirdBranch ) );
        assertEquals( result.getResult( draftBranch ), result.getResult( fourthBranch ) );

        // All should have the patched value
        assertEquals( "yes", result.getResult( RepositoryConstants.MASTER_BRANCH ).data().getString( "patched" ) );
        assertEquals( "yes", result.getResult( draftBranch ).data().getString( "patched" ) );
        assertEquals( "yes", result.getResult( thirdBranch ).data().getString( "patched" ) );
        assertEquals( "yes", result.getResult( fourthBranch ).data().getString( "patched" ) );

        // Only version B branches should have the "version" field
        assertNull( result.getResult( RepositoryConstants.MASTER_BRANCH ).data().getString( "version" ) );
        assertEquals( "B", result.getResult( draftBranch ).data().getString( "version" ) );
        assertEquals( "B", result.getResult( thirdBranch ).data().getString( "version" ) );
        assertEquals( "B", result.getResult( fourthBranch ).data().getString( "version" ) );
    }
}
