package com.enonic.xp.repo.impl.node;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Strings;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.util.Reference;

import static org.junit.Assert.*;

public class FindNodesDependenciesCommandTest
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
    public void several_layers_of_dependencies()
        throws Exception
    {

        final Node node1 = createNodeWithReference( "n1", NodePath.ROOT, "n1_1" );
        final Node node1_1 = createNodeWithReference( "n1_1", node1.path(), "n1_1_1" );
        createNodeWithReference( "n1_1_1", node1_1.path(), null );

        final NodeIds dependants = FindNodesDependenciesCommand.create().
            recursive( true ).
            nodeIds( NodeIds.from( node1.id() ) ).
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            build().
            execute();

        assertEquals( "Should contain [node1_1(r),node1_1_1(r), contains " + dependants.getAsStrings(), 2, dependants.getSize() );
    }

    @Test
    public void several_layers_of_dependencies_non_recursive()
        throws Exception
    {

        final Node node1 = createNodeWithReference( "n1", NodePath.ROOT, "n1_1" );
        final Node node1_1 = createNodeWithReference( "n1_1", node1.path(), "n1_1_1" );
        createNodeWithReference( "n1_1_1", node1_1.path(), null );

        final NodeIds dependants = FindNodesDependenciesCommand.create().
            recursive( false ).
            nodeIds( NodeIds.from( node1.id() ) ).
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            build().
            execute();

        assertEquals( "Should contain [node1_1(r)], contains " + dependants.getAsStrings(), 1, dependants.getSize() );
    }


    @Test
    public void lopping_dependencies()
        throws Exception
    {

        final Node node1 = createNodeWithReference( "n1", NodePath.ROOT, "n1_1" );
        final Node node1_1 = createNodeWithReference( "n1_1", node1.path(), "n1_1_1" );
        createNodeWithReference( "n1_1_1", node1_1.path(), "n1" );

        final NodeIds dependants = FindNodesDependenciesCommand.create().
            recursive( true ).
            nodeIds( NodeIds.from( node1.id() ) ).
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            build().
            execute();

        assertEquals( "Should contain [node1_1(r),node1_1_1(r), contains " + dependants.getAsStrings(), 2, dependants.getSize() );
    }


    private Node createNodeWithReference( final String nodeId, final NodePath parent, final String referenceTo )
    {
        final PropertyTree nodeData = new PropertyTree();

        if ( !Strings.isNullOrEmpty( referenceTo ) )
        {
            nodeData.addReference( "myRef", Reference.from( referenceTo ) );
        }

        return createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( nodeId ) ).
            parent( parent ).
            data( nodeData ).
            name( nodeId ).
            build() );
    }


}