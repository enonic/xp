package com.enonic.xp.lib.node;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeEditor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.util.GeoPoint;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ModifyNodeHandlerTest
    extends BaseNodeHandlerTest
{
    @Captor
    private ArgumentCaptor<UpdateNodeParams> updateCaptor;

    private Node mockGetNode()
    {
        final Node node = doCreateNode();
        Mockito.when( this.nodeService.getById( Mockito.isA( NodeId.class ) ) ).
            thenReturn( node );

        return node;
    }

    private Node mockUpdateNode()
    {
        final Node node = doCreateNode();

        Mockito.when( this.nodeService.update( Mockito.isA( UpdateNodeParams.class ) ) ).
            then( new Answer<Node>()
            {
                @Override
                public Node answer( InvocationOnMock invocation )
                    throws Throwable
                {
                    final UpdateNodeParams updateNodeParams = (UpdateNodeParams) invocation.getArguments()[0];

                    final EditableNode editableNode = new EditableNode( node );
                    updateNodeParams.getEditor().edit( editableNode );

                    final Node editedNode = editableNode.build();

                    final Node.Builder builder = Node.create( editedNode );

                    final BinaryAttachments binaryAttachments = updateNodeParams.getBinaryAttachments();

                    final AttachedBinaries.Builder binariesBuilder = AttachedBinaries.create();

                    for ( final BinaryAttachment binaryAttachment : binaryAttachments )
                    {
                        binariesBuilder.add( new AttachedBinary( binaryAttachment.getReference(), "fisk" ) );
                    }

                    return builder.
                        attachedBinaries( binariesBuilder.build() ).
                        build();
                }
            } );

        return node;
    }

    private Node doCreateNode()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "notChanged", "originalValue" );
        data.setString( "myString", "originalValue" );
        data.setString( "toBeRemoved", "removeThis" );
        final PropertySet mySet = data.addSet( "mySet" );
        mySet.setGeoPoint( "myGeoPoint", new GeoPoint( 30, -30 ) );

        return Node.create().
            id( NodeId.from( "abc" ) ).
            parentPath( NodePath.ROOT ).
            data( data ).
            name( "myNode" ).
            build();
    }

    @Test
    public void testExample()
    {
        final Node node = mockGetNode();
        mockUpdateNode();

        Mockito.when( this.repositoryService.get( RepositoryId.from( "cms-repo" ) ) ).
            thenReturn( Repository.create().
                id( RepositoryId.from( "cms-repo" ) ).
                branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
                build() );

        runScript( "/site/lib/xp/examples/node/modify.js" );

        Mockito.verify( this.nodeService ).update( updateCaptor.capture() );
        assertEquals( updateCaptor.getValue().getId(), NodeId.from( "abc" ) );
        assertEditor( node );
    }

    private void assertEditor( final Node node )
    {
        final NodeEditor editor = updateCaptor.getValue().getEditor();

        assertNotNull( editor );

        final EditableNode editableNode = new EditableNode( node );
        editor.edit( editableNode );
        assertEquals( "modified", editableNode.data.getString( "myString" ) );
        assertEquals( "originalValue", editableNode.data.getString( "notChanged" ) );
        assertEquals( new GeoPoint( 0, 0 ), editableNode.data.getGeoPoint( "mySet.myGeoPoint" ) );
        final Iterable<String> myArray = editableNode.data.getStrings( "myArray" );
        assertNotNull( myArray );
        final ArrayList<String> myArrayValues = Lists.newArrayList( myArray );
        assertEquals( 3, myArrayValues.size() );
        assertTrue( myArrayValues.containsAll( Lists.newArrayList( "modified1", "modified2", "modified3" ) ) );

        final AccessControlList permissions = editableNode.permissions;
        assertTrue( permissions.getEntry( PrincipalKey.from( "role:newRole" ) ).isAllowed( Permission.MODIFY ) );
        assertTrue( permissions.getEntry( PrincipalKey.from( "user:system:newUser" ) ).isAllowed( Permission.CREATE ) );

        final IndexConfigDocument indexConfigDocument = editableNode.indexConfigDocument;
        assertFalse( indexConfigDocument.getConfigForPath( PropertyPath.from( "displayName" ) ).isEnabled() );
        assertTrue( indexConfigDocument.getConfigForPath( PropertyPath.from( "whatever" ) ).isFulltext() );
    }

    @SuppressWarnings("unused")
    public static ByteSource createByteSource( final String value )
    {
        return ByteSource.wrap( value.getBytes() );
    }
}