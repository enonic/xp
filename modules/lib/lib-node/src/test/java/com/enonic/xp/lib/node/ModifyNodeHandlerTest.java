package com.enonic.xp.lib.node;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
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
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Link;
import com.enonic.xp.util.Reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ModifyNodeHandlerTest
    extends BaseNodeHandlerTest
{
    @Captor
    private ArgumentCaptor<UpdateNodeParams> updateCaptor;

    private void mockUpdateNode( final Node originalNode )
    {
        Mockito.when( this.nodeService.update( Mockito.isA( UpdateNodeParams.class ) ) ).
            then( new Answer<Node>()
            {
                @Override
                public Node answer( InvocationOnMock invocation )
                    throws Throwable
                {
                    final UpdateNodeParams updateNodeParams = (UpdateNodeParams) invocation.getArguments()[0];
                    final EditableNode editableNode = new EditableNode( originalNode );
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
    }

    private void mockGetNode( final Node node )
    {
        Mockito.when( this.nodeService.getById( Mockito.isA( NodeId.class ) ) ).
            thenReturn( node );
    }

    @Test
    public void testExample()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "notChanged", "originalValue" );
        data.setString( "myString", "originalValue" );
        data.setString( "toBeRemoved", "removeThis" );
        final PropertySet mySet = data.addSet( "mySet" );
        mySet.setGeoPoint( "myGeoPoint", new GeoPoint( 30, -30 ) );

        final Node node = Node.create().
            id( NodeId.from( "abc" ) ).
            parentPath( NodePath.ROOT ).
            data( data ).
            name( "myNode" ).
            build();

        mockGetNode( node );
        mockUpdateNode( node );

        runScript( "/lib/xp/examples/node/modify.js" );

        Mockito.verify( this.nodeService ).update( updateCaptor.capture() );
        assertEquals( updateCaptor.getValue().getId(), NodeId.from( "abc" ) );

        final EditableNode editedNode = getEditedNode( node );
        assertEquals( "modified", editedNode.data.getString( "myString" ) );
        assertEquals( "originalValue", editedNode.data.getString( "notChanged" ) );
        assertEquals( new GeoPoint( 0, 0 ), editedNode.data.getGeoPoint( "mySet.myGeoPoint" ) );
        final Iterable<String> myArray = editedNode.data.getStrings( "myArray" );
        assertNotNull( myArray );
        final ArrayList<String> myArrayValues = Lists.newArrayList( myArray );
        assertEquals( 3, myArrayValues.size() );
        assertTrue( myArrayValues.containsAll( List.of( "modified1", "modified2", "modified3" ) ) );

        final AccessControlList permissions = editedNode.permissions;
        assertTrue( permissions.getEntry( PrincipalKey.from( "role:newRole" ) ).isAllowed( Permission.MODIFY ) );
        assertTrue( permissions.getEntry( PrincipalKey.from( "user:system:newUser" ) ).isAllowed( Permission.CREATE ) );

        final IndexConfigDocument indexConfigDocument = editedNode.indexConfigDocument;
        assertFalse( indexConfigDocument.getConfigForPath( PropertyPath.from( "displayName" ) ).isEnabled() );
        assertTrue( indexConfigDocument.getConfigForPath( PropertyPath.from( "whatever" ) ).isFulltext() );
    }

    @Test
    public void keep_original_value_types_when_not_touched()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "myString", "originalValue" );
        data.setString( "untouchedString", "originalValue" );
        data.setBoolean( "untouchedBoolean", true );
        data.setDouble( "untouchedDouble", 2.0 );
        data.setLong( "untouchedLong", 2L );
        data.setLink( "untouchedLink", Link.from( "myLink" ) );
        data.setInstant( "untouchedInstant", Instant.parse( "2017-01-02T10:00:00Z" ) );
        data.setBinaryReference( "untouchedBinaryRef", BinaryReference.from( "abcd" ) );
        data.setGeoPoint( "untouchedGeoPoint", GeoPoint.from( "30,-30" ) );
        data.setLocalDate( "untouchedLocalDate", LocalDate.parse( "2017-03-24" ) );
        data.setReference( "untouchedReference", Reference.from( "myReference" ) );

        final Node node = Node.create().
            id( NodeId.from( "abc" ) ).
            parentPath( NodePath.ROOT ).
            data( data ).
            name( "myNode" ).
            build();

        mockGetNode( node );
        mockUpdateNode( node );

        runScript( "/lib/xp/examples/node/modify-keep-types.js" );

        Mockito.verify( this.nodeService ).update( updateCaptor.capture() );
        assertEquals( updateCaptor.getValue().getId(), NodeId.from( "abc" ) );

        final EditableNode editedNode = getEditedNode( node );
        assertEquals( "modifiedValue", editedNode.data.getString( "myString" ) );
        // Validate that properties not changed keeps original type
        assertTrue( editedNode.data.getProperty( "untouchedString" ).getType().equals( ValueTypes.STRING ) );
        assertTrue( editedNode.data.getProperty( "untouchedBoolean" ).getType().equals( ValueTypes.BOOLEAN ) );
        assertTrue( editedNode.data.getProperty( "untouchedDouble" ).getType().equals( ValueTypes.DOUBLE ) );
        assertTrue( editedNode.data.getProperty( "untouchedLong" ).getType().equals( ValueTypes.LONG ) );
        assertTrue( editedNode.data.getProperty( "untouchedLink" ).getType().equals( ValueTypes.LINK ) );
        assertTrue( editedNode.data.getProperty( "untouchedInstant" ).getType().equals( ValueTypes.DATE_TIME ) );
        assertTrue( editedNode.data.getProperty( "untouchedGeoPoint" ).getType().equals( ValueTypes.GEO_POINT ) );
        assertTrue( editedNode.data.getProperty( "untouchedLocalDate" ).getType().equals( ValueTypes.LOCAL_DATE ) );
        assertTrue( editedNode.data.getProperty( "untouchedReference" ).getType().equals( ValueTypes.REFERENCE ) );
        assertTrue( editedNode.data.getProperty( "untouchedBinaryRef" ).getType().equals( ValueTypes.BINARY_REFERENCE ) );
    }

    private EditableNode getEditedNode( final Node node )
    {
        final NodeEditor editor = updateCaptor.getValue().getEditor();
        assertNotNull( editor );

        final EditableNode editableNode = new EditableNode( node );
        editor.edit( editableNode );

        return editableNode;
    }

    @SuppressWarnings("unused")
    public static ByteSource createByteSource( final String value )
    {
        return ByteSource.wrap( value.getBytes() );
    }
}
