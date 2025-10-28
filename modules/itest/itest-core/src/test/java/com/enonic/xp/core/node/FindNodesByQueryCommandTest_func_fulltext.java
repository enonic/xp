package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
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
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.expr.DynamicConstraintExpr;
import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.node.NodeConstants;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FindNodesByQueryCommandTest_func_fulltext
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void fulltext()
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( NodeConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );
        nodeService.refresh( RefreshMode.ALL );

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( NodeIndexPath.NAME.getPath() ),
                                   ValueExpr.string( "My node name is my-node-1" ), ValueExpr.string( "OR" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodeIds().getSize() );
        assertTrue( result.getNodeIds().contains( node.id() ) );
    }

    @Test
    void fulltext_norwegian_characters()
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
                analyzer( NodeConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );

        nodeService.refresh( RefreshMode.ALL );

        printContentRepoIndex();

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( NodeIndexPath.ALL_TEXT.getPath() ), ValueExpr.string( "æ" ),
                                   ValueExpr.string( "OR" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodeIds().getSize() );
        assertTrue( result.getNodeIds().contains( node.id() ) );

        final NodeQuery query2 = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( "data.displayName" ), ValueExpr.string( "ø å" ),
                                   ValueExpr.string( "OR" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result2 = doFindByQuery( query2 );

        assertEquals( 1, result2.getNodeIds().getSize() );
        assertTrue( result.getNodeIds().contains( node.id() ) );
    }

    @Test
    void fulltext_norwegian_characters_check_asciifolding()
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
                analyzer( NodeConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );

        nodeService.refresh( RefreshMode.ALL );

        printContentRepoIndex();

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( NodeIndexPath.ALL_TEXT.getPath() ), ValueExpr.string( "ae" ),
                                   ValueExpr.string( "OR" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodeIds().getSize() );
        assertTrue( result.getNodeIds().contains( node.id() ) );

        final NodeQuery query2 = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( "data.displayName" ), ValueExpr.string( "o a" ),
                                   ValueExpr.string( "OR" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result2 = doFindByQuery( query2 );

        assertEquals( 1, result2.getNodeIds().getSize() );
        assertTrue( result.getNodeIds().contains( node.id() ) );
    }

    @Test
    void ascii_folding_with_wildcard()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "myProperty", "grønnsaker" );

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

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( NodeIndexPath.ALL_TEXT.getPath() ), ValueExpr.string( "grønns*" ),
                                   ValueExpr.string( "AND" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodeIds().getSize() );
        assertTrue( result.getNodeIds().contains( node.id() ) );
    }

    @Test
    void fulltext_with_path()
    {

        final PropertyTree data = new PropertyTree();

        final String path1 = "Test.Of.String.With.Path";
        final String value1 = "fisk ost pølse løk";
        data.setString( path1, value1 );

        final String path2 = "MyString.With.Path2";
        final String value2 = "vif rbk lsk sif";
        data.setString( path2, value2 );

        final Node node = createNode( CreateNodeParams.create().
            name( "fisk ost" ).
            parent( NodePath.ROOT ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( NodeConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );
        nodeService.refresh( RefreshMode.ALL );

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( path1 ), ValueExpr.string( "leter etter fisk" ),
                                   ValueExpr.string( "OR" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodeIds().getSize() );
        assertTrue( result.getNodeIds().contains( node.id() ) );
    }

    @Test
    void fulltext_fuzzy()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "title", "Levenshtein" );

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

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( "title" ), ValueExpr.string( "levvenstein~2" ),
                                   ValueExpr.string( "AND" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodeIds().getSize() );
        assertTrue( result.getNodeIds().contains( node.id() ) );
    }


    @Test
    void fulltext_fuzzy_2()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "title", "Levenshtein" );

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
        data2.addString( "title", "fisk" );

        final Node node2 = createNode( CreateNodeParams.create().
            name( "my-node-2" ).
            parent( NodePath.ROOT ).
            data( data2 ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( NodeConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );

        nodeService.refresh( RefreshMode.ALL );

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( "title" ), ValueExpr.string( "levvenstein~2 fsik~2" ),
                                   ValueExpr.string( "OR" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 2, result.getNodeIds().getSize() );
        assertTrue( result.getNodeIds().contains( node.id() ) );
        assertTrue( result.getNodeIds().contains( node2.id() ) );
    }


    @Test
    void negate()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "title", "fisk kake" );

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
        data2.addString( "title", "fisk båt" );

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

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( "title" ), ValueExpr.string( "fisk -båt" ),
                                   ValueExpr.string( "AND" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodeIds().getSize() );
        assertTrue( result.getNodeIds().contains( node.id() ) );
    }


    @Test
    void boost_field()
    {
        createWithTitleAndDescription( "1", "fish", "fash" );
        createWithTitleAndDescription( "2", "fosh", "basics and some fish other words not relevant" );
        createWithTitleAndDescription( "3", "fash", "fish" );
        nodeService.refresh( RefreshMode.ALL );

        assertOrder( doQuery( "fulltext('title, description^5', 'fish', 'AND')" ), NodeId.from( "3" ), NodeId.from( "1" ),
                     NodeId.from( "2" ) );
        assertOrder( doQuery( "fulltext('title^5, description', 'fish', 'AND')" ), NodeId.from( "1" ), NodeId.from( "3" ),
                     NodeId.from( "2" ) );
    }

    @Test
    void ngram_not_fulltext()
    {
        createWithTitle( "1", "fishing techniques" );
        createWithTitle( "2", "fishing time interrupted by fiance" );
        createWithTitle( "3", "figure skating is better than fishing" );
        nodeService.refresh( RefreshMode.ALL );

        final String queryString = "ngram('title', 'fi', 'AND') AND NOT fulltext('title', 'fiancé figure', 'OR')";

        queryAndAssert( queryString, 1 );
    }

    @Test
    void fulltext_wildcard_paths()
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
                analyzer( NodeConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
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
                analyzer( NodeConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );
        nodeService.refresh( RefreshMode.ALL );

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
        if ( !isNullOrEmpty( description ) )
        {
            data.addString( "description", description );
        }

        createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( id ) ).
            name( title ).
            parent( NodePath.ROOT ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( NodeConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );
    }

    @Test
    void fulltext_word_breaking_character()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "title", "Levenshteins-algorithm" );

        createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( NodeConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );
        nodeService.refresh( RefreshMode.ALL );

        queryAndAssert( "fulltext('title', 'levenshteins algorithm', 'AND')", 1 );
        queryAndAssert( "fulltext('title', 'levenshteins-algorithm', 'AND')", 1 );
    }


    @Test
    void word_delimiter_testing_underscore()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "title", "testing_delimiter" );

        createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( NodeConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );
        nodeService.refresh( RefreshMode.ALL );

        queryAndAssert( "fulltext('title', 'testing', 'AND')", 1 );
        queryAndAssert( "fulltext('title', 'delimiter', 'AND')", 1 );
    }

    @Test
    void word_delimiter_testing_dot()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "title", "testing.delimiter" );

        createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( NodeConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );
        nodeService.refresh( RefreshMode.ALL );

        queryAndAssert( "fulltext('title', 'testing', 'AND')", 1 );
        queryAndAssert( "fulltext('title', 'delimiter', 'AND')", 1 );
    }


}
