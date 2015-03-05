package com.enonic.wem.repo.internal.entity;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Strings;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.expr.DynamicConstraintExpr;
import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

public class FindNodesByQueryCommandTest_func_fulltext
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
    public void fulltext()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( "content_default" ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( NodeIndexPath.NAME.getPath() ),
                                   ValueExpr.string( "My node name is my-node-1" ), ValueExpr.string( "OR" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( node.id() ) );
    }

    @Test
    public void fulltext_norwegian_characters()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "myProperty", "æ" );
        final PropertySet userdata = data.addSet( "data" );
        userdata.addString( "displayName", "ø å" );

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( "content_default" ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );

        refresh();

        printContentRepoIndex();

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( NodeIndexPath.ALL_TEXT.getPath() ), ValueExpr.string( "æ" ),
                                   ValueExpr.string( "OR" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( node.id() ) );

        final NodeQuery query2 = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( "data.displayName" ), ValueExpr.string( "ø å" ),
                                   ValueExpr.string( "OR" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result2 = doFindByQuery( query2 );

        assertEquals( 1, result2.getNodes().getSize() );
        assertNotNull( result2.getNodes().getNodeById( node.id() ) );
    }

    @Test
    public void fulltext_norwegian_characters_check_asciifolding()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "myProperty", "æ" );
        final PropertySet userdata = data.addSet( "data" );
        userdata.addString( "displayName", "ø å" );

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( "content_default" ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );

        refresh();

        printContentRepoIndex();

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( NodeIndexPath.ALL_TEXT.getPath() ), ValueExpr.string( "ae" ),
                                   ValueExpr.string( "OR" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( node.id() ) );

        final NodeQuery query2 = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( "data.displayName" ), ValueExpr.string( "o a" ),
                                   ValueExpr.string( "OR" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result2 = doFindByQuery( query2 );

        assertEquals( 1, result2.getNodes().getSize() );
        assertNotNull( result2.getNodes().getNodeById( node.id() ) );
    }

    @Test
    public void fulltext_with_path()
        throws Exception
    {

        final PropertyTree data = new PropertyTree();

        final String path1 = "test.of.string.with.path";
        final String value1 = "fisk ost pølse løk";
        data.setString( path1, value1 );

        final String path2 = "mystring.with.path2";
        final String value2 = "vif rbk lsk sif";
        data.setString( path2, value2 );

        final Node node = createNode( CreateNodeParams.create().
            name( "fisk ost" ).
            parent( NodePath.ROOT ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( "content_default" ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( path1 ), ValueExpr.string( "leter etter fisk" ),
                                   ValueExpr.string( "OR" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( node.id() ) );
    }

    @Test
    public void fulltext_fuzzy()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "title", "Levenshtein" );

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( "content_default" ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );

        refresh();

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( "title" ), ValueExpr.string( "levvenstein~2" ),
                                   ValueExpr.string( "AND" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( node.id() ) );
    }

    @Test
    public void negate()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "title", "fisk kake" );

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( "content_default" ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );

        final PropertyTree data2 = new PropertyTree();
        data2.addString( "title", "fisk båt" );

        createNode( CreateNodeParams.create().
            name( "my-node-2" ).
            parent( NodePath.ROOT ).
            data( data2 ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( "content_default" ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( "title" ), ValueExpr.string( "fisk -båt" ),
                                   ValueExpr.string( "AND" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( node.id() ) );
    }


    @Test
    public void boost_field()
        throws Exception
    {
        createWithTitleAndDescription( "1", "fish", "fash" );
        createWithTitleAndDescription( "2", "fosh", "basics and some fish other words not relevant" );
        createWithTitleAndDescription( "3", "fash", "fish" );

        assertOrder( doQuery( "fulltext('title, description^5', 'fish', 'AND')" ), "3", "1", "2" );
        assertOrder( doQuery( "fulltext('title^5, description', 'fish', 'AND')" ), "1", "3", "2" );
    }

    @Test
    public void ngram_not_fulltext()
        throws Exception
    {
        createWithTitle( "1", "fishing techniques" );
        createWithTitle( "2", "fishing time interrupted by fiance" );
        createWithTitle( "3", "figure skating is better than fishing" );

        final String queryString = "ngram('title', 'fi', 'AND') AND NOT fulltext('title', 'fiancé figure', 'OR')";

        queryAndAssert( queryString, 1 );
    }

    @Test
    public void fulltext_wildcard_paths()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        final String path1 = "test.of.string-1.with.path-1";
        final String value1 = "fisk ost pølse løk";
        data.setString( path1, value1 );

        createNode( CreateNodeParams.create().
            name( "node1" ).
            parent( NodePath.ROOT ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( "content_default" ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );

        final PropertyTree data2 = new PropertyTree();
        final String path2 = "test.of.string-2.with.path-2";
        final String value2 = "fisk ost pølse løk";
        data2.setString( path2, value2 );

        createNode( CreateNodeParams.create().
            name( "node2" ).
            parent( NodePath.ROOT ).
            data( data2 ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( "content_default" ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );

        queryAndAssert( "fulltext('test*', 'leter etter fisk', 'OR')", 2 );
        queryAndAssert( "fulltext('test.*', 'leter etter fisk', 'OR')", 2 );
        queryAndAssert( "fulltext('test.of*', 'leter etter fisk', 'OR')", 2 );
        queryAndAssert( "fulltext('test.of.string*', 'leter etter fisk', 'OR')", 2 );
        queryAndAssert( "fulltext('test.of.string-1.*', 'leter etter fisk', 'OR')", 1 );
        queryAndAssert( "fulltext('test.of.string-2.*', 'leter etter fisk', 'OR')", 1 );
        queryAndAssert( "fulltext('*path*', 'leter etter fisk', 'OR')", 2 );
        queryAndAssert( "fulltext('*path', 'leter etter fisk', 'OR')", 0 );
        queryAndAssert( "fulltext('*path-1', 'leter etter fisk', 'OR')", 1 );
        queryAndAssert( "fulltext('*path-2', 'leter etter fisk', 'OR')", 1 );
    }

    private void createWithTitle( final String id, final String title )
    {
        createWithTitleAndDescription( id, title, null );
    }

    private void createWithTitleAndDescription( final String id, final String title, final String description )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "title", title );
        if ( !Strings.isNullOrEmpty( description ) )
        {
            data.addString( "description", description );
        }

        createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( id ) ).
            name( title ).
            parent( NodePath.ROOT ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( "content_default" ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );
    }

}
