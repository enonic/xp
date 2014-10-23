package com.enonic.wem.core.entity;

import java.time.Instant;
import java.time.LocalDate;

import org.junit.Test;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.index.IndexConfig;
import com.enonic.wem.api.index.PatternIndexConfigDocument;

import static org.junit.Assert.*;

public class NodeEditTest
{

    @Test
    public void edit_nothing()
        throws Exception
    {
        final Node node = createNode();

        final Node editedNode = Node.editNode( node ).build();

        assertEquals( node, editedNode );
    }

    @Test
    public void edit_name()
        throws Exception
    {
        final Node node = createNode();

        final NodeName newName = NodeName.from( "newname" );

        final Node editedNode = Node.editNode( node ).
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

        final Node editedNode = Node.editNode( node ).
            indexConfigDocument( newIndexConfig ).
            build();

        assertEquals( newIndexConfig, editedNode.getIndexConfigDocument() );

    }


    private Node createNode()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( DataPath.from( "a.b.c" ), Value.newDouble( 2.0 ) );
        rootDataSet.setProperty( DataPath.from( "b" ), Value.newLocalDate( LocalDate.now() ) );
        rootDataSet.setProperty( DataPath.from( "c" ), Value.newString( "runar" ) );

        return Node.newNode().
            id( NodeId.from( "node" ) ).
            parent( NodePath.ROOT ).
            name( NodeName.from( "mynode" ) ).
            creator( UserKey.superUser() ).
            createdTime( Instant.now() ).
            rootDataSet( rootDataSet ).
            childOrder( ChildOrder.from( "modifiedTime DESC" ) ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                defaultConfig( IndexConfig.MINIMAL ).
                build() ).
            build();
    }
}