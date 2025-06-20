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
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.PatchNodeResult;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.NodeEvents;
import com.enonic.xp.repository.RepositoryConstants;
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

public class PatchNodeCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    public void timestamp_updated_when_modifying()
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
                NodeId.from( ( (List<Map>) event.getData().get( "nodes" ) ).getFirst().get( "id" ) ).equals( createdNode.id() ) )
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
            final Map node = ( (List<Map>) event.getData().get( "nodes" ) ).getFirst();
            return NodeId.from( node.get( "id" ) ).equals( createdNode.id() ) && node.get( "branch" ).equals( branch.getValue() );
        } ).count() );

        assertEquals( 1, capturedEvents.stream().filter( event -> {
            if ( !NodeEvents.NODE_PUSHED_EVENT.equals( event.getType() ) )
            {
                return false;
            }
            final Map node = ( (List<Map>) event.getData().get( "nodes" ) ).getFirst();
            return NodeId.from( node.get( "id" ) ).equals( createdNode.id() ) &&
                node.get( "branch" ).equals( RepositoryConstants.MASTER_BRANCH.getValue() );
        } ).count() );
    }

    @Test
    public void modify_in_branches()
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
    public void modify_equal_versions()
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
    public void modify_not_equal_versions()
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
    public void modify_not_existing()
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
    public void modify_by_user_without_permissions()
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

        ContextBuilder.copyOf( ContextAccessor.current() ).branch( RepositoryConstants.MASTER_BRANCH );
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
}
