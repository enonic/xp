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

public class ResolveNodesDependenciesCommandTest
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
    public void recursive_dependencies()
        throws Exception
    {
        final PropertyTree node1_data = new PropertyTree();
        node1_data.addReference( "myRef", Reference.from( "node2" ) );

        final Node node1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1" ) ).
            parent( NodePath.ROOT ).
            data( node1_data ).
            name( "node1" ).
            build() );

        final PropertyTree node1_1_data = new PropertyTree();
        node1_1_data.addReference( "myRef", Reference.from( "node2_1" ) );

        final Node node1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1" ) ).
            parent( node1.path() ).
            name( "node1_1" ).
            data( node1_1_data ).
            build() );

        final PropertyTree node2_data = new PropertyTree();
        node2_data.addReference( "myRef", Reference.from( "node1_1" ) );

        final Node node2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2" ) ).
            parent( NodePath.ROOT ).
            name( "node2" ).
            build() );

        final PropertyTree node2_1_data = new PropertyTree();
        node2_1_data.addReference( "myRef", Reference.from( "node1" ) );
        createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2_1" ) ).
            parent( node2.path() ).
            name( "node2_1" ).
            data( node2_1_data ).
            build() );

        final NodeIds dependants = ResolveNodesDependenciesCommand.create().
            recursive( true ).
            nodeIds( NodeIds.from( node1.id(), node1_1.id() ) ).
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            build().
            execute();

        assertEquals( "Should contain [node2(r),node2_1(r), contains " + dependants.getAsStrings(), 2, dependants.getSize() );


    }
}