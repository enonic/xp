package com.enonic.xp.core.node;

import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.parser.QueryParser;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FindNodePathsByQueryTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }


    @Test
    void empty()
    {
        setupData();

        queryAndExpect( "_path = '/any_wrong_path'", NodePaths.empty() );
    }

    @Test
    void single()
    {
        setupData();

        queryAndExpect( "_path = '/content'", NodePaths.from( "/content" ) );
    }

    @Test
    void multiple()
    {
        setupData();

        queryAndExpect( "_path in ('/content','/content/superhero/posts/test-post' )",
                        NodePaths.from( "/content", "/content/superhero/posts/test-post" ) );

        queryAndExpect( "", NodePaths.from( "/", "/content", "/content/superhero", "/content/superhero/posts",
                                            "/content/superhero/posts/test-post" ) );
    }

    private void setupData()
    {

        final Node content = createNode( CreateNodeParams.create().
            name( "content" ).
            parent( NodePath.ROOT ).
            build() );

        final Node superhero = createNode( CreateNodeParams.create().
            name( "superhero" ).
            parent( content.path() ).
            build() );

        final Node posts = createNode( CreateNodeParams.create().
            name( "posts" ).
            parent( superhero.path() ).
            build() );

        createNode( CreateNodeParams.create().
            name( "test-post" ).
            parent( posts.path() ).
            build() );
        nodeService.refresh( RefreshMode.ALL );
    }

    private void queryAndExpect( final String queryString, final NodePaths expected )
    {
        final NodeQuery query = NodeQuery.create().query( QueryParser.parse( queryString ) ).
            withPath( true ).
            build();

        final FindNodesByQueryResult result = this.nodeService.findByQuery( query );

        assertEquals( expected, NodePaths.from( result.getNodeHits().
            stream().
            map( NodeHit::getNodePath ).
            collect( Collectors.toSet() ) ) );
    }
}
