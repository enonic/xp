package com.enonic.wem.repo.internal.entity;

import java.time.LocalDate;

import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;

import static org.junit.Assert.*;

public class NodeEditTest
{
    @Test
    public void edit_nothing()
        throws Exception
    {
        final Node node = createNode();

        final Node editedNode = Node.newNode( node ).build();

        assertEquals( node, editedNode );
    }

    @Test
    public void edit_name()
        throws Exception
    {
        final Node node = createNode();

        final NodeName newName = NodeName.from( "newname" );

        final Node editedNode = Node.newNode( node ).
            name( newName ).
            build();

        assertEquals( newName, editedNode.name() );
    }

    @Test
    public void edit_index_config()
        throws Exception
    {
        final Node node = createNode();

        final PatternIndexConfigDocument newIndexConfig = PatternIndexConfigDocument.create().
            defaultConfig( IndexConfig.FULLTEXT ).
            build();

        final Node editedNode = Node.newNode( node ).
            indexConfigDocument( newIndexConfig ).
            build();

        assertEquals( newIndexConfig, editedNode.getIndexConfigDocument() );

    }


    private Node createNode()
    {
        PropertyTree rootDataSet = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        rootDataSet.setDouble( "a.b.c", 2.0 );
        rootDataSet.setLocalDate( "b", LocalDate.now() );
        rootDataSet.setString( "c", "runar" );

        return Node.newNode().
            id( NodeId.from( "node" ) ).
            parentPath( NodePath.ROOT ).
            name( NodeName.from( "mynode" ) ).
            data( rootDataSet ).
            childOrder( ChildOrder.from( "modifiedTime DESC" ) ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                defaultConfig( IndexConfig.MINIMAL ).
                build() ).
            build();
    }
}