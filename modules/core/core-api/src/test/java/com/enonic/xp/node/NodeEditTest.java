package com.enonic.xp.node;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NodeEditTest
{
    @Test
    void edit_nothing()
    {
        final Node node = createNode();

        final Node editedNode = Node.create( node ).build();

        assertEquals( node, editedNode );
    }

    @Test
    void edit_name()
    {
        final Node node = createNode();

        final NodeName newName = NodeName.from( "newname" );

        final Node editedNode = Node.create( node ).
            name( newName ).
            build();

        assertEquals( newName, editedNode.name() );
    }

    @Test
    void edit_index_config()
    {
        final Node node = createNode();

        final PatternIndexConfigDocument newIndexConfig = PatternIndexConfigDocument.create().
            defaultConfig( IndexConfig.FULLTEXT ).
            build();

        final Node editedNode = Node.create( node ).
            indexConfigDocument( newIndexConfig ).
            build();

        assertEquals( newIndexConfig, editedNode.getIndexConfigDocument() );

    }


    private Node createNode()
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.setDouble( "a.b.c", 2.0 );
        rootDataSet.setLocalDate( "b", LocalDate.now() );
        rootDataSet.setString( "c", "runar" );

        return Node.create().
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
