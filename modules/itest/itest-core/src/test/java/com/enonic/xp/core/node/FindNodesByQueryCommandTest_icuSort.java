package com.enonic.xp.core.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.DslOrderExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.parser.QueryParser;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies ICU collation sorting.
 * <p>
 * Norwegian alphabet ends: ..., z, æ, ø, å
 * Swedish alphabet ends:   ..., z, å, ä, ö
 * Danish alphabet ends:    ..., z, æ, ø, å
 * <p>
 * Without ICU (byte order after toLowerCase):  å (U+00E5) < æ (U+00E6) < ø (U+00F8)
 * With ICU Norwegian/Danish collation:         æ < ø < å
 * With ICU Swedish collation:                  å < ä < ö
 */
class FindNodesByQueryCommandTest_icuSort
    extends AbstractNodeTest
{
    private static final String FIELD_STRING = "fieldString";

    private static final String FIELD_PRIORITY = "priority";

    static Stream<Arguments> languageSortTestCases()
    {
        return Stream.of(
            // Norwegian: æ < ø < å  (same language rules as default _orderby, but now via _orderby_no)
            Arguments.of( "no", List.of( "alfa", "zulu", "æsel", "øl", "år" ) ),
            // Swedish: å < ä < ö
            Arguments.of( "sv", List.of( "alfa", "zulu", "åre", "ärlig", "öl" ) ),
            // Danish: æ < ø < å  (same ordering as Norwegian)
            Arguments.of( "da", List.of( "alfa", "zulu", "æble", "øl", "åben" ) ),
            // German: ä sorts near a, ü sorts near u
            Arguments.of( "de", List.of( "aal", "ähnlich", "opa", "über", "zug" ) ) );
    }

    @BeforeEach
    void setUp()
    {
        createDefaultRootNode();
    }

    /**
     * Parameterized tests: per-language ICU collation via {@code _orderby_XX} fields.
     * Nodes are created with the target language in their {@code IndexConfig} so the
     * {@code _orderby_XX} field is indexed at write time.
     * The sort query uses a {@link FieldOrderExpr} with an explicit language so the
     * resolver targets the language-specific field at query time.
     */
    @ParameterizedTest
    @MethodSource("languageSortTestCases")
    void sort_ascending_with_language_specific_collation( final String language, final List<String> ascendingWords )
    {
        ascendingWords.forEach( word -> createStringNodeWithLanguage( "node-" + word, word, language ) );
        nodeService.refresh( RefreshMode.ALL );

        assertThat( getNodes( sortByStringWithLanguage( "ASC", language ).getNodeIds() ) ).extracting( node -> node.name().toString() )
            .containsExactlyElementsOf( ascendingWords.stream().map( w -> "node-" + w ).toList() );
    }

    @ParameterizedTest
    @MethodSource("languageSortTestCases")
    void sort_descending_with_language_specific_collation( final String language, final List<String> ascendingWords )
    {
        ascendingWords.forEach( word -> createStringNodeWithLanguage( "node-" + word, word, language ) );
        nodeService.refresh( RefreshMode.ALL );

        final List<String> descendingWords = new ArrayList<>( ascendingWords );
        Collections.reverse( descendingWords );

        assertThat( getNodes( sortByStringWithLanguage( "DESC", language ).getNodeIds() ) ).extracting( node -> node.name().toString() )
            .containsExactlyElementsOf( descendingWords.stream().map( w -> "node-" + w ).toList() );
    }

    /**
     * Nodes indexed without the sort language have no {@code _orderby_XX} field. They sort after the nodes that have it,
     * in binary order of the default {@code _orderby} field, instead of in arbitrary order.
     */
    @Test
    void sort_with_language_falls_back_to_binary_order_for_nodes_without_language()
    {
        createStringNodeWithLanguage( "node-ol", "øl", "no" );
        createStringNodeWithLanguage( "node-alfa", "alfa", "no" );
        createStringNode( "node-zeta", "zeta" );
        createStringNode( "node-beta", "beta" );
        createStringNode( "node-delta", "delta" );
        nodeService.refresh( RefreshMode.ALL );

        assertThat( getNodes( sortByStringWithLanguage( "ASC", "no" ).getNodeIds() ) ).extracting( node -> node.name().toString() )
            .containsExactly( "node-alfa", "node-ol", "node-beta", "node-delta", "node-zeta" );

        assertThat( getNodes( sortByStringWithLanguage( "DESC", "no" ).getNodeIds() ) ).extracting( node -> node.name().toString() )
            .containsExactly( "node-ol", "node-alfa", "node-zeta", "node-delta", "node-beta" );
    }

    /**
     * The binary fallback must not override a requested secondary sort when language-specific values tie.
     * Both nodes have "alfa" as their lowest value, but different first values.
     */
    @Test
    void sort_with_language_keeps_secondary_sort_for_tied_values()
    {
        createNodeWithLanguage( "node-a", List.of( "zeta", "alfa" ), 1L, "no" );
        createNodeWithLanguage( "node-b", List.of( "beta", "alfa" ), 2L, "no" );
        nodeService.refresh( RefreshMode.ALL );

        final QueryExpr queryExpr = QueryExpr.from( QueryParser.parseCostraintExpression( "_parentPath=\"/\"" ),
                                                    FieldOrderExpr.create( IndexPath.from( FIELD_STRING ), OrderExpr.Direction.ASC,
                                                                           Locale.forLanguageTag( "no" ) ),
                                                    new FieldOrderExpr( FieldExpr.from( FIELD_PRIORITY ), OrderExpr.Direction.ASC ) );

        assertThat( getNodes( doFindByQuery( NodeQuery.create().query( queryExpr ).build() ).getNodeIds() ) ).extracting(
            node -> node.name().toString() ).containsExactly( "node-a", "node-b" );
    }

    private FindNodesByQueryResult sortByStringWithLanguage( final String direction, final String language )
    {
        final OrderExpr.Direction dir = OrderExpr.Direction.valueOf( direction );
        final FieldOrderExpr orderExpr = FieldOrderExpr.create( IndexPath.from( FIELD_STRING ), dir, Locale.forLanguageTag( language ) );
        final ConstraintExpr constraintExpr = QueryParser.parseCostraintExpression( "_parentPath=\"/\"" );
        final QueryExpr queryExpr = QueryExpr.from( constraintExpr, orderExpr );
        return doFindByQuery( NodeQuery.create().query( queryExpr ).build() );
    }

    @ParameterizedTest
    @MethodSource("languageSortTestCases")
    void dsl_sort_ascending_with_language_specific_collation( final String language, final List<String> ascendingWords )
    {
        ascendingWords.forEach( word -> createStringNodeWithLanguage( "node-" + word, word, language ) );
        nodeService.refresh( RefreshMode.ALL );

        assertThat( getNodes( dslSortByStringWithLanguage( "ASC", language ).getNodeIds() ) ).extracting( node -> node.name().toString() )
            .containsExactlyElementsOf( ascendingWords.stream().map( w -> "node-" + w ).toList() );
    }

    @ParameterizedTest
    @MethodSource("languageSortTestCases")
    void dsl_sort_descending_with_language_specific_collation( final String language, final List<String> ascendingWords )
    {
        ascendingWords.forEach( word -> createStringNodeWithLanguage( "node-" + word, word, language ) );
        nodeService.refresh( RefreshMode.ALL );

        final List<String> descendingWords = new ArrayList<>( ascendingWords );
        Collections.reverse( descendingWords );

        assertThat( getNodes( dslSortByStringWithLanguage( "DESC", language ).getNodeIds() ) ).extracting( node -> node.name().toString() )
            .containsExactlyElementsOf( descendingWords.stream().map( w -> "node-" + w ).toList() );
    }

    private FindNodesByQueryResult dslSortByStringWithLanguage( final String direction, final String language )
    {
        final PropertyTree expr = new PropertyTree();
        expr.addString( "field", FIELD_STRING );
        expr.addString( "direction", direction );
        expr.addString( "language", language );
        final DslOrderExpr orderExpr = DslOrderExpr.from( expr );
        final ConstraintExpr constraintExpr = QueryParser.parseCostraintExpression( "_parentPath=\"/\"" );
        final QueryExpr queryExpr = QueryExpr.from( constraintExpr, orderExpr );
        return doFindByQuery( NodeQuery.create().query( queryExpr ).build() );
    }


    private void createNodeWithLanguage( final String name, final List<String> fieldValues, final long priority, final String language )
    {
        final PropertyTree data = new PropertyTree();
        fieldValues.forEach( value -> data.addString( FIELD_STRING, value ) );
        data.addLong( FIELD_PRIORITY, priority );

        final IndexConfig fieldIndexConfig = IndexConfig.create().enabled( true ).addLanguage( Locale.forLanguageTag( language ) ).build();

        createNode( CreateNodeParams.create()
                        .parent( NodePath.ROOT )
                        .name( name )
                        .data( data )
                        .indexConfigDocument( PatternIndexConfigDocument.create()
                                                  .defaultConfig( IndexConfig.BY_TYPE )
                                                  .add( FIELD_STRING, fieldIndexConfig )
                                                  .build() )
                        .build() );
    }

    private void createStringNode( final String name, final String fieldValue )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( FIELD_STRING, fieldValue );

        createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( name ).data( data ).build() );
    }

    private void createStringNodeWithLanguage( final String name, final String fieldValue, final String language )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( FIELD_STRING, fieldValue );

        final IndexConfig fieldIndexConfig = IndexConfig.create().enabled( true ).addLanguage( Locale.forLanguageTag( language ) ).build();

        final PatternIndexConfigDocument indexConfigDocument =
            PatternIndexConfigDocument.create().defaultConfig( IndexConfig.BY_TYPE ).add( FIELD_STRING, fieldIndexConfig ).build();

        createNode( CreateNodeParams.create()
                        .parent( NodePath.ROOT )
                        .name( name )
                        .data( data )
                        .indexConfigDocument( indexConfigDocument )
                        .build() );
    }
}
