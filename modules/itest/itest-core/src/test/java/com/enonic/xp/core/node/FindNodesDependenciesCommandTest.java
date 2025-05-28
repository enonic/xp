package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.node.CompareNodesCommand;
import com.enonic.xp.repo.impl.node.FindNodesDependenciesCommand;
import com.enonic.xp.util.Reference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FindNodesDependenciesCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    void several_layers_of_dependencies()
    {
        final Node node1 = createNodeWithReference( "n1", NodePath.ROOT, "n1_1" );
        final Node node1_1 = createNodeWithReference( "n1_1", node1.path(), "n1_1_1" );
        createNodeWithReference( "n1_1_1", node1_1.path() );

        nodeService.refresh( RefreshMode.ALL );

        final NodeIds dependants = FindNodesDependenciesCommand.create().
            nodeIds( NodeIds.from( node1.id() ) ).
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            build().
            execute();

        assertThat( dependants ).containsExactly( NodeId.from( "n1_1" ), NodeId.from( "n1_1_1" ) );
    }

    @Test
    public void several_layers_of_dependencies_stopped_by_status()
        throws Exception
    {
        final Node node1 = createNodeWithReference( "n1", NodePath.ROOT, "n1_1" );
        final Node node1_1 = createNodeWithReference( "n1_1", node1.path(), "n1_1_1" );
        createNodeWithReference( "n1_1_1", node1_1.path() );

        nodeService.refresh( RefreshMode.ALL );

        final NodeIds dependants = FindNodesDependenciesCommand.create().
            nodeIds( NodeIds.from( node1.id() ) ).
            indexServiceInternal( this.indexServiceInternal ).searchService( this.searchService )
            .storageService( this.storageService )
            .filter( nodeIds -> {
                final NodeIds.Builder filteredNodeIds = NodeIds.create();
                final NodeComparisons currentLevelNodeComparisons = CompareNodesCommand.create().
                    nodeIds( nodeIds ).
                    storageService( this.storageService ).
                    target( ContextAccessor.current().getBranch() ).
                    build().
                    execute();
                nodeIds.stream().
                    filter( nodeId -> !CompareStatus.EQUAL.equals( currentLevelNodeComparisons.get( nodeId ).getCompareStatus() ) ).
                    forEach( filteredNodeIds::add );
                return filteredNodeIds.build();
            } ).
            build().
            execute();

        assertThat( dependants ).isEmpty();
    }

    @Test
    public void looping_dependencies()
        throws Exception
    {
        final Node node1 = createNodeWithReference( "n1", NodePath.ROOT, "n1_1" );
        final Node node1_1 = createNodeWithReference( "n1_1", node1.path(), "n1_1_1" );
        createNodeWithReference( "n1_1_1", node1_1.path(), "n1" );

        nodeService.refresh( RefreshMode.ALL );

        final NodeIds dependants = FindNodesDependenciesCommand.create().
            nodeIds( NodeIds.from( node1.id() ) ).
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            build().
            execute();

        assertThat( dependants ).containsExactly( NodeId.from( "n1_1" ), NodeId.from( "n1_1_1" ) );
    }

    @Test
    public void exclude_dependencies()
        throws Exception
    {
        final Node node1 = createNodeWithReference( "n1", NodePath.ROOT, "n1_1", "n1_2" );
        final Node node1_1 = createNodeWithReference( "n1_1", node1.path(), "n1_1_1" );
        final Node node1_2 = createNodeWithReference( "n1_2", node1.path(), "n1_2_1" );
        createNodeWithReference( "n1_1_1", node1_1.path() );
        createNodeWithReference( "n1_2_1", node1_2.path() );

        nodeService.refresh( RefreshMode.ALL );

        final NodeIds dependants = FindNodesDependenciesCommand.create().
            nodeIds( NodeIds.from( node1.id() ) ).
            excludedIds( NodeIds.from( node1_1.id() ) ).
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            build().
            execute();

        assertEquals( 2, dependants.getSize() );
        assertTrue( dependants.contains( node1_2.id() ) , "Should contain node1_2");
        assertTrue( dependants.contains( NodeId.from( "n1_2_1" ) ) , "Should contain node1_2_1");
    }


    private Node createNodeWithReference( final String nodeId, final NodePath parent, final String... referencesTo )
    {
        final PropertyTree nodeData = new PropertyTree();

        if ( !( referencesTo == null || referencesTo.length == 0 ) )
        {
            for ( final String referenceTo : referencesTo )
            {
                nodeData.addReference( "myRef_" + referenceTo, Reference.from( referenceTo ) );
            }
        }

        return createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( nodeId ) ).
            parent( parent ).
            data( nodeData ).
            name( nodeId ).
            build() );
    }


}
