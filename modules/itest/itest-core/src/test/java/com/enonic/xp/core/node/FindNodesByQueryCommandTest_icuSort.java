package com.enonic.xp.core.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.DslOrderExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.parser.QueryParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        final Iterator<Node> it = getNodes( sortByStringWithLanguage( "ASC", language ).getNodeIds() ).iterator();
        for ( final String expectedWord : ascendingWords )
        {
            assertEquals( "node-" + expectedWord, it.next().name().toString() );
        }
    }

    @ParameterizedTest
    @MethodSource("languageSortTestCases")
    void sort_descending_with_language_specific_collation( final String language, final List<String> ascendingWords )
    {
        ascendingWords.forEach( word -> createStringNodeWithLanguage( "node-" + word, word, language ) );
        nodeService.refresh( RefreshMode.ALL );

        final List<String> descendingWords = new ArrayList<>( ascendingWords );
        Collections.reverse( descendingWords );

        final Iterator<Node> it = getNodes( sortByStringWithLanguage( "DESC", language ).getNodeIds() ).iterator();
        for ( final String expectedWord : descendingWords )
        {
            assertEquals( "node-" + expectedWord, it.next().name().toString() );
        }
    }

    private FindNodesByQueryResult sortByStringWithLanguage( final String direction, final String language )
    {
        final OrderExpr.Direction dir = OrderExpr.Direction.valueOf( direction );
        final FieldOrderExpr orderExpr = FieldOrderExpr.create( FIELD_STRING, dir, language );
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

        final Iterator<Node> it = getNodes( dslSortByStringWithLanguage( "ASC", language ).getNodeIds() ).iterator();
        for ( final String expectedWord : ascendingWords )
        {
            assertEquals( "node-" + expectedWord, it.next().name().toString() );
        }
    }

    @ParameterizedTest
    @MethodSource("languageSortTestCases")
    void dsl_sort_descending_with_language_specific_collation( final String language, final List<String> ascendingWords )
    {
        ascendingWords.forEach( word -> createStringNodeWithLanguage( "node-" + word, word, language ) );
        nodeService.refresh( RefreshMode.ALL );

        final List<String> descendingWords = new ArrayList<>( ascendingWords );
        Collections.reverse( descendingWords );

        final Iterator<Node> it = getNodes( dslSortByStringWithLanguage( "DESC", language ).getNodeIds() ).iterator();
        for ( final String expectedWord : descendingWords )
        {
            assertEquals( "node-" + expectedWord, it.next().name().toString() );
        }
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

    /**
     * Creating a node with an unsupported language code must throw {@link IllegalArgumentException} at node creation time.
     */
    @Test
    void unsupported_language_throws_at_node_creation()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( FIELD_STRING, "alfa" );

        final IndexConfig fieldIndexConfig = IndexConfig.create().enabled( true ).addLanguage( "xyz" ).build();

        final PatternIndexConfigDocument indexConfigDocument =
            PatternIndexConfigDocument.create().defaultConfig( IndexConfig.BY_TYPE ).add( FIELD_STRING, fieldIndexConfig ).build();

        assertThrows( IllegalArgumentException.class, () -> createNode( CreateNodeParams.create()
                                                                            .parent( NodePath.ROOT )
                                                                            .name( "node-alfa" )
                                                                            .data( data )
                                                                            .indexConfigDocument( indexConfigDocument )
                                                                            .build() ) );
    }

    private void createStringNodeWithLanguage( final String name, final String fieldValue, final String language )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( FIELD_STRING, fieldValue );

        final IndexConfig fieldIndexConfig = IndexConfig.create().enabled( true ).addLanguage( language ).build();

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
