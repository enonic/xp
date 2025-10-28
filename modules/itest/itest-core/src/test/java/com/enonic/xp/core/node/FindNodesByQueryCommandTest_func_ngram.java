package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.repo.impl.node.NodeConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FindNodesByQueryCommandTest_func_ngram
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void ngram_and()
    {
        final Node node = createNodes();

        queryAndAssert( node, "ngram('title', 'lev alg', 'AND')", 1 );
    }

    @Test
    void ngram_or()
    {
        final Node node = createNodes();

        queryAndAssert( node, "ngram('title', 'lev liv', 'OR')", 2 );
    }

    @Test
    void ngram_one_char()
    {
        final Node node = createNodes();

        queryAndAssert( node, "ngram('title', 'l', 'AND')", 2 );
    }

    @Test
    void ngram_word_breaking_character()
    {
        final Node node = createNodes();

        queryAndAssert( node, "ngram('title', 'levenshteins-algo', 'AND')", 1 );
    }

    @Test
    void fuzzy()
    {
        final Node node = createNodes();

        queryAndAssert( node, "ngram('title', 'levneshtein~2 lvier~2', 'OR')", 2 );
    }

    @Test
    void word_delimiter_underscore()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "title", "testing_delimiter" );

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( NodeConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );
        nodeService.refresh( RefreshMode.ALL );

        queryAndAssert( node, "ngram('title', 'test', 'AND')", 1 );
        queryAndAssert( node, "ngram('title', 'delim', 'AND')", 1 );
    }

    @Test
    void word_delimiter_dot()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "title", "testing.delimiter" );

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( NodeConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );
        nodeService.refresh( RefreshMode.ALL );

        queryAndAssert( node, "ngram('title', 'test', 'AND')", 1 );
        queryAndAssert( node, "ngram('title', 'delim', 'AND')", 1 );
    }

    @Test
    void ascii_folding_with_wildcard()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "title", "grønnsaker" );

        final Node node = createNode( CreateNodeParams.create()
                                          .name( "my-node-1" )
                                          .parent( NodePath.ROOT )
                                          .data( data )
                                          .indexConfigDocument( PatternIndexConfigDocument.create()
                                                                    .analyzer( NodeConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER )
                                                                    .defaultConfig( IndexConfig.BY_TYPE )
                                                                    .build() )
                                          .build() );
        nodeService.refresh( RefreshMode.ALL );

        queryAndAssert( node, "ngram('title', 'grønnsak*', 'AND')", 1 );
    }

    private Node createNodes()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "title", "Levenshteins-algorithm" );

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( NodeConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );

        final PropertyTree data2 = new PropertyTree();
        data2.addString( "title", "Liver and almonds" );

        createNode( CreateNodeParams.create().
            name( "my-node-2" ).
            parent( NodePath.ROOT ).
            data( data2 ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( NodeConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );
        nodeService.refresh( RefreshMode.ALL );

        return node;
    }

    private void queryAndAssert( final Node node, final String queryString, final int expected )
    {
        final NodeQuery query = NodeQuery.create().
            query( QueryParser.parse( queryString ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( expected, result.getNodeIds().getSize() );
        assertTrue( result.getNodeIds().contains( node.id() ) );
    }
}
