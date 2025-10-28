package com.enonic.xp.lib.node;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.aggregation.Aggregation;
import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.Buckets;
import com.enonic.xp.highlight.HighlightedProperties;
import com.enonic.xp.highlight.HighlightedProperty;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.suggester.Suggestions;
import com.enonic.xp.suggester.TermSuggestion;
import com.enonic.xp.suggester.TermSuggestionEntry;
import com.enonic.xp.suggester.TermSuggestionOption;

class FindNodesByQueryHandlerTest
    extends BaseNodeHandlerTest
{
    @Test
    void testExample()
    {
        final BucketAggregation duration = Aggregation.bucketAggregation( "duration" ).
            buckets( Buckets.create().
                add( Bucket.create().
                    key( "1600" ).
                    docCount( 2 ).
                    build() ).
                add( Bucket.create().
                    key( "1400" ).
                    docCount( 1 ).
                    build() ).
                add( Bucket.create().
                    key( "1300" ).
                    docCount( 5 ).
                    build() ).
                build() ).
            build();

        final Aggregations agg = Aggregations.create().
            add( Aggregation.bucketAggregation( "urls" ).
                buckets( Buckets.create().
                    add( Bucket.create().
                        key( "/site/draft/superhero/search" ).
                        docCount( 6762L ).
                        addAggregations( Aggregations.from( duration ) ).
                        build() ).
                    add( Bucket.create().
                        key( "/site/draft/superhero" ).
                        docCount( 1245 ).
                        addAggregations( Aggregations.from( duration ) ).
                        build() ).
                    build() ).
                build() ).
            build();

        final Suggestions suggestions = Suggestions.create().
            add( TermSuggestion.create( "termSuggestion" ).
                addSuggestionEntry( TermSuggestionEntry.create().
                    text( "text1" ).
                    length( 2 ).
                    offset( 1 ).
                    addSuggestionOption( TermSuggestionOption.create().
                        text( "text1-1" ).
                        score( 1.0f ).
                        freq( 2 ).
                        build() ).
                    addSuggestionOption( TermSuggestionOption.create().
                        text( "text1-2" ).
                        score( 4.0f ).
                        freq( 5 ).
                        build() ).
                    build() ).
                addSuggestionEntry( TermSuggestionEntry.create().
                    text( "text2" ).
                    length( 2 ).
                    offset( 2 ).
                    addSuggestionOption( TermSuggestionOption.create().
                        text( "text2-1" ).
                        score( 2.3f ).
                        freq( 4 ).
                        build() ).
                    addSuggestionOption( TermSuggestionOption.create().
                        text( "text2-2" ).
                        score( 1.0f ).
                        freq( 2 ).
                        build() ).
                    build() ).
                build() ).
            build();

        Mockito.doReturn( FindNodesByQueryResult.create().
            totalHits( 12902 ).
            addNodeHit( NodeHit.create().
                nodeId( NodeId.from( "b186d24f-ac38-42ca-a6db-1c1bda6c6c26" ) ).
                score( 1.23f ).
                highlight( HighlightedProperties.create().
                    add( HighlightedProperty.create().
                        name( "property1" ).
                        addFragment( "fragment1" ).
                        addFragment( "fragment2" ).
                        build() ).
                    build() ).
                build() ).
            addNodeHit( NodeHit.create().
                nodeId( NodeId.from( "350ba4a6-589c-498b-8af0-f183850e1120" ) ).
                score( 1.40f ).
                build() ).
            aggregations( agg ).
            suggestions( suggestions ).
            build() ).
            when( this.nodeService ).
            findByQuery( Mockito.isA( NodeQuery.class ) );

        runScript( "/lib/xp/examples/node/query.js" );
    }

    @Test
    void testExample2()
    {
        Mockito.when( this.nodeService.findByQuery( Mockito.isA( NodeQuery.class ) ) )
            .thenReturn( FindNodesByQueryResult.create()
                             .totalHits( 12902 )
                             .addNodeHit(
                                 NodeHit.create().nodeId( NodeId.from( "b186d24f-ac38-42ca-a6db-1c1bda6c6c26" ) ).score( 1.23f ).build() )
                             .addNodeHit(
                                 NodeHit.create().nodeId( NodeId.from( "350ba4a6-589c-498b-8af0-f183850e1120" ) ).score( 1.40f ).build() )
                             .build() );

        runScript( "/lib/xp/examples/node/query-filter-array-on-root.js" );
    }

    @Test
    void testDslExample()
    {
        Mockito.doReturn( FindNodesByQueryResult.create()
                              .totalHits( 12902 )
                              .addNodeHit(
                                  NodeHit.create().nodeId( NodeId.from( "b186d24f-ac38-42ca-a6db-1c1bda6c6c26" ) ).score( 1.23f ).build() )
                              .addNodeHit(
                                  NodeHit.create().nodeId( NodeId.from( "350ba4a6-589c-498b-8af0-f183850e1120" ) ).score( 1.7f ).build() )
                              .build() ).when( this.nodeService ).findByQuery( Mockito.isA( NodeQuery.class ) );

        runScript( "/lib/xp/examples/node/query-dsl.js" );
    }

    @Test
    void testSortDslExample()
    {
        Mockito.doReturn( FindNodesByQueryResult.create()
                              .totalHits( 12902 )
                              .addNodeHit(
                                  NodeHit.create().nodeId( NodeId.from( "b186d24f-ac38-42ca-a6db-1c1bda6c6c26" ) ).score( 1.23f ).build() )
                              .addNodeHit(
                                  NodeHit.create().nodeId( NodeId.from( "350ba4a6-589c-498b-8af0-f183850e1120" ) ).score( 1.7f ).build() )
                              .build() ).when( this.nodeService ).findByQuery( Mockito.isA( NodeQuery.class ) );

        runScript( "/lib/xp/examples/node/query-dsl-sort.js" );
    }

    @Test
    void dslSortWithArray()
    {
        Mockito.doReturn( FindNodesByQueryResult.create()
                              .totalHits( 12902 )
                              .addNodeHit(
                                  NodeHit.create().nodeId( NodeId.from( "b186d24f-ac38-42ca-a6db-1c1bda6c6c26" ) ).score( 1.23f ).build() )
                              .addNodeHit(
                                  NodeHit.create().nodeId( NodeId.from( "350ba4a6-589c-498b-8af0-f183850e1120" ) ).score( 1.7f ).build() )
                              .build() ).when( this.nodeService ).findByQuery( Mockito.isA( NodeQuery.class ) );

        runFunction( "/test/FindNodesByQueryHandlerTest.js", "sort" );
    }

    @Test
    void dslFilterInQuery()
    {
        Mockito.doReturn( FindNodesByQueryResult.create()
                              .totalHits( 12902 )
                              .addNodeHit(
                                  NodeHit.create().nodeId( NodeId.from( "b186d24f-ac38-42ca-a6db-1c1bda6c6c26" ) ).score( 1.23f ).build() )
                              .addNodeHit(
                                  NodeHit.create().nodeId( NodeId.from( "350ba4a6-589c-498b-8af0-f183850e1120" ) ).score( 1.7f ).build() )
                              .build() ).when( this.nodeService ).findByQuery( Mockito.isA( NodeQuery.class ) );

        runFunction( "/test/FindNodesByQueryHandlerTest.js", "filterInQuery" );
    }

    @Test
    void dslQueryDslExistsExpr()
    {
        Mockito.doReturn( FindNodesByQueryResult.create()
                              .totalHits( 12902 )
                              .addNodeHit(
                                  NodeHit.create().nodeId( NodeId.from( "b186d24f-ac38-42ca-a6db-1c1bda6c6c26" ) ).score( 1.23f ).build() )
                              .addNodeHit(
                                  NodeHit.create().nodeId( NodeId.from( "350ba4a6-589c-498b-8af0-f183850e1120" ) ).score( 1.7f ).build() )
                              .build() ).when( this.nodeService ).findByQuery( Mockito.isA( NodeQuery.class ) );

        runFunction( "/test/FindNodesByQueryHandlerTest.js", "queryDslExistsExpr" );
    }

    @Test
    void dslSortInvalid()
    {
        runFunction( "/test/FindNodesByQueryHandlerTest.js", "sortInvalid" );
    }

    @Test
    void dslQueryInvalid()
    {
        runFunction( "/test/FindNodesByQueryHandlerTest.js", "invalid" );
    }

    @Test
    void dslQueryEmpty()
    {
        Mockito.doReturn( FindNodesByQueryResult.create().build() )
            .when( this.nodeService )
            .findByQuery( Mockito.isA( NodeQuery.class ) );
        runFunction( "/test/FindNodesByQueryHandlerTest.js", "queryEmpty" );
    }

    @Test
    void dslQueryNull()
    {
        Mockito.doReturn( FindNodesByQueryResult.create().build() )
            .when( this.nodeService )
            .findByQuery( Mockito.isA( NodeQuery.class ) );
        runFunction( "/test/FindNodesByQueryHandlerTest.js", "queryNull" );
    }

}
