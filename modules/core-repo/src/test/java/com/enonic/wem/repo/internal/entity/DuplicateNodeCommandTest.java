package com.enonic.wem.repo.internal.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.util.Reference;

import static org.junit.Assert.*;

public class DuplicateNodeCommandTest
    extends AbstractNodeTest
{
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Test
    public void duplicate_single()
        throws Exception
    {
        final String nodeName = "my-node";
        final Node createdNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( nodeName ).
            build() );

        final Node duplicatedNode = DuplicateNodeCommand.create().
            id( createdNode.id() ).
            versionService( versionService ).
            indexServiceInternal( indexServiceInternal ).
            nodeDao( nodeDao ).
            queryService( queryService ).
            branchService( branchService ).
            binaryBlobStore( binaryBlobStore ).
            build().
            execute();

        assertEquals( nodeName + "-" + DuplicateValueResolver.COPY_TOKEN, duplicatedNode.name().toString() );
    }

    @Test
    public void duplicate_with_children()
        throws Exception
    {
        final String nodeName = "my-node";
        final Node createdNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( nodeName ).
            build() );

        final Node childNode = createNode( CreateNodeParams.create().
            parent( createdNode.path() ).
            name( "my-child" ).
            build() );

        refresh();

        final Node duplicatedNode = DuplicateNodeCommand.create().
            id( createdNode.id() ).
            versionService( versionService ).
            indexServiceInternal( indexServiceInternal ).
            nodeDao( nodeDao ).
            queryService( queryService ).
            branchService( branchService ).
            binaryBlobStore( binaryBlobStore ).
            build().
            execute();

        refresh();

        final FindNodesByParentResult children = findByParent( FindNodesByParentParams.create().
            parentPath( duplicatedNode.path() ).
            build() );

        final Nodes childNodes = children.getNodes();
        assertEquals( 1, childNodes.getSize() );
        assertEquals( childNode.name(), childNodes.first().name() );
    }

    @Test
    public void child_reference_updated_same_level_untouched()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node1" ).
            data( createDataWithReferences( Reference.from( "node1_1-id" ), Reference.from( "node2-id" ) ) ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            setNodeId( NodeId.from( "node2-id" ) ).
            name( "node2" ).
            build() );

        final Node node1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1-id" ) ).
            parent( node1.path() ).
            name( "node1_1" ).
            build() );

        final Node node1Duplicate = duplicateNode( node1 );

        final Node dNode1_1 = getNodeByPath( NodePath.newNodePath( node1Duplicate.path(), node1_1.name().toString() ).build() );
        assertEquals( dNode1_1.id(), node1Duplicate.data().getReference( "node1_1-id" ).getNodeId() );
        assertEquals( node2.id(), node1Duplicate.data().getReference( "node2-id" ).getNodeId() );
    }

    @Test
    public void subchild_reference_updated()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node1" ).
            data( createDataWithReferences( Reference.from( "node1_1-id" ), Reference.from( "node1_1_1-id" ) ) ).
            build() );

        final Node node1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1-id" ) ).
            parent( node1.path() ).
            name( "node1_1" ).
            data( createDataWithReferences( Reference.from( "node1_1_1-id" ) ) ).
            build() );

        final Node node_1_1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1_1-id" ) ).
            parent( node1_1.path() ).
            name( "node1_1_1" ).
            build() );

        refresh();

        final Node node1Duplicate = duplicateNode( node1 );
        final Node dNode1_1 = getNodeByPath( NodePath.newNodePath( node1Duplicate.path(), node1_1.name().toString() ).build() );

        final Reference reference = node1Duplicate.data().getReference( "node1_1-id" );

        assertEquals( dNode1_1.id(), reference.getNodeId() );

        final Node dNode1_1_1 = getNodeByPath( NodePath.newNodePath( dNode1_1.path(), node_1_1_1.name().toString() ).build() );
        assertEquals( dNode1_1.id(), node1Duplicate.data().getReference( "node1_1-id" ).getNodeId() );
        assertEquals( dNode1_1_1.id(), dNode1_1.data().getReference( "node1_1_1-id" ).getNodeId() );
        assertEquals( dNode1_1_1.id(), node1Duplicate.data().getReference( "node1_1_1-id" ).getNodeId() );
    }

    @Test
    public void child_in_other_branch_updated_reference()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node1" ).
            build() );

        final Node node1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1-id" ) ).
            parent( node1.path() ).
            name( "node1_1" ).
            data( createDataWithReferences( Reference.from( "node1_2_1-id" ) ) ).
            build() );

        final Node node1_2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_2-id" ) ).
            parent( node1.path() ).
            name( "node1_2" ).
            data( createDataWithReferences( Reference.from( "node1_1_1-id" ) ) ).
            build() );

        final Node node1_1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1_1-id" ) ).
            parent( node1_1.path() ).
            name( "node1_1_1" ).
            data( createDataWithReferences( Reference.from( "node1_2-id" ) ) ).
            data( createDataWithReferences( Reference.from( "node1_2_1-id" ) ) ).
            build() );

        final Node node1_2_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_2_1-id" ) ).
            parent( node1_2.path() ).
            name( "node1_2_1" ).
            data( createDataWithReferences( Reference.from( "node1_1-id" ) ) ).
            data( createDataWithReferences( Reference.from( "node1_1_1-id" ) ) ).
            build() );

        refresh();

        final Node duplicatedNode1 = duplicateNode( node1 );

        final Node dNode1_1 = getNodeByPath( NodePath.newNodePath( duplicatedNode1.path(), node1_1.name().toString() ).build() );
        final Node dNode1_2 = getNodeByPath( NodePath.newNodePath( duplicatedNode1.path(), node1_2.name().toString() ).build() );
        final Node dNode1_1_1 = getNodeByPath( NodePath.newNodePath( dNode1_1.path(), node1_1_1.name().toString() ).build() );
        final Node dNode1_2_1 = getNodeByPath( NodePath.newNodePath( dNode1_2.path(), node1_2_1.name().toString() ).build() );
        assertNotNull( dNode1_1 );
        assertNotNull( dNode1_2 );
        assertNotNull( dNode1_1_1 );
        assertNotNull( dNode1_2_1 );

        final Reference node1_1_1_ref_to_1_2_1 = dNode1_1_1.data().getReference( "node1_2_1-id" );
        final Reference node1_2_1_ref_to_1_1_1 = dNode1_2_1.data().getReference( "node1_1_1-id" );
        assertNotNull( node1_1_1_ref_to_1_2_1 );
        assertNotNull( node1_2_1_ref_to_1_1_1 );
        assertEquals( dNode1_2_1.id(), node1_1_1_ref_to_1_2_1.getNodeId() );
        assertEquals( dNode1_1_1.id(), node1_2_1_ref_to_1_1_1.getNodeId() );
    }

    @Test
    public void parent_relation_updated()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1-id" ) ).
            parent( NodePath.ROOT ).
            name( "node1" ).
            build() );

        final Node node1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1-id" ) ).
            parent( node1.path() ).
            name( "node1_1" ).
            data( createDataWithReferences( Reference.from( "node1-id" ) ) ).
            build() );

        final Node node1Duplicate = duplicateNode( node1 );

        final Node dNode1_1 = getNodeByPath( NodePath.newNodePath( node1Duplicate.path(), node1_1.name().toString() ).build() );

        assertEquals( node1Duplicate.id(), dNode1_1.data().getReference( "node1-id" ).getNodeId() );
    }

    private Node duplicateNode( final Node node1 )
    {
        return DuplicateNodeCommand.create().
            id( node1.id() ).
            versionService( versionService ).
            indexServiceInternal( indexServiceInternal ).
            nodeDao( nodeDao ).
            queryService( queryService ).
            branchService( branchService ).
            binaryBlobStore( this.binaryBlobStore ).
            build().
            execute();
    }

    private PropertyTree createDataWithReferences( final Reference... references )
    {
        PropertyTree data = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        for ( final Reference reference : references )
        {
            data.setReference( reference.getNodeId().toString(), reference );
        }

        return data;
    }
}
