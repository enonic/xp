package com.enonic.xp.repo.impl.node;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodePathsByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.parser.QueryParser;

import static org.junit.Assert.*;

@Deprecated
public class FindNodePathByQueryTest
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
    public void empty()
        throws Exception
    {
        setupData();

        queryAndExpect( "_path = '/any_wrong_path'", NodePaths.empty() );
    }

    @Test
    public void single()
        throws Exception
    {
        setupData();

        queryAndExpect( "_path = '/content'", NodePaths.from( "/content" ) );
    }

    @Test
    public void multiple()
        throws Exception
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
    }

    private void queryAndExpect( final String queryString, final NodePaths expected )
    {
        final NodeQuery query = NodeQuery.create().query( QueryParser.parse( queryString ) ).
            build();

        final FindNodePathsByQueryResult result = this.nodeService.findNodePathsByQuery( query );

        assertEquals( expected, result.getPaths() );
    }
}
