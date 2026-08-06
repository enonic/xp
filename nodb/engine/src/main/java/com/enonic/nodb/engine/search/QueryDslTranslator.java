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
 * <p><b>Gate B covers the structured families only</b> — {@code term}, {@code in},
 * {@code like}, {@code range}, {@code exists}, {@code boolean}, {@code matchAll}, plus the
 * filter vocabulary ({@code values}, {@code ids}, {@code exists}, {@code range},
 * {@code boolean}) and field sorts. Everything else throws
 * {@link UnsupportedQueryException}: text/geo (Gate D) and aggregations (Gate E) are the next
 * batches, and a half-translated construct that silently returns the wrong hits is the one
 * failure mode this port cannot afford.
 *
 * <p><b>Three typing rules coexist and are reproduced verbatim from XP.</b> An explicit
 * {@code type: "dateTime"} reaches {@code ._datetime}; a JSON number reaches {@code ._number};
 * everything else lands on the base text variant. String values bound for that variant are
 * normalized ({@code trim().toLowerCase()}) exactly as XP normalizes them at index time —
 * without it, every mixed-case term silently matches nothing.
 */
public final class QueryDslTranslator
{
    private static final String TYPE_DATETIME = "dateTime";

    private QueryDslTranslator()
    {
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

    public static ObjectNode translateQuery( JsonNode dsl )
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

    private static ObjectNode term( JsonNode expression )
    {
        String type = text( expression, "type" );
        JsonNode value = require( expression, "value", "term" );

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
    private static ObjectNode in( JsonNode expression )
    {
        String type = text( expression, "type" );
        JsonNode values = require( expression, "values", "in" );

        ArrayNode should = OpenSearchClient.mapper().createArrayNode();
        for ( JsonNode value : values.isArray() ? values : OpenSearchClient.mapper().createArrayNode().add( values ) )
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

    private static ObjectNode like( JsonNode expression )
    {
        JsonNode value = require( expression, "value", "like" );

        ObjectNode body = object();
        body.put( "value", normalize( value.asText() ) );
        applyBoost( body, expression );

        return wrap( "wildcard", object().set( fieldName( expression, null, null ), body ) );
    }

    private static ObjectNode range( JsonNode expression )
    {
        String type = text( expression, "type" );

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

    private static ObjectNode exists( JsonNode expression )
    {
        ObjectNode body = object();
        body.put( "field", fieldName( expression, null, null ) );
        return wrap( "exists", body );
    }

    /**
     * Nesting is preserved exactly as it arrives: a pairwise-nested {@code must} is NOT
     * flattened into a flat clause list. The two are set-equivalent but not score-equivalent.
     */
    private static ObjectNode bool( JsonNode expression )
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
            for ( JsonNode element : operand.isArray() ? operand : OpenSearchClient.mapper().createArrayNode().add( operand ) )
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

    public static ObjectNode translateFilter( JsonNode dsl )
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

    private static ObjectNode valuesFilter( JsonNode expression )
    {
        String type = text( expression, "type" );
        JsonNode values = require( expression, "values", "values" );

        ArrayNode terms = OpenSearchClient.mapper().createArrayNode();
        String field = null;
        for ( JsonNode value : values.isArray() ? values : OpenSearchClient.mapper().createArrayNode().add( values ) )
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
     * The {@code ids} filter targets the branch/id keyword fields verbatim, without the text
     * postfix or normalization: it is how case-sensitive identifiers (branch names above all)
     * are matched, which is exactly why XP used an id filter rather than a value filter there.
     */
    private static ObjectNode idsFilter( JsonNode expression )
    {
        JsonNode values = require( expression, "values", "ids" );

        ArrayNode terms = OpenSearchClient.mapper().createArrayNode();
        for ( JsonNode value : values.isArray() ? values : OpenSearchClient.mapper().createArrayNode().add( values ) )
        {
            terms.add( value.asText() );
        }
        return wrap( "terms", object().set( IndexFields.physicalName( logicalField( expression ) ), terms ) );
    }

    private static ObjectNode booleanFilter( JsonNode expression )
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
            for ( JsonNode element : operand.isArray() ? operand : OpenSearchClient.mapper().createArrayNode().add( operand ) )
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

    // --- sorts -----------------------------------------------------------------------

    /**
     * {@code unmappedType} is {@code keyword}, not {@code long}. Every {@code _orderby*} field
     * holds a lexicographically sortable ASCII string, so XP's hardcoded {@code long} was a
     * latent bug: harmless while the field is mapped everywhere, fatal on a multi-index sort
     * where one repo has never seen the property. The built-in pseudo-fields get no
     * {@code unmapped_type} at all.
     */
    public static ObjectNode translateSort( JsonNode dsl )
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
        return "_score".equals( field ) || "_id".equals( field ) || "_doc".equals( field );
    }

    // --- field names and values ------------------------------------------------------

    private static String fieldName( JsonNode expression, String type, JsonNode value )
    {
        String field = logicalField( expression );

        if ( type == null || type.isBlank() )
        {
            return IndexFields.physicalName( value != null && value.isNumber() ? field + "._number" : field );
        }
        if ( TYPE_DATETIME.equals( type ) )
        {
            return IndexFields.physicalName( field + "._datetime" );
        }
        throw new UnsupportedQueryException( "There is no [" + type + "] dsl expression type in this backend" );
    }

    private static String logicalField( JsonNode expression )
    {
        String field = text( expression, "field" );
        if ( field == null || field.isBlank() )
        {
            throw new UnsupportedQueryException( "'field' cannot be empty" );
        }
        return field;
    }

    /**
     * Dates ride as epoch millis, which is what the mapping's first date format expects and
     * what the projection writes; numbers stay numbers; strings are normalized for the text
     * variant. The wire's ISO-instant form is parsed here so the engine never has to guess a
     * format.
     */
    private static JsonNode resolveValue( JsonNode value, String type )
    {
        if ( TYPE_DATETIME.equals( type ) )
        {
            return OpenSearchClient.mapper().getNodeFactory().numberNode( instantMillis( value ) );
        }
        if ( value.isNumber() || value.isBoolean() )
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
