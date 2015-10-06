package com.enonic.xp.repo.impl.node;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.parser.QueryParser;

import static org.junit.Assert.*;

public class FindNodesByQueryCommandTest_func_ngram
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
    public void ngram_and()
        throws Exception
    {
        final Node node = createNodes();

        queryAndAssert( node, "ngram('title', 'lev alg', 'AND')", 1 );
    }

    @Test
    public void ngram_or()
        throws Exception
    {
        final Node node = createNodes();

        queryAndAssert( node, "ngram('title', 'lev liv', 'OR')", 2 );
    }

    @Test
    public void ngram_one_char()
        throws Exception
    {
        final Node node = createNodes();

        queryAndAssert( node, "ngram('title', 'l', 'AND')", 2 );
    }

    @Test
    public void ngram_word_breaking_character()
        throws Exception
    {
        final Node node = createNodes();

        queryAndAssert( node, "ngram('title', 'levenshteins-algo', 'AND')", 1 );
    }

    @Test
    public void word_delimiter_underscore()
        throws Exception
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

        queryAndAssert( node, "ngram('title', 'test', 'AND')", 1 );
        queryAndAssert( node, "ngram('title', 'delim', 'AND')", 1 );
    }

    @Test
    public void word_delimiter_dot()
        throws Exception
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

        queryAndAssert( node, "ngram('title', 'test', 'AND')", 1 );
        queryAndAssert( node, "ngram('title', 'delim', 'AND')", 1 );
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
        return node;
    }

    private void queryAndAssert( final Node node, final String queryString, final int expected )
    {
        final NodeQuery query = NodeQuery.create().
            query( QueryParser.parse( queryString ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( expected, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( node.id() ) );
    }
}
