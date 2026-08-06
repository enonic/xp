package com.enonic.nodb.engine.search;

import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Translates XP's canonical JSON query DSL into an OpenSearch query. This is the server side
 * of Phase 4 decision 1/2: the wire carries ONE language, and the postfix table
 * ({@link IndexFields}) that turns a logical field name into a physical one lives here, next
 * to the mapping that declares those fields and next to the projection that writes them.
 *
 * <p><b>Gate C covers the structured families</b> — {@code term}, {@code in}, {@code like},
 * {@code range}, {@code exists}, {@code boolean} (incl. the bare-{@code NOT} form),
 * {@code matchAll}, the whole filter vocabulary ({@code values}, {@code ids}, {@code exists},
 * {@code range}, {@code boolean}) and field sorts. <b>Gate D adds the text family</b> —
 * {@code fulltext}, {@code ngram}, {@code stemmed} (one {@code simple_query_string} base, three
 * field/analyzer resolutions) and {@code pathMatch} — plus geo-distance and {@code COLLATE}
 * sorts. Aggregations remain Gate E and still throw {@link UnsupportedQueryException}: a
 * half-translated construct that silently returns the wrong hits is the one failure mode this
 * port cannot afford.
 *
 * <h2>Field-name resolution — the six rules (Gate 0(b)+(c))</h2>
 * <ol>
 * <li><b>Lowercased and trimmed.</b> Applied by {@code IndexPath} ON THE XP SIDE, before the
 * name is rendered, so names arrive already normalized. Reproduced defensively here anyway:
 * a physical name derived from an un-normalized logical one is a silent zero-hit query.</li>
 * <li><b>A postfix per value type.</b> {@link IndexFields#physicalName} owns the table.</li>
 * <li><b>Value → sub-field, in the order dated → numeric → geo → raw.</b> An explicit
 * {@code type} wins ({@code dateTime}/{@code time} → {@code ._datetime}); otherwise a JSON
 * number reaches {@code ._number}; everything else lands on the base text variant.
 * {@code geoPoint} is rejected rather than resolved — see {@link #GEO_POINT}.</li>
 * <li><b>Value coercion must match the sub-field.</b> Numerics are ALWAYS {@code Double} (an
 * integer term against a {@code double} field is a different query in a sort context, and
 * XP's own {@code ValueHelper} always widened); strings bound for the text variant are
 * {@code trim().toLowerCase()}, exactly as the projection writes them.</li>
 * <li><b>Order-by.</b> {@code _score}/{@code _id}/{@code _doc} pass through unmodified;
 * everything else resolves to {@code ._orderby}, or to the locale-qualified
 * {@code ._orderby_<loc>} when the sort carries a {@code language} — resolved by
 * {@link IndexLanguages}, which must agree with the indexer field for field. A geo-distance
 * sort is the one form that is not an order-by field at all.</li>
 * <li><b>The store resolver ignores value type entirely.</b> It has no Phase-4 role: decision
 * D2 moved the branch/storage queries to SQL, so nothing reaches this translator with
 * store-mode field names — which is why there is no {@code fieldResolution} flag on the
 * envelope (D6, resolved by construction in Gate B).</li>
 * </ol>
 * {@code in} and {@code like} are the two constructs whose XP behaviour the rules describe
 * inconsistently, and both are settled by rulings rather than by this table: {@code like}
 * forces the base string field (rule 3 does not apply — a wildcard pattern is text by
 * definition), while {@code in} resolves PER VALUE TYPE, which is decision D4's ruling that
 * gap G-2 be FIXED rather than preserved. The corpus's {@code GAP-G2-*} rows record what ES
 * did instead; they are expected deltas, not regressions.
 */
public final class QueryDslTranslator
{
    private static final String TYPE_DATETIME = "dateTime";

    /**
     * The other spelling {@code ExpressionQueryBuilder}'s type switch accepts. Nothing in XP
     * renders it today, but the wire schema is XP's public DSL plus a superset, and silently
     * rejecting a type the DSL documents would be a gratuitous behaviour difference.
     */
    private static final String TYPE_TIME = "time";

    /**
     * Rejected rather than translated (decision D3). A geo-typed term/range is not a gap in
     * this backend: Gate 0(e) MEASURED it erroring in Elasticsearch 2.4 as well
     * ({@code IndexException: Search request failed} — a {@code term} query against a
     * {@code geo_point} field is invalid), and the DSL family rejects it outright with "there
     * is no [geoPoint] dsl expression". Failing fast MATCHES today's behaviour; inventing the
     * capability would not.
     */
    private static final String GEO_POINT = "geoPoint";

    /**
     * The document {@code _id} — the only "field" in a query that is not a field.
     *
     * <p>{@code NodeIndexPath.ID} is {@code _id}, and XP queried it as an ordinary term
     * because in ES 2.4 {@code _id} was a queryable metadata field holding the bare node id.
     * Here it is neither: {@code _id} is rejected as a document field by OpenSearch outright
     * (see {@link IndexFields}), and the metadata {@code _id} is the COMPOSITE
     * {@code <nodeId>@<branch>} of decision D10. So an {@code _id} predicate is rewritten into
     * an OpenSearch {@code ids} query over the composite ids, expanded across the request's
     * source branches (the ACL/source filter already restricts which of those can match, so
     * the expansion cannot widen the result).
     *
     * <p>The alternative — adding a bare node-id field to the document — was rejected: it
     * would bump {@link IndexDocumentProjection#VERSION} and therefore require a generational
     * rebuild, to store a value the {@code _id} metadata already carries.
     */
    private static final String ID_FIELD = "_id";

    private static final String SCORE_FIELD = "_score";

    private static final String DOC_FIELD = "_doc";

    /**
     * The analyzed sub-field, in CANONICAL (XP) spelling — {@link IndexFields#physicalName} turns
     * it into {@code _fulltext}. Written this way rather than as the physical name so the D1b
     * rename lives in exactly one place.
     */
    private static final String FULLTEXT_POSTFIX = IndexFields.FULLTEXT_POSTFIX;

    private static final String NGRAM_POSTFIX = "_ngram";

    /** {@code NodeConstants.DEFAULT_FULLTEXT_SEARCH_ANALYZER}, and a real analyzer in the template. */
    private static final String FULLTEXT_SEARCH_ANALYZER = "fulltext_search_default";

    /** {@code NodeConstants.DEFAULT_NGRAM_SEARCH_ANALYZER}. */
    private static final String NGRAM_SEARCH_ANALYZER = "ngram_search_default";

    /**
     * The path-hierarchy analyzer, named EXPLICITLY on the {@code pathMatch} query.
     *
     * <p>Discovered by measurement, and the reason this constant exists rather than being inherited
     * from the mapping: the template gives {@code *._path} an {@code analyzer} and no
     * {@code search_analyzer}, which on ES 2.4 meant the field's own analyzer ran at search time
     * too — so {@code pathMatch('/tree/a/b/c/d')} tokenized into every prefix and matched every
     * ancestor (corpus row {@code PATH-01}: 7 hits). On OpenSearch 3.7 the index-level analyzer
     * named {@code default_search} wins over a field's {@code analyzer} at search time, so the same
     * query analyzed to ONE keyword token and matched only the exact path. Zero error, wrong hits —
     * the failure shape this gate is built to catch.
     *
     * <p>Fixed on the QUERY side rather than by adding {@code search_analyzer} to the template,
     * because that puts all four text constructs on one rule — <b>the translator names the search
     * analyzer and never inherits one</b> — which is what {@code fulltext}, {@code ngram} and
     * {@code stemmed} already did (they pass theirs explicitly, which is why they were unaffected
     * and why only {@code pathMatch} broke). It also needs no mapping change, and therefore no
     * {@code +g(N+1)} rebuild for a difference that is purely about how a query is parsed.
     */
    private static final String PATH_ANALYZER = "path_analyzer";

    /** The sort {@code type} the wire uses for a geo-distance order (Gate B's {@code OrderDslRenderer}). */
    private static final String GEO_DISTANCE = "geoDistance";

    /**
     * Branch names of the request's sources, case-preserved. Needed only to expand {@code _id}
     * predicates into composite document ids; every other construct is branch-agnostic because
     * the source filter carries the branch term.
     */
    private final List<String> branches;

    public QueryDslTranslator( List<String> branches )
    {
        this.branches = List.copyOf( branches );
    }

    /** Thrown for any DSL construct this gate does not translate. Never a silent fallback. */
    public static class UnsupportedQueryException
        extends RuntimeException
    {
        public UnsupportedQueryException( String message )
        {
            super( message );
        }
    }

    // --- queries ---------------------------------------------------------------------

    public ObjectNode translateQuery( JsonNode dsl )
    {
        if ( dsl == null || !dsl.isObject() || dsl.size() != 1 )
        {
            throw new UnsupportedQueryException( "A query must be a JSON object with exactly one root expression" );
        }

        String name = dsl.fieldNames().next();
        JsonNode expression = dsl.get( name );

        return switch ( name )
        {
            case "matchAll" -> wrap( "match_all", boosted( object(), expression ) );
            case "term" -> term( expression );
            case "in" -> in( expression );
            case "like" -> like( expression );
            case "range" -> range( expression );
            case "exists" -> exists( expression );
            case "boolean" -> bool( expression );
            case "fulltext" -> fulltext( expression );
            case "ngram" -> ngram( expression );
            case "stemmed" -> stemmed( expression );
            case "pathMatch" -> pathMatch( expression );
            default -> throw new UnsupportedQueryException( "Query type '" + name + "' is not supported" );
        };
    }

    private ObjectNode term( JsonNode expression )
    {
        String type = text( expression, "type" );
        JsonNode value = require( expression, "value", "term" );

        if ( isIdField( expression ) )
        {
            return idsQuery( List.of( value ), expression );
        }

        ObjectNode body = object();
        body.set( "value", resolveValue( value, type ) );
        applyBoost( body, expression );

        return wrap( "term", object().set( fieldName( expression, type, value ), body ) );
    }

    /**
     * N {@code should} term clauses, not a {@code terms} query. XP's own {@code in} fans out
     * the same way, and a {@code terms} query scores differently — the acceptance rule forbids
     * order drift on deterministic sorts, so the fan-out is preserved rather than optimized.
     */
    private ObjectNode in( JsonNode expression )
    {
        String type = text( expression, "type" );
        JsonNode values = require( expression, "values", "in" );

        if ( isIdField( expression ) )
        {
            return idsQuery( elements( values ), expression );
        }

        ArrayNode should = OpenSearchClient.mapper().createArrayNode();
        for ( JsonNode value : elements( values ) )
        {
            ObjectNode body = object();
            body.set( "value", resolveValue( value, type ) );
            should.add( wrap( "term", object().set( fieldName( expression, type, value ), body ) ) );
        }
        if ( should.isEmpty() )
        {
            throw new UnsupportedQueryException( "Cannot translate an empty 'in' statement" );
        }

        ObjectNode body = object();
        body.set( "should", should );
        applyBoost( body, expression );
        return wrap( "bool", body );
    }

    /** Rule 3 does not apply: a wildcard pattern is text by definition, so this is always the base variant. */
    private ObjectNode like( JsonNode expression )
    {
        JsonNode value = require( expression, "value", "like" );
        rejectIdField( expression, "like" );

        ObjectNode body = object();
        body.put( "value", normalize( value.asText() ) );
        applyBoost( body, expression );

        return wrap( "wildcard", object().set( baseFieldName( expression ), body ) );
    }

    private ObjectNode range( JsonNode expression )
    {
        String type = text( expression, "type" );
        rejectIdField( expression, "range" );

        ObjectNode body = object();
        JsonNode reference = null;
        for ( String bound : List.of( "gt", "gte", "lt", "lte" ) )
        {
            JsonNode value = expression.get( bound );
            if ( value != null && !value.isNull() )
            {
                reference = reference == null ? value : reference;
                body.set( bound, resolveValue( value, type ) );
            }
        }
        if ( reference == null )
        {
            throw new UnsupportedQueryException( "A 'range' query needs at least one of gt/gte/lt/lte" );
        }
        applyBoost( body, expression );

        return wrap( "range", object().set( fieldName( expression, type, reference ), body ) );
    }

    /** {@code ExistsFilter} forces the base string field regardless of value type (rule 3's exception). */
    private ObjectNode exists( JsonNode expression )
    {
        rejectIdField( expression, "exists" );

        ObjectNode body = object();
        body.put( "field", baseFieldName( expression ) );
        return wrap( "exists", body );
    }

    /**
     * Nesting is preserved exactly as it arrives: a pairwise-nested {@code must} is NOT
     * flattened into a flat clause list. The two are set-equivalent but not score-equivalent.
     * A bare {@code NOT} arrives as this same construct with only a {@code mustNot} clause,
     * which is how both XP builder families express it too — there is no separate {@code not}
     * query type to translate.
     */
    private ObjectNode bool( JsonNode expression )
    {
        ObjectNode body = object();
        for ( String clause : List.of( "must", "should", "mustNot", "filter" ) )
        {
            JsonNode operand = expression.get( clause );
            if ( operand == null )
            {
                continue;
            }
            ArrayNode translated = OpenSearchClient.mapper().createArrayNode();
            for ( JsonNode element : elements( operand ) )
            {
                translated.add( translateQuery( element ) );
            }
            body.set( "mustNot".equals( clause ) ? "must_not" : clause, translated );
        }
        if ( body.isEmpty() )
        {
            throw new UnsupportedQueryException( "A 'boolean' query needs at least one of must/should/mustNot/filter" );
        }
        applyBoost( body, expression );
        return wrap( "bool", body );
    }

    // --- the text family -------------------------------------------------------------

    /**
     * {@code fulltext} — the analyzed variant, {@code ._fulltext} here and {@code ._analyzed} in
     * XP's vocabulary (D1b).
     *
     * <p><b>An empty query string is {@code match_all}</b>, not an empty
     * {@code simple_query_string}. That is XP's own recorded behaviour and it is a short-circuit,
     * not a degenerate case: {@code FulltextFunction} returns {@code MatchAllQueryBuilder} before
     * it even resolves the field names. The renderer already applies the same rule for the NoQL
     * form, so this branch exists for the DSL form — where {@code FulltextQueryBuilder} does NOT
     * short-circuit and instead emits {@code "query": null}. Reproducing the null would mean
     * sending OpenSearch a query it rejects, so the fulltext rule is applied uniformly here and
     * recorded as the one place the two XP builder families are reconciled rather than mirrored.
     *
     * <p>{@code ngram} and {@code stemmed} deliberately do NOT get this treatment — neither
     * {@code NGramFunction} nor {@code StemmedFunction} short-circuits, so an empty query string
     * stays an empty query string and matches nothing.
     */
    private ObjectNode fulltext( JsonNode expression )
    {
        String query = text( expression, "query" );
        if ( query == null || query.isEmpty() )
        {
            return wrap( "match_all", boosted( object(), expression ) );
        }
        return simpleQueryString( expression, FULLTEXT_POSTFIX, analyzer( expression, FULLTEXT_SEARCH_ANALYZER ) );
    }

    /** {@code ngram} — the edge-ngram variant. Same base, {@code ._ngram} and its own analyzer. */
    private ObjectNode ngram( JsonNode expression )
    {
        return simpleQueryString( expression, NGRAM_POSTFIX, analyzer( expression, NGRAM_SEARCH_ANALYZER ) );
    }

    /**
     * {@code stemmed} — the language variant. The 4th NoQL argument is a LANGUAGE TAG, not an
     * analyzer name: both the analyzer ({@code norwegian}) and the field postfix
     * ({@code ._stemmed_nb}) are derived from it by {@link IndexLanguages}, and a language with
     * no stemmer fails loudly there rather than querying an unanalyzed field.
     *
     * <p>The G-3 {@code analyzer} override does not apply here — XP has no form for it, because
     * the argument slot it would occupy is already the language.
     */
    private ObjectNode stemmed( JsonNode expression )
    {
        String language = text( expression, "language" );
        return simpleQueryString( expression, IndexLanguages.stemmedPostfix( language ),
                                  IndexLanguages.stemmedAnalyzer( language ) );
    }

    /**
     * The shared {@code simple_query_string} base of all three.
     *
     * <p>Every parameter XP sets, and only those: {@code query}, the weighted {@code fields},
     * {@code analyzer}, {@code default_operator} (lower-cased, defaulting to {@code or}) and
     * {@code analyze_wildcard: true}. {@code flags}, {@code lenient},
     * {@code minimum_should_match} and {@code _name} are never emitted by XP and are not emitted
     * here — an unset parameter is the engine's default on both sides, whereas a spelled-out
     * default is a value this port would then own.
     */
    private ObjectNode simpleQueryString( JsonNode expression, String postfix, String analyzer )
    {
        JsonNode fields = require( expression, "fields", "simple query string" );
        String query = text( expression, "query" );
        if ( query == null )
        {
            throw new UnsupportedQueryException( "A text query needs a 'query' string" );
        }

        ArrayNode resolved = OpenSearchClient.mapper().createArrayNode();
        for ( JsonNode field : elements( fields ) )
        {
            resolved.add( weightedField( field.asText(), postfix ) );
        }
        if ( resolved.isEmpty() )
        {
            throw new UnsupportedQueryException( "A text query needs at least one field" );
        }

        ObjectNode body = object();
        body.put( "query", query );
        body.set( "fields", resolved );
        body.put( "analyzer", analyzer );
        body.put( "default_operator", operator( expression ) );
        body.put( "analyze_wildcard", true );
        applyBoost( body, expression );

        return wrap( "simple_query_string", body );
    }

    /**
     * {@code data.title^5} → {@code data.title._fulltext^5.0}: the postfix is inserted BEFORE the
     * weight, because {@code ^} is {@code simple_query_string}'s own per-field boost syntax and a
     * postfix appended after it would become part of the number.
     *
     * <p>The weight is re-rendered through {@code Float.toString} rather than passed through as
     * written, which is what XP's builder did (its {@code fields} map holds a {@code Float}) —
     * so {@code ^5} and {@code ^5.0} are one query rather than two spellings of it. XP's
     * validation is reproduced too: a non-finite or negative weight is rejected rather than
     * silently reinterpreted by the engine.
     */
    private static String weightedField( String entry, String postfix )
    {
        int caret = entry.indexOf( '^' );
        if ( caret < 0 )
        {
            return physicalTextField( entry, postfix );
        }

        float weight;
        try
        {
            weight = Float.parseFloat( entry.substring( caret + 1 ) );
        }
        catch ( NumberFormatException e )
        {
            throw new UnsupportedQueryException( "Invalid field weight in '" + entry + "'" );
        }
        if ( !Float.isFinite( weight ) || weight < 0 )
        {
            throw new UnsupportedQueryException( "Invalid field weight in '" + entry + "'" );
        }
        return physicalTextField( entry.substring( 0, caret ), postfix ) + "^" + Float.toString( weight );
    }

    /**
     * A text-family field name.
     *
     * <p>A trailing {@code *} is a WILDCARD over field names ({@code descri*}, corpus row
     * {@code TEXT-07}), which {@code simple_query_string} expands itself. Appending a postfix
     * after the star would produce {@code descri*._fulltext} — a pattern no field matches,
     * because the star already swallowed the rest of the name. XP had the same shape and the same
     * consequence; the wildcard is therefore moved to the END, so the pattern reaches every
     * matching field's analyzed sub-field.
     */
    private static String physicalTextField( String field, String postfix )
    {
        String logical = field.trim().toLowerCase( Locale.ROOT );
        if ( logical.isEmpty() )
        {
            throw new UnsupportedQueryException( "'fields' cannot contain an empty field name" );
        }
        if ( logical.endsWith( "*" ) )
        {
            return logical.substring( 0, logical.length() - 1 ) + "*." + postfix;
        }
        return logical + "." + postfix;
    }

    /** {@code AND}/{@code OR}, lower-cased for the wire exactly as XP's builder serialized it. */
    private static String operator( JsonNode expression )
    {
        String operator = text( expression, "operator" );
        if ( operator == null || operator.isBlank() )
        {
            return "or";
        }
        String normalized = operator.trim().toUpperCase( Locale.ROOT );
        if ( !"AND".equals( normalized ) && !"OR".equals( normalized ) )
        {
            throw new UnsupportedQueryException( "Invalid operator '" + operator + "' (expected AND or OR)" );
        }
        return normalized.toLowerCase( Locale.ROOT );
    }

    /** G-3: the optional analyzer override, which Gate B's renderer emits as a wire-superset field. */
    private static String analyzer( JsonNode expression, String defaultAnalyzer )
    {
        String analyzer = text( expression, "analyzer" );
        return analyzer == null || analyzer.isBlank() ? defaultAnalyzer : analyzer;
    }

    /**
     * {@code pathMatch} — {@code PathMatchFunction}'s exact shape, on {@code ._path}.
     *
     * <p>A plain {@code match} against the {@code path_hierarchy}-analyzed field: the query text
     * tokenizes into every prefix of the path, so a document scores by HOW MANY path segments it
     * shares with the argument — which is what makes "most-matching first" the natural order and
     * why the row is a SET row rather than an EXACT one.
     *
     * <p>{@code minimumMatch > 1} adds a second clause under {@code bool.must}: a {@code term} on
     * the path truncated to that many segments, which is a hard floor on the shared prefix rather
     * than a scoring hint. The truncation is {@code split("/")} then {@code limit(n + 1)} — the
     * {@code + 1} is not an off-by-one, it accounts for the empty leading element a
     * leading-slash path produces. Neither clause carries a boost, and the value is passed
     * through UNNORMALIZED: XP does not lower-case it either, and the field's own analyzer chain
     * is what makes that work.
     */
    private ObjectNode pathMatch( JsonNode expression )
    {
        String field = IndexFields.physicalName( logicalField( expression ) + "._path" );
        JsonNode pathNode = require( expression, "path", "pathMatch" );
        String path = pathNode.asText();

        ObjectNode match =
            wrap( "match", object().set( field, object().put( "query", path ).put( "analyzer", PATH_ANALYZER ) ) );

        int minimumMatch = minimumMatch( expression );
        if ( minimumMatch <= 1 )
        {
            ObjectNode body = (ObjectNode) match.get( "match" ).get( field );
            applyBoost( body, expression );
            return match;
        }

        String[] segments = path.split( "/" );
        String minimumPath = String.join( "/", List.of( segments ).subList( 0, Math.min( segments.length, minimumMatch + 1 ) ) );

        ArrayNode must = OpenSearchClient.mapper().createArrayNode();
        must.add( wrap( "term", object().set( field, object().put( "value", minimumPath ) ) ) );
        must.add( match );

        // No boost: PathMatchFunction's minimumMatch branch returns the bool without one, and the
        // DSL builder drops it on that branch too. Reproduced rather than improved.
        return wrap( "bool", object().set( "must", must ) );
    }

    private static int minimumMatch( JsonNode expression )
    {
        JsonNode node = expression.get( "minimumMatch" );
        if ( node == null || node.isNull() )
        {
            return 1;
        }
        if ( node.isNumber() )
        {
            return (int) node.doubleValue();
        }
        try
        {
            return (int) Double.parseDouble( node.asText() );
        }
        catch ( NumberFormatException e )
        {
            throw new UnsupportedQueryException( "'minimumMatch' must be a number, got '" + node.asText() + "'" );
        }
    }

    // --- filters ---------------------------------------------------------------------

    public ObjectNode translateFilter( JsonNode dsl )
    {
        if ( dsl == null || !dsl.isObject() || dsl.size() != 1 )
        {
            throw new UnsupportedQueryException( "A filter must be a JSON object with exactly one root expression" );
        }

        String name = dsl.fieldNames().next();
        JsonNode expression = dsl.get( name );

        return switch ( name )
        {
            case "values" -> valuesFilter( expression );
            case "ids" -> idsFilter( expression );
            case "exists" -> exists( expression );
            case "range" -> range( expression );
            case "boolean" -> booleanFilter( expression );
            default -> throw new UnsupportedQueryException( "Filter type '" + name + "' is not supported" );
        };
    }

    private ObjectNode valuesFilter( JsonNode expression )
    {
        String type = text( expression, "type" );
        JsonNode values = require( expression, "values", "values" );

        if ( isIdField( expression ) )
        {
            return idsQuery( elements( values ), expression );
        }

        ArrayNode terms = OpenSearchClient.mapper().createArrayNode();
        String field = null;
        for ( JsonNode value : elements( values ) )
        {
            field = field == null ? fieldName( expression, type, value ) : field;
            terms.add( resolveValue( value, type ) );
        }
        if ( field == null )
        {
            throw new UnsupportedQueryException( "Cannot translate an empty 'values' filter" );
        }
        return wrap( "terms", object().set( field, terms ) );
    }

    /**
     * The {@code ids} filter targets its field VERBATIM — no text postfix, no normalization:
     * it is how case-sensitive identifiers are matched, which is exactly why XP used an id
     * filter rather than a value filter for branch names (a {@code ValueFilter} would lowercase
     * them). Its default field is {@code _id}, which takes the composite-id route.
     */
    private ObjectNode idsFilter( JsonNode expression )
    {
        JsonNode values = require( expression, "values", "ids" );

        if ( isIdField( expression ) )
        {
            return idsQuery( elements( values ), expression );
        }

        ArrayNode terms = OpenSearchClient.mapper().createArrayNode();
        for ( JsonNode value : elements( values ) )
        {
            terms.add( value.asText() );
        }
        return wrap( "terms", object().set( IndexFields.physicalName( logicalField( expression ) ), terms ) );
    }

    private ObjectNode booleanFilter( JsonNode expression )
    {
        ObjectNode body = object();
        for ( String clause : List.of( "must", "should", "mustNot" ) )
        {
            JsonNode operand = expression.get( clause );
            if ( operand == null )
            {
                continue;
            }
            ArrayNode translated = OpenSearchClient.mapper().createArrayNode();
            for ( JsonNode element : elements( operand ) )
            {
                translated.add( translateFilter( element ) );
            }
            body.set( "mustNot".equals( clause ) ? "must_not" : clause, translated );
        }
        if ( body.isEmpty() )
        {
            throw new UnsupportedQueryException( "A 'boolean' filter needs at least one of must/should/mustNot" );
        }
        return wrap( "bool", body );
    }

    // --- the composite-id rewrite -----------------------------------------------------

    /**
     * An {@code _id} predicate over bare node ids, as an OpenSearch {@code ids} query over
     * composite document ids. See {@link #ID_FIELD} for why this rewrite exists.
     *
     * <p>Node ids are NOT normalized: {@code NodeId} is an opaque token matched exactly, and the
     * composite id is built from it verbatim by the indexer.
     */
    private ObjectNode idsQuery( List<JsonNode> values, JsonNode expression )
    {
        if ( branches.isEmpty() )
        {
            throw new UnsupportedQueryException( "An '_id' predicate needs at least one source branch to resolve composite document ids" );
        }

        ArrayNode ids = OpenSearchClient.mapper().createArrayNode();
        for ( JsonNode value : values )
        {
            for ( String branch : branches )
            {
                ids.add( IndexFields.documentId( value.asText(), branch ) );
            }
        }
        if ( ids.isEmpty() )
        {
            throw new UnsupportedQueryException( "Cannot translate an empty '_id' predicate" );
        }

        ObjectNode body = object();
        body.set( "values", ids );
        applyBoost( body, expression );
        return wrap( "ids", body );
    }

    private boolean isIdField( JsonNode expression )
    {
        return ID_FIELD.equals( logicalField( expression ) );
    }

    private void rejectIdField( JsonNode expression, String construct )
    {
        if ( isIdField( expression ) )
        {
            throw new UnsupportedQueryException( "'_id' cannot be used in a '" + construct +
                                                     "' predicate: the document id is the composite <nodeId>@<branch> and is matchable only by exact id" );
        }
    }

    // --- sorts -----------------------------------------------------------------------

    /**
     * {@code unmappedType} is {@code keyword}, not {@code long}. Every {@code _orderby*} field
     * holds a lexicographically sortable ASCII string, so XP's hardcoded {@code long} was a
     * latent bug: harmless while the field is mapped everywhere, fatal on a multi-index sort
     * where one repo has never seen the property. The built-in pseudo-fields get no
     * {@code unmapped_type} at all.
     */
    public ObjectNode translateSort( JsonNode dsl )
    {
        String field = logicalField( dsl );
        String type = text( dsl, "type" );
        String direction = text( dsl, "direction" );
        String language = text( dsl, "language" );

        if ( type != null && !GEO_DISTANCE.equals( type ) )
        {
            throw new UnsupportedQueryException( "Not valid sort function: '" + type + "'" );
        }
        if ( GEO_DISTANCE.equals( type ) || dsl.get( "location" ) != null )
        {
            return geoDistanceSort( dsl, field, direction );
        }

        ObjectNode body = object();
        body.put( "order", direction == null ? "asc" : direction.toLowerCase( Locale.ROOT ) );

        if ( isPseudoField( field ) )
        {
            return object().set( field, body );
        }

        body.put( "unmapped_type", "keyword" );
        return object().set( IndexFields.physicalName( field + "." + orderByPostfix( language ) ), body );
    }

    /**
     * Rule 5's locale-qualified order-by, and D8's whole point: this is an ORDINARY KEYWORD SORT.
     *
     * <p>ES 2.4 mapped {@code *._orderby_<loc>} as an analyzed string with an
     * {@code icu_sort_<loc>} analyzer and sorted on it directly, so the collation contract lived
     * in the engine's ICU. Here {@link CollationKeyResolver} computed a hex collation key at index
     * time with a pinned icu4j and the field is a plain {@code keyword} — so the sort side has no
     * collation feature to configure, and there is nothing here that a numeric or date sort does
     * not also do. The only language-aware step left is picking WHICH field, which is
     * {@link IndexLanguages}' job and must agree with the indexer exactly.
     */
    private static String orderByPostfix( String language )
    {
        return language == null || language.isBlank() ? "_orderby" : IndexLanguages.orderByPostfix( language );
    }

    /**
     * {@code {"_geo_distance": {"<field>._geopoint": [{"lat":..,"lon":..}], "unit":.., "order":..}}}
     * — the shape ES 2.4's {@code GeoDistanceSortBuilder} serialized, which OpenSearch still
     * accepts verbatim. The builder API changed; the JSON did not, which is exactly why this
     * translator emits JSON rather than driving a typed client.
     *
     * <p>{@code order} is always written. ES 2.4 omitted it for {@code asc} (its builder only
     * emitted the non-default), but the wire always carries an explicit direction and {@code asc}
     * is also OpenSearch's default, so spelling it out is the same query and one less implicit
     * value. {@code distance_type} is NOT set — XP never set it either; note that means the
     * default moved from ES 2.4's {@code sloppy_arc} to OpenSearch's {@code arc}, i.e. distances
     * are now computed exactly rather than approximately. That changes the sort VALUES in their
     * low-order digits while leaving the ORDER identical, and is recorded as a documented delta
     * rather than pinned: {@code sloppy_arc} no longer exists to pin it to.
     */
    private ObjectNode geoDistanceSort( JsonNode dsl, String field, String direction )
    {
        JsonNode location = dsl.get( "location" );
        if ( location == null || location.get( "lat" ) == null || location.get( "lon" ) == null )
        {
            throw new UnsupportedQueryException( "A geoDistance sort needs a 'location' with 'lat' and 'lon'" );
        }
        if ( isPseudoField( field ) )
        {
            throw new UnsupportedQueryException( "'" + field + "' cannot be sorted by geo distance" );
        }

        ObjectNode point = object();
        point.put( "lat", location.get( "lat" ).doubleValue() );
        point.put( "lon", location.get( "lon" ).doubleValue() );

        ObjectNode body = object();
        body.set( IndexFields.physicalName( field + "._geopoint" ), OpenSearchClient.mapper().createArrayNode().add( point ) );
        String unit = text( dsl, "unit" );
        if ( unit != null && !unit.isBlank() )
        {
            body.put( "unit", unit );
        }
        body.put( "order", direction == null ? "asc" : direction.toLowerCase( Locale.ROOT ) );

        return wrap( "_geo_distance", body );
    }

    private static boolean isPseudoField( String field )
    {
        return SCORE_FIELD.equals( field ) || ID_FIELD.equals( field ) || DOC_FIELD.equals( field );
    }

    // --- field names and values ------------------------------------------------------

    /** Rule 3: an explicit type wins, then a JSON number, then the base text variant. */
    private static String fieldName( JsonNode expression, String type, JsonNode value )
    {
        String field = logicalField( expression );

        if ( type == null || type.isBlank() )
        {
            return IndexFields.physicalName( value != null && value.isNumber() ? field + "._number" : field );
        }
        if ( TYPE_DATETIME.equals( type ) || TYPE_TIME.equals( type ) )
        {
            return IndexFields.physicalName( field + "._datetime" );
        }
        // Matches the DSL family's own wording, which the corpus recorded as the current
        // behaviour of both XP builder families (Gate 0(e), GAP-G1-* rows).
        throw new UnsupportedQueryException( "There is no [" + type + "] dsl expression type in this backend" );
    }

    /** The base text variant, for the constructs that force it: {@code like} and {@code exists}. */
    private static String baseFieldName( JsonNode expression )
    {
        return IndexFields.physicalName( logicalField( expression ) );
    }

    /**
     * Rule 1, reproduced defensively. {@code IndexPath} already lowercased and trimmed every
     * name on the XP side; doing it again here costs nothing and means a caller that hand-builds
     * an envelope (a test, a future non-XP client) cannot produce a physically wrong field name
     * that silently matches nothing.
     */
    private static String logicalField( JsonNode expression )
    {
        String field = text( expression, "field" );
        if ( field == null || field.isBlank() )
        {
            throw new UnsupportedQueryException( "'field' cannot be empty" );
        }
        return field.trim().toLowerCase( Locale.ROOT );
    }

    /**
     * Rule 4. Dates ride as epoch millis, which is what the mapping's first date format expects
     * and what the projection writes; numerics become {@code Double} unconditionally, matching
     * both the {@code double} mapping of every {@code ._number} field and XP's own always-widening
     * {@code ValueHelper}; strings are normalized for the text variant. The wire's ISO-instant
     * form is parsed here so the engine never has to guess a format.
     */
    private static JsonNode resolveValue( JsonNode value, String type )
    {
        if ( TYPE_DATETIME.equals( type ) || TYPE_TIME.equals( type ) )
        {
            return OpenSearchClient.mapper().getNodeFactory().numberNode( instantMillis( value ) );
        }
        if ( GEO_POINT.equals( type ) )
        {
            throw new UnsupportedQueryException( "There is no [" + GEO_POINT + "] dsl expression type in this backend" );
        }
        if ( value.isNumber() )
        {
            return OpenSearchClient.mapper().getNodeFactory().numberNode( value.doubleValue() );
        }
        if ( value.isBoolean() )
        {
            return value;
        }
        return OpenSearchClient.mapper().getNodeFactory().textNode( normalize( value.asText() ) );
    }

    private static long instantMillis( JsonNode value )
    {
        if ( value.isNumber() )
        {
            return value.asLong();
        }
        try
        {
            return java.time.Instant.parse( value.asText() ).toEpochMilli();
        }
        catch ( java.time.format.DateTimeParseException e )
        {
            throw new UnsupportedQueryException( "Value [" + value.asText() + "] is not an ISO instant" );
        }
    }

    /** XP's {@code IndexValueNormalizer}: the text variant is a lowercase-normalized keyword. */
    public static String normalize( String value )
    {
        return value == null ? null : value.trim().toLowerCase( Locale.ROOT );
    }

    // --- json helpers ----------------------------------------------------------------

    /** A DSL slot accepts either one expression or an array of them; both arrive here as a list. */
    private static List<JsonNode> elements( JsonNode node )
    {
        if ( !node.isArray() )
        {
            return List.of( node );
        }
        java.util.List<JsonNode> list = new java.util.ArrayList<>( node.size() );
        node.forEach( list::add );
        return list;
    }

    private static ObjectNode object()
    {
        return OpenSearchClient.mapper().createObjectNode();
    }

    private static ObjectNode wrap( String name, JsonNode body )
    {
        return object().set( name, body );
    }

    private static ObjectNode boosted( ObjectNode body, JsonNode expression )
    {
        applyBoost( body, expression );
        return body;
    }

    private static void applyBoost( ObjectNode body, JsonNode expression )
    {
        JsonNode boost = expression == null ? null : expression.get( "boost" );
        if ( boost != null && boost.isNumber() )
        {
            body.put( "boost", boost.doubleValue() );
        }
    }

    private static String text( JsonNode expression, String name )
    {
        JsonNode node = expression == null ? null : expression.get( name );
        return node == null || node.isNull() || !node.isTextual() ? null : node.asText();
    }

    private static JsonNode require( JsonNode expression, String name, String queryType )
    {
        JsonNode node = expression == null ? null : expression.get( name );
        if ( node == null )
        {
            throw new UnsupportedQueryException( "'" + name + "' is required by the '" + queryType + "' query" );
        }
        return node;
    }
}
