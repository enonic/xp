package com.enonic.xp.core.nodb.corpus;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.AllTextIndexConfig;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.util.GeoPoint;

/**
 * The deterministic seed dataset behind the golden-query corpus.
 * <p>
 * Everything here is fixed by construction, because the baseline artifact records values, not
 * just shapes: node ids are explicit (never generated), there is no "now" anywhere (all instants
 * are literal), and no set is chosen so that two aggregation buckets or two sort keys tie. The
 * Gate 0(c) inventory notes order-by values are pre-encoded lexi-sortable ASCII, so the exact
 * field values -- not merely their relative order -- end up in the artifact.
 * <p>
 * Groups:
 * <ul>
 * <li><b>/cities</b> (8 nodes, ids {@code city-*}) -- the workhorse set: mixed-case titles with
 * Nordic/German characters, three categories with counts 4/3/1 (deliberately tie-free so terms
 * aggregations are order-stable), eight distinct populations, eight distinct instants in six
 * distinct months, a geo point each, a boolean, and a {@code numish} field held as a string on
 * one node and as a long on another so the same logical field can be queried as string and as
 * number. Fulltext/ngram come from {@code BY_TYPE}; {@code _allText} and {@code _stemmed_no}
 * come from an explicit index-config document.</li>
 * <li><b>/collation</b> (10 nodes, ids {@code coll-*}) -- words whose ICU collation order differs
 * from code-point order in Norwegian (æ ø å), Swedish (å ä ö) and German (ä ö ü ß). The
 * {@code word} field declares nb/sv/da/de, so {@code _orderby}, {@code _orderby_no},
 * {@code _orderby_sv}, {@code _orderby_da} and {@code _orderby_ducet} are all indexed.</li>
 * <li><b>/tree</b> (7 nodes) -- a deep path tree for {@code pathMatch}, {@code _path} LIKE and
 * parent queries.</li>
 * <li><b>/acl</b> (3 nodes) -- one node readable by the test user, one only by the corpus secret
 * user, one by a principal nobody asserts, so the ACL rows actually have something to filter.</li>
 * <li><b>/page</b> (25 nodes, ids {@code page-01}..{@code page-25}) -- enough documents for
 * from/size, count-only, GET_ALL/scroll and histogram buckets, each carrying an explicit
 * {@code manualOrderValue} so the manual-order sort is deterministic.</li>
 * </ul>
 */
final class CorpusFixture
{
    static final Locale NO = Locale.forLanguageTag( "no" );

    static final Locale NB = Locale.forLanguageTag( "nb" );

    static final Locale SV = Locale.forLanguageTag( "sv" );

    static final Locale DA = Locale.forLanguageTag( "da" );

    static final Locale DE = Locale.forLanguageTag( "de" );

    static final NodePath CITIES = new NodePath( "/cities" );

    static final NodePath COLLATION = new NodePath( "/collation" );

    static final NodePath TREE = new NodePath( "/tree" );

    static final NodePath ACL = new NodePath( "/acl" );

    static final NodePath PAGE = new NodePath( "/page" );

    static final NodePath UNTYPED = new NodePath( "/untyped" );

    /** Origin for every geo-distance sort and aggregation in the corpus (Oslo). */
    static final GeoPoint ORIGIN = GeoPoint.from( "59.9127300,10.7460900" );

    static final PrincipalKey SECRET_USER = PrincipalKey.ofUser( IdProviderKey.system(), "corpus-secret" );

    static final PrincipalKey NOBODY = PrincipalKey.ofUser( IdProviderKey.system(), "corpus-nobody" );

    private CorpusFixture()
    {
    }

    /**
     * A city row. Kept as a record so the table below reads as data.
     */
    private record City(String id, String title, String category, long population, String founded, String location, boolean active,
                        String description)
    {
    }

    /**
     * Categories: c1 x4, c2 x3, c3 x1 -- no doc-count ties, so a terms aggregation ordered by
     * count is fully deterministic. Populations and instants are pairwise distinct for the same
     * reason. Descriptions are Norwegian/Swedish/German so stemming and ASCII folding have
     * something to bite on.
     */
    private static final List<City> CITIES_DATA =
        List.of( new City( "city-oslo", "Oslo", "c1", 700000, "2020-01-15T00:00:00Z", "59.9127300,10.7460900", true,
                           "fisk og laks i Oslo" ),
                 new City( "city-bergen", "Bergen", "c1", 280000, "2020-02-20T00:00:00Z", "60.3929900,5.3241500", true,
                           "fisk og grønnsaker i Bergen" ),
                 new City( "city-tromso", "Tromsø", "c2", 77000, "2020-03-25T00:00:00Z", "69.6492000,18.9552900", false,
                           "laks og reker fra nord" ),
                 new City( "city-alesund", "Ålesund", "c2", 67000, "2020-06-01T00:00:00Z", "62.4722500,6.1549200", true,
                           "fisk fisk og mer fisk" ),
                 new City( "city-arendal", "Arendal", "c3", 45000, "2021-01-10T00:00:00Z", "58.4613800,8.7724000", false,
                           "grønnsaker og bær" ),
                 new City( "city-berlin", "Berlin", "c1", 3600000, "2021-05-05T00:00:00Z", "52.5243700,13.4105300", true,
                           "Straße und Fische" ),
                 new City( "city-malmo", "Malmö", "c2", 350000, "2022-08-08T00:00:00Z", "55.6058700,13.0007300", true,
                           "fisk och grönsaker" ),
                 new City( "city-aarhus", "Århus", "c1", 285000, "2023-12-31T00:00:00Z", "56.1567400,10.2107600", false,
                           "fisk og øl" ) );

    /**
     * Words that separate ICU collation from code-point order. After {@code toLowerCase} the raw
     * code points run å (U+00E5) &lt; æ (U+00E6) &lt; ö (U+00F6) &lt; ø (U+00F8); Norwegian/Danish
     * collation gives æ &lt; ø &lt; å, Swedish gives å &lt; ä &lt; ö, and German folds ä near a
     * and ß to ss.
     */
    private static final List<String> COLLATION_WORDS =
        List.of( "aal", "ähnlich", "zulu", "æsel", "øl", "år", "åre", "ärlig", "öl", "straße" );

    static void seed( final Function<CreateNodeParams, Node> createNode )
    {
        seedCities( createNode );
        seedCollation( createNode );
        seedTree( createNode );
        seedAcl( createNode );
        seedPaging( createNode );
        seedUntyped( createNode );
    }

    /**
     * The bare/untyped STRING sub-field, i.e. {@link com.enonic.xp.repo.impl.index.StaticIndexValueType#STRING}
     * whose postfix is the empty string, so the field is indexed under its plain name with no
     * dotted suffix. Gate 0(d) decision 2 asks whether OpenSearch should map it to
     * {@code keyword}, and says the answer must come from THIS corpus in ES mode rather than from
     * reasoning -- so these five values are chosen to make every candidate mapping produce a
     * different, visible answer:
     * <ul>
     * <li>{@code phrase} = a MULTI-WORD value. Under a keyword-style analyzer the whole value is
     * one token, so an exact multi-word term query matches and a single-word one does not. Under a
     * word-splitting analyzer (ES 2.4's dynamic default {@code standard}) it is the exact
     * opposite. ES's {@code term} query never analyses its argument, so the two mappings cannot
     * both be right.</li>
     * <li>a value that differs from the query only in CASE.</li>
     * <li>a value carrying PUNCTUATION and HYPHENATION, which a word-splitting analyzer strips and
     * a keyword analyzer keeps verbatim.</li>
     * <li>a value carrying a non-ASCII character, to show whether ASCII folding is in play on the
     * bare field (it is present in {@code default_search} but not in {@code keywordlowercase}).</li>
     * <li>a value longer than the mapping's {@code ignore_above}, which is silently NOT indexed on
     * the bare field while its {@code ._analyzed} sibling still is.</li>
     * </ul>
     */
    private static void seedUntyped( final Function<CreateNodeParams, Node> createNode )
    {
        createNode.apply( folder( "untyped", NodePath.ROOT ) );

        untyped( createNode, "untyped-multiword", "Oslo Sentrum Vest" );
        untyped( createNode, "untyped-case", "MiXeD Case Value" );
        untyped( createNode, "untyped-punctuation", "levenshtein-algoritme (v2.1)" );
        untyped( createNode, "untyped-nonascii", "Ålesund Sentrum" );
        untyped( createNode, "untyped-singleword", "singleword" );
        // 4000 chars: above the bare field's ignore_above (3072) but below any limit on ._analyzed
        untyped( createNode, "untyped-toolong", "z".repeat( 4000 ) );
    }

    private static void untyped( final Function<CreateNodeParams, Node> createNode, final String id, final String phrase )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "phrase", phrase );

        createNode.apply( CreateNodeParams.create()
                              .setNodeId( NodeId.from( id ) )
                              .name( id )
                              .parent( UNTYPED )
                              .data( data )
                              .build() );
    }

    /**
     * {@code BY_TYPE} already yields {@code ._analyzed}/{@code ._ngram}/{@code ._orderby} for
     * every string (IndexItemFactory#createFullText / #createOrderBy). The explicit
     * {@code description} entry adds the Norwegian stemmer ({@code ._stemmed_no}) and the
     * all-text config adds {@code _allText._stemmed_no}, which is what
     * {@code stemmed('_allText', ..., 'no')} resolves to.
     */
    private static IndexConfigDocument citiesIndexConfig()
    {
        final IndexConfig description = IndexConfig.create()
            .enabled( true )
            .decideByType( false )
            .fulltext( true )
            .nGram( true )
            .includeInAllText( true )
            .path( false )
            .addLanguage( NO )
            .build();

        return PatternIndexConfigDocument.create()
            .defaultConfig( IndexConfig.BY_TYPE )
            .add( "description", description )
            .allTextConfig( AllTextIndexConfig.create().addLanguage( NO ).build() )
            .build();
    }

    private static void seedCities( final Function<CreateNodeParams, Node> createNode )
    {
        createNode.apply( folder( "cities", NodePath.ROOT ) );

        final IndexConfigDocument indexConfig = citiesIndexConfig();

        for ( final City city : CITIES_DATA )
        {
            final PropertyTree data = new PropertyTree();
            data.addString( "title", city.title() );
            data.addString( "description", city.description() );
            data.addString( "category", city.category() );
            data.addLong( "population", city.population() );
            data.addInstant( "founded", Instant.parse( city.founded() ) );
            data.addGeoPoint( "location", GeoPoint.from( city.location() ) );
            data.addBoolean( "active", city.active() );

            // Field-name-resolution edge cases (Gate 0(c) rules 1-4): a field whose NAME carries
            // upper case, and a field holding "42" as a STRING on one node and 42 as a LONG on
            // another, so the same logical path can be queried as string and as number.
            data.addString( "MixedCaseField", "MixedCaseValue" );
            if ( "city-oslo".equals( city.id() ) )
            {
                data.addString( "numish", "42" );
            }
            else if ( "city-bergen".equals( city.id() ) )
            {
                data.addLong( "numish", 42L );
            }

            createNode.apply( CreateNodeParams.create()
                                  .setNodeId( NodeId.from( city.id() ) )
                                  .name( city.id() )
                                  .parent( CITIES )
                                  .data( data )
                                  .indexConfigDocument( indexConfig )
                                  .build() );
        }
    }

    private static void seedCollation( final Function<CreateNodeParams, Node> createNode )
    {
        createNode.apply( folder( "collation", NodePath.ROOT ) );

        // nb -> _orderby_no, sv -> _orderby_sv, da -> _orderby_da, de -> _orderby_ducet
        // (IndexLanguageController maps German's order-by to DUCET, not to a de-specific field --
        // a fact the corpus pins on purpose).
        final IndexConfig wordConfig = IndexConfig.create( IndexConfig.BY_TYPE )
            .addLanguage( NB )
            .addLanguage( SV )
            .addLanguage( DA )
            .addLanguage( DE )
            .build();

        final IndexConfigDocument indexConfig =
            PatternIndexConfigDocument.create().defaultConfig( IndexConfig.BY_TYPE ).add( "word", wordConfig ).build();

        for ( int i = 0; i < COLLATION_WORDS.size(); i++ )
        {
            final String id = String.format( Locale.ROOT, "coll-%02d", i + 1 );
            final PropertyTree data = new PropertyTree();
            data.addString( "word", COLLATION_WORDS.get( i ) );

            createNode.apply( CreateNodeParams.create()
                                  .setNodeId( NodeId.from( id ) )
                                  .name( id )
                                  .parent( COLLATION )
                                  .data( data )
                                  .indexConfigDocument( indexConfig )
                                  .build() );
        }
    }

    private static void seedTree( final Function<CreateNodeParams, Node> createNode )
    {
        createNode.apply( folder( "tree", NodePath.ROOT ) );
        createNode.apply( folder( "a", TREE ) );
        createNode.apply( folder( "b", new NodePath( "/tree/a" ) ) );
        createNode.apply( folder( "c", new NodePath( "/tree/a/b" ) ) );
        createNode.apply( folder( "d", new NodePath( "/tree/a/b/c" ) ) );
        createNode.apply( folder( "x", new NodePath( "/tree/a" ) ) );
        createNode.apply( folder( "q", TREE ) );
    }

    private static void seedAcl( final Function<CreateNodeParams, Node> createNode )
    {
        createNode.apply( folder( "acl", NodePath.ROOT ) );

        createNode.apply( CreateNodeParams.create()
                              .setNodeId( NodeId.from( "acl-public" ) )
                              .name( "acl-public" )
                              .parent( ACL )
                              .build() );

        createNode.apply( CreateNodeParams.create()
                              .setNodeId( NodeId.from( "acl-secret" ) )
                              .name( "acl-secret" )
                              .parent( ACL )
                              .inheritPermissions( false )
                              .permissions( allowAll( SECRET_USER ) )
                              .build() );

        createNode.apply( CreateNodeParams.create()
                              .setNodeId( NodeId.from( "acl-nobody" ) )
                              .name( "acl-nobody" )
                              .parent( ACL )
                              .inheritPermissions( false )
                              .permissions( allowAll( NOBODY ) )
                              .build() );
    }

    private static void seedPaging( final Function<CreateNodeParams, Node> createNode )
    {
        createNode.apply( folder( "page", NodePath.ROOT ) );

        for ( int i = 1; i <= 25; i++ )
        {
            final String id = String.format( Locale.ROOT, "page-%02d", i );
            final PropertyTree data = new PropertyTree();
            data.addLong( "seq", (long) i );
            data.addString( "bucket", i <= 10 ? "low" : "high" );

            createNode.apply( CreateNodeParams.create()
                                  .setNodeId( NodeId.from( id ) )
                                  .name( id )
                                  .parent( PAGE )
                                  .data( data )
                                  // descending manual order value, so the manual sort is the
                                  // reverse of the id order and cannot accidentally agree with it
                                  .manualOrderValue( (long) ( 1000 - i ) )
                                  .build() );
        }
    }

    private static CreateNodeParams folder( final String name, final NodePath parent )
    {
        return CreateNodeParams.create().setNodeId( NodeId.from( "n-" + name ) ).name( name ).parent( parent ).build();
    }

    private static AccessControlList allowAll( final PrincipalKey principal )
    {
        return AccessControlList.create()
            .add( AccessControlEntry.create().principal( principal ).allowAll().build() )
            .add( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() )
            .build();
    }
}
