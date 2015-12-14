package com.enonic.xp.repo.impl.node;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.util.Reference;

import static org.junit.Assert.*;

public class GetOutgoingReferencesCommandTest
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
    public void multiple_references()
        throws Exception
    {
        PropertyTree data = new PropertyTree();

        data.addReference( "myRef1", new Reference( NodeId.from( "node1Id" ) ) );
        data.addReference( "myRef2", new Reference( NodeId.from( "node2Id" ) ) );

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            data( data ).
            build() );

        printBranchIndex();

        final NodeIds references = GetOutgoingReferencesCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            nodeId( node.id() ).
            build().
            execute();

        assertEquals( 2, references.getSize() );
    }
}