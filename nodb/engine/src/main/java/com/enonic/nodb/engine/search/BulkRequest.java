package com.enonic.nodb.engine.search;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * NDJSON accumulator for {@code _bulk}. Assembled as text rather than through a JSON library
 * because that is literally what the format is: newline-delimited action/document pairs with
 * no enclosing array, and a trailing newline that the engine rejects the request without.
 */
public final class BulkRequest
{
    private final StringBuilder body = new StringBuilder();

    private int operations;

    /** Full-document upsert. Bulk {@code index} replaces, which is what an XP node write is. */
    public BulkRequest index( String indexName, String documentId, ObjectNode document )
    {
        body.append( "{\"index\":{\"_index\":" )
            .append( jsonString( indexName ) )
            .append( ",\"_id\":" )
            .append( jsonString( documentId ) )
            .append( "}}\n" )
            .append( document.toString() )
            .append( '\n' );
        operations++;
        return this;
    }

    /**
     * {@code delete} of a document that is not there answers {@code not_found} in the item,
     * not an error, so a replayed DELETE outbox row is harmless — which matters because the
     * outbox is at-least-once by design.
     */
    public BulkRequest delete( String indexName, String documentId )
    {
        body.append( "{\"delete\":{\"_index\":" )
            .append( jsonString( indexName ) )
            .append( ",\"_id\":" )
            .append( jsonString( documentId ) )
            .append( "}}\n" );
        operations++;
        return this;
    }

    public boolean isEmpty()
    {
        return operations == 0;
    }

    public int size()
    {
        return operations;
    }

    public String toNdjson()
    {
        return body.toString();
    }

    private static String jsonString( String value )
    {
        return OpenSearchClient.mapper().getNodeFactory().textNode( value ).toString();
    }
}
