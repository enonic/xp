package com.enonic.xp.core.node;

import java.util.Comparator;
import java.util.stream.Stream;

import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.GetNodeVersionsResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.node.GetNodeVersionsCommand;

import static org.assertj.core.api.Assertions.assertThat;

class GetNodeVersionsCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        createDefaultRootNode();
    }

    @Test
    void get_single_version()
    {
        final Node node = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );
        refresh();

        final GetNodeVersionsResult result = executeGetVersions( node.id(), 10 );

        assertThat( result.getTotalHits() ).isEqualTo( 1 );
        assertThat( result.getNodeVersions() ).hasSize( 1 );
        assertThat( result.getCursor() ).isNull();
    }

    @Test
    void get_multiple_versions()
    {
        final Node node = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );
        updateNode( node.id(), 4 );
        refresh();

        final GetNodeVersionsResult result = executeGetVersions( node.id(), 10 );

        assertThat( result.getTotalHits() ).isEqualTo( 5 );
        assertThat( result.getNodeVersions() ).hasSize( 5 );
        assertThat( result.getCursor() ).isNull();
    }

    @Test
    void versions_ordered_by_timestamp_desc()
    {
        final Node node = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );
        updateNode( node.id(), 3 );
        refresh();

        final GetNodeVersionsResult result = executeGetVersions( node.id(), 10 );

        assertThat( result.getNodeVersions() ).extracting( NodeVersion::getTimestamp )
            .isSortedAccordingTo( Comparator.reverseOrder() );
    }

    @Test
    void cursor_returned_when_more_results()
    {
        final Node node = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );
        updateNode( node.id(), 4 );
        refresh();

        final GetNodeVersionsResult result = executeGetVersions( node.id(), 2 );

        assertThat( result.getTotalHits() ).isEqualTo( 5 );
        assertThat( result.getNodeVersions() ).hasSize( 2 );
        assertThat( result.getCursor() ).isNotNull();
    }

    @Test
    void no_cursor_when_all_results_fit()
    {
        final Node node = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );
        updateNode( node.id(), 2 );
        refresh();

        final GetNodeVersionsResult result = executeGetVersions( node.id(), 10 );

        assertThat( result.getTotalHits() ).isEqualTo( 3 );
        assertThat( result.getNodeVersions() ).hasSize( 3 );
        assertThat( result.getCursor() ).isNull();
    }

    @Test
    void no_cursor_when_size_equals_total()
    {
        final Node node = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );
        updateNode( node.id(), 2 );
        refresh();

        final GetNodeVersionsResult result = executeGetVersions( node.id(), 3 );

        assertThat( result.getTotalHits() ).isEqualTo( 3 );
        assertThat( result.getNodeVersions() ).hasSize( 3 );
        assertThat( result.getCursor() ).isNull();
    }

    @Test
    void cursor_pagination()
    {
        final Node node = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );
        updateNode( node.id(), 4 );
        refresh();

        // Page 1
        final GetNodeVersionsResult page1 = executeGetVersions( node.id(), 2 );
        assertThat( page1.getNodeVersions() ).hasSize( 2 );
        assertThat( page1.getCursor() ).isNotNull();

        // Page 2
        final GetNodeVersionsResult page2 = executeGetVersions( node.id(), 2, page1.getCursor() );
        assertThat( page2.getNodeVersions() ).hasSize( 2 );
        assertThat( page2.getCursor() ).isNotNull();

        // Page 3 - last page with 1 remaining
        final GetNodeVersionsResult page3 = executeGetVersions( node.id(), 2, page2.getCursor() );
        assertThat( page3.getNodeVersions() ).hasSize( 1 );
        assertThat( page3.getCursor() ).isNull();

        final ListAssert<NodeVersion> forAssert =
            assertThat( Stream.of( page1, page2, page3 ).flatMap( r -> r.getNodeVersions().stream() ) );

        // All version IDs should be distinct
        forAssert.extracting(
            NodeVersion::getNodeVersionId ).hasSize( 5 ).doesNotHaveDuplicates();

        // All timestamps should be sorted descending
        forAssert.extracting(
            NodeVersion::getTimestamp ).hasSize( 5 ).isSortedAccordingTo( Comparator.reverseOrder() );
    }

    private GetNodeVersionsResult executeGetVersions( final NodeId nodeId, final int size )
    {
        return executeGetVersions( nodeId, size, null );
    }

    private GetNodeVersionsResult executeGetVersions( final NodeId nodeId, final int size, final String cursor )
    {
        return GetNodeVersionsCommand.create()
            .params( GetNodeVersionsParams.create().nodeId( nodeId ).size( size ).cursor( cursor ).build() )
            .searchService( this.searchService )
            .build()
            .execute();
    }

    private void updateNode( final NodeId nodeId, final int updates )
    {
        for ( int i = 0; i < updates; i++ )
        {
            final long value = i;
            updateNode( UpdateNodeParams.create().id( nodeId ).editor( node -> node.data.setLong( "someValue", value ) ).build() );
        }
    }
}
