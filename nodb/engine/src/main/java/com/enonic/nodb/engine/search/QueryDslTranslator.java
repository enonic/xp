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
 * {@code range}, {@code boolean}) and field sorts. Everything else throws
 * {@link UnsupportedQueryException}: text/geo (Gate D) and aggregations/suggest/highlight
 * (Gate E) are the next batches, and a half-translated construct that silently returns the
 * wrong hits is the one failure mode this port cannot afford.
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
 * everything else resolves to {@code ._orderby}. The locale-qualified
 * {@code ._orderby_<loc>} form is Gate D and fails loudly here.</li>
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
            case "fulltext", "ngram", "stemmed", "pathMatch" -> throw new UnsupportedQueryException(
                "Query type '" + name + "' is not translated yet (text and geo families are the next translation batch)" );
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

        if ( type != null )
        {
            throw new UnsupportedQueryException(
                "Sort type '" + type + "' is not translated yet (geo-distance sorting is the next translation batch)" );
        }
        if ( text( dsl, "language" ) != null )
        {
            throw new UnsupportedQueryException(
                "Language-aware (COLLATE) sorting is not translated yet; it arrives with the text family" );
        }

        ObjectNode body = object();
        body.put( "order", direction == null ? "asc" : direction.toLowerCase( Locale.ROOT ) );

        if ( isPseudoField( field ) )
        {
            return object().set( field, body );
        }

        body.put( "unmapped_type", "keyword" );
        return object().set( IndexFields.physicalName( field + "._orderby" ), body );
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
