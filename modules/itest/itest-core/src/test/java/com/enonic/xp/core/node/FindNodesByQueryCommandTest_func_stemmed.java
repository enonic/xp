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
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.repo.impl.node.NodeConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FindNodesByQueryCommandTest_func_stemmed
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void ascii_folding_with_wildcard()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "title", "grønnsaker" );

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( NodeConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
                defaultConfig( IndexConfig.BY_TYPE ).
                addAllTextConfigLanguage( "no" ).
                build() ).
            build() );

        refresh();

        queryAndAssert( node, "stemmed('_allText', 'grønnsake*', 'AND', 'no')", 1 );
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
