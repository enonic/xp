package com.enonic.nodb.engine.search;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * One XP-shipped index document, in CANONICAL form: the node id, XP's optional per-document
 * analyzer, and a field map in XP's own vocabulary with typed, multi-valued values.
 *
 * <p>"Canonical" is the load-bearing word. This is what {@code search_document} stores and what
 * a rebuild replays — deliberately the INPUT to {@link IndexDocumentProjection}, never its
 * output. The projection is versioned and will change (the ACL admin-key injection alone
 * guarantees at least one bump), and a projection bump can only be replayed from rows that
 * predate the projection.
 *
 * <p>Values keep their type because the mapping depends on it: {@code *._number} is a
 * {@code double}, {@code *._datetime} a {@code date}, and losing the distinction would either
 * fail the mapping or coerce a timestamp into a keyword. Instants ride as epoch millis, which is
 * exactly the precision an ES 2.4 {@code date} stored.
 */
public record SearchDocument(String nodeId, String analyzer, Map<String, List<Value>> fields)
{
    public SearchDocument
    {
        if ( nodeId == null || nodeId.isEmpty() )
        {
            throw new IllegalArgumentException( "Search document node id must not be empty" );
        }
        fields = Map.copyOf( fields );
    }

    /** A single typed field value. */
    public sealed interface Value
    {
        record Text(String value) implements Value
        {
        }

        record Number(double value) implements Value
        {
        }

        record Integer(long value) implements Value
        {
        }

        record Bool(boolean value) implements Value
        {
        }

        /** Epoch millis; rendered as a JSON number and parsed by the mapping's {@code epoch_millis} format. */
        record Timestamp(long epochMillis) implements Value
        {
        }
    }

    // ------------------------------------------------------------------- jsonb round trip

    /**
     * The {@code search_document.doc} JSONB shape: {@code {"<field>": [{"t":"s","v":...}, ...]}}.
     * Type-tagged per value, mirroring the Phase-3 payload format's own type-tagged JSON
     * convention — the alternative (inferring the type back from the JSON literal) would turn a
     * long that happens to be integral into a double on every round trip and quietly break
     * {@code _orderby} parity.
     */
    public ObjectNode toJson()
    {
        ObjectNode root = OpenSearchClient.mapper().createObjectNode();
        fields.forEach( ( name, values ) -> {
            ArrayNode array = root.putArray( name );
            for ( Value value : values )
            {
                ObjectNode entry = array.addObject();
                switch ( value )
                {
                    case Value.Text text ->
                    {
                        entry.put( "t", "s" );
                        entry.put( "v", text.value() );
                    }
                    case Value.Number number ->
                    {
                        entry.put( "t", "d" );
                        entry.put( "v", number.value() );
                    }
                    case Value.Integer integer ->
                    {
                        entry.put( "t", "l" );
                        entry.put( "v", integer.value() );
                    }
                    case Value.Bool bool ->
                    {
                        entry.put( "t", "b" );
                        entry.put( "v", bool.value() );
                    }
                    case Value.Timestamp timestamp ->
                    {
                        entry.put( "t", "ts" );
                        entry.put( "v", timestamp.epochMillis() );
                    }
                }
            }
        } );
        return root;
    }

    public static SearchDocument fromJson( String nodeId, String analyzer, JsonNode json )
    {
        Map<String, List<Value>> fields = new LinkedHashMap<>();
        json.properties().forEach( entry -> {
            List<Value> values = new java.util.ArrayList<>();
            for ( JsonNode value : entry.getValue() )
            {
                String type = value.path( "t" ).asText();
                JsonNode raw = value.path( "v" );
                values.add( switch ( type )
                            {
                                case "s" -> new Value.Text( raw.asText() );
                                case "d" -> new Value.Number( raw.asDouble() );
                                case "l" -> new Value.Integer( raw.asLong() );
                                case "b" -> new Value.Bool( raw.asBoolean() );
                                case "ts" -> new Value.Timestamp( raw.asLong() );
                                default -> throw new IllegalArgumentException( "Unknown search-document value type '" + type + "'" );
                            } );
            }
            fields.put( entry.getKey(), List.copyOf( values ) );
        } );
        return new SearchDocument( nodeId, analyzer, fields );
    }
}
