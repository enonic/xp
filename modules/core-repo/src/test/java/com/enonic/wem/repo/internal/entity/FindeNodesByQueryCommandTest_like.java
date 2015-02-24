package com.enonic.wem.repo.internal.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.parser.QueryParser;

import static org.junit.Assert.*;

public class FindeNodesByQueryCommandTest_like
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
    public void like_queries()
        throws Exception
    {
        setupData();

        queryAndExpect( "_parentPath=\"/content/superhero/posts\" AND data.tags LIKE \"tag\"", 1 );
        queryAndExpect( "_parentPath=\"/content/superhero/posts\" AND data.tags LIKE \"sample\"", 1 );
    }

    @Test
    public void like_quals_full_value()
        throws Exception
    {
        setupData();

        queryAndExpect( " _parentPath=\"/content/superhero/posts\" AND data.author LIKE \"ec87335c-3ee2-45ef-8ece-90401e04af29\"", 1 );
    }

    @Test
    public void with_wildcards()
        throws Exception
    {
        setupData();

        queryAndExpect( "_parentPath=\"/content/superhero/posts\" AND data.post LIKE \"*text*\"", 1 );
        queryAndExpect( " _parentPath=\"/content/superhero/posts\" AND data.title LIKE \"*post*\"", 1 );
    }

    private void setupData()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "displayName", "Test post" );
        final PropertySet contentData = data.addSet( "data" );
        contentData.addString( "title", "Test post" );
        contentData.addHtmlPart( "post", "<p>This is the text of the test post</p>" );
        contentData.addString( "category", "af4fb132-1565-4c2a-850d-a415566bba34" );
        contentData.addString( "author", "ec87335c-3ee2-45ef-8ece-90401e04af29" );
        contentData.addString( "tags", "test" );
        contentData.addString( "tags", "tag" );
        contentData.addString( "tags", "sample" );

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
            data( data ).
            parent( posts.path() ).
            build() );
    }

    private void queryAndExpect( final String queryString, final int expected )
    {
        final NodeQuery query = NodeQuery.create().query( QueryParser.parse( queryString ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( expected, result.getHits() );
    }
}
