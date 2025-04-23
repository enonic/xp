package com.enonic.xp.lib.node;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.PatchNodeResult;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.util.GeoPoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

public class PatchNodeHandlerTest
    extends BaseNodeHandlerTest
{
    @Captor
    private ArgumentCaptor<PatchNodeParams> patchCaptor;

    @Test
    public void nodeNotFound()
    {
        when( nodeService.patch( isA( PatchNodeParams.class ) ) )
            .thenReturn( PatchNodeResult.create().addResult( ContentConstants.BRANCH_MASTER, mock( Node.class ) ).build() );

        assertThrows( NodeNotFoundException.class, () -> runScript( "/lib/xp/examples/node/patch-1.js" ) );
    }

    @Test
    public void testExample1()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "notChanged", "originalValue" );
        data.setString( "myString", "originalValue" );
        data.setString( "toBeRemoved", "removeThis" );
        final PropertySet mySet = data.addSet( "mySet" );
        mySet.setGeoPoint( "myGeoPoint", new GeoPoint( 30, -30 ) );

        final Node node = Node.create()
            .id( NodeId.from( "a" ) )
            .parentPath( NodePath.ROOT )
            .data( data )
            .name( "myNode" )
            .nodeState( NodeState.DEFAULT )
            .permissions(
                AccessControlList.of( AccessControlEntry.create().allow( Permission.READ ).principal( RoleKeys.EVERYONE ).build() ) )
            .build();

        when( nodeService.getById( eq( node.id() ) ) ).thenReturn( node );

        when( nodeService.patch( isA( PatchNodeParams.class ) ) )
            .thenReturn( PatchNodeResult.create()
                             .addResult( ContentConstants.BRANCH_MASTER, node )
                             .addResult( ContentConstants.BRANCH_DRAFT, node )
                             .build() );

        runScript( "/lib/xp/examples/node/patch-1.js" );

        Mockito.verify( this.nodeService ).patch( patchCaptor.capture() );

        EditableNode editedNodeBuilder = new EditableNode( node );
        patchCaptor.getValue().getEditor().edit( editedNodeBuilder );
        Node editedNode = editedNodeBuilder.build();

        assertNotNull( editedNode.data().getSet( "mySet" ) );
        assertThat( editedNode.data().getStrings( "myArray" ) ).contains( "modified1", "modified2", "modified3" );
        assertEquals( "updatedorderfield DESC", editedNode.getChildOrder().toString() );
    }
}

