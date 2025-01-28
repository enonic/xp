package com.enonic.xp.core.node;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.Event;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.PatchNodeResult;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.NodeEvents;
import com.enonic.xp.repository.RepositoryConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
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
    public void timestamp_updated_when_patching()
    {
        final PropertyTree data = new PropertyTree();

        final Node node = createNode( CreateNodeParams.create().name( "myNode" ).data( data ).parent( NodePath.ROOT ).build() );

        try
        {
            Thread.sleep( 2 );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }

        nodeService.patch( PatchNodeParams.create().id( node.id() ).editor( toBeEdited -> {
            toBeEdited.data.addString( "another", "stuff" );
        } ).build() );

        final Node patchedNode = getNodeById( node.id() );

        assertTrue( patchedNode.getTimestamp().isAfter( node.getTimestamp() ) );
    }

    @Test
    public void patch_by_path()
    {
        final PropertyTree data = new PropertyTree();

        final Node node = createNode( CreateNodeParams.create().name( "myNode" ).data( data ).parent( NodePath.ROOT ).build() );

        nodeService.patch( PatchNodeParams.create().path( node.path() ).editor( toBeEdited -> {
            toBeEdited.data.addString( "another", "stuff" );
        } ).build() );

        final Node patchedNode = getNodeById( node.id() );

        assertTrue( patchedNode.getTimestamp().isAfter( node.getTimestamp() ) );
    }

    @Test
    void patch_different_node_fields()
    {
        final ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );

        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );

        final Branch branch = ContextAccessor.current().getBranch();

        Node patchedNode = nodeService.patch( PatchNodeParams.create().id( createdNode.id() ).editor( toBeEdited -> {
            toBeEdited.data.addString( "another", "stuff" );
        } ).build() ).getResult( branch );

        assertNotEquals( createdNode.data(), patchedNode.data() );

        patchedNode = nodeService.patch( PatchNodeParams.create().id( createdNode.id() ).editor( toBeEdited -> {
            toBeEdited.childOrder = ChildOrder.manualOrder();
        } ).build() ).getResult( branch );

        assertNotEquals( createdNode.getChildOrder(), patchedNode.getChildOrder() );

        patchedNode = nodeService.patch( PatchNodeParams.create().id( createdNode.id() ).editor( toBeEdited -> {
            toBeEdited.indexConfigDocument = PatternIndexConfigDocument.create().defaultConfig( IndexConfig.FULLTEXT ).build();
        } ).build() ).getResult( branch );

        assertNotEquals( createdNode.getIndexConfigDocument(), patchedNode.getIndexConfigDocument() );

        patchedNode = nodeService.patch( PatchNodeParams.create().id( createdNode.id() ).editor( toBeEdited -> {
            toBeEdited.manualOrderValue = -1L;
        } ).build() ).getResult( branch );

        assertNotEquals( createdNode.getManualOrderValue(), patchedNode.getManualOrderValue() );

        patchedNode = nodeService.patch( PatchNodeParams.create().id( createdNode.id() ).editor( toBeEdited -> {
            toBeEdited.nodeType = NodeType.from( "NewType" );
        } ).build() ).getResult( branch );

        assertNotEquals( createdNode.getNodeType(), patchedNode.getNodeType() );

        verify( eventPublisher, times( 17 ) ).publish( captor.capture() );

        final List<Event> capturedEvents = captor.getAllValues();

        assertEquals( 5, capturedEvents.stream().filter( event -> NodeEvents.NODE_PATCHED_EVENT.equals( event.getType() ) ).count() );
    }

    @Test
    public void patch_in_branches()
    {
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );

        final Branch branch = ContextAccessor.current().getBranch();

        final PatchNodeResult result = nodeService.patch( PatchNodeParams.create()
                                                              .id( createdNode.id() )
                                                              .addBranches( Branches.from( RepositoryConstants.MASTER_BRANCH ) )
                                                              .editor( toBeEdited -> {
                                                                  toBeEdited.data.addString( "another", "stuff" );
                                                              } )
                                                              .build() );

        assertNotNull( result.getResult( branch ) );
        assertNull( result.getResult( RepositoryConstants.MASTER_BRANCH ) );
    }

    @Test
    public void patch_equal_versions()
    {
        final Node createdNode = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );

        pushNodes( RepositoryConstants.MASTER_BRANCH, createdNode.id() );

        final Branch branch = ContextAccessor.current().getBranch();

        final PatchNodeResult result = nodeService.patch( PatchNodeParams.create()
                                                              .id( createdNode.id() )
                                                              .addBranches( Branches.from( RepositoryConstants.MASTER_BRANCH ) )
                                                              .editor( toBeEdited -> {
                                                                  toBeEdited.data.addString( "another", "stuff" );
                                                              } )
                                                              .build() );

        assertEquals( result.getResult( branch ), result.getResult( RepositoryConstants.MASTER_BRANCH ) );
    }

    @Test
    public void patch_not_equal_versions()
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
    public void patch_not_existing()
    {
        assertThrows( NodeNotFoundException.class, () -> nodeService.patch( PatchNodeParams.create()
                                                                                .id( NodeId.from( "non-existing" ) )
                                                                                .addBranches(
                                                                                    Branches.from( RepositoryConstants.MASTER_BRANCH ) )
                                                                                .editor( toBeEdited -> {
                                                                                    toBeEdited.data.addString( "another", "stuff2" );
                                                                                } )
                                                                                .build() ) );
    }
}
