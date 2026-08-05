package com.enonic.nodb.engine.search;

/**
 * Any failure talking to OpenSearch: transport, non-2xx response, or a partial bulk failure.
 * Carries the HTTP status (0 for transport failures) and the response body, because
 * OpenSearch's mapping/analysis errors are only meaningful in full — {@code
 * mapper_parsing_exception: No handler for type [string]} and {@code can't merge a non object
 * mapping [data.x] with an object mapping} are the two the whole mapping port exists to
 * avoid, and truncating them to "index failed" would have cost Gate 0 its blockers.
 */
public class OpenSearchException
    extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    private final int status;

    private final String body;

    public OpenSearchException( String message, int status, String body )
    {
        super( status > 0 ? message + " (HTTP " + status + "): " + body : message + ": " + body );
        this.status = status;
        this.body = body;
    }

    public OpenSearchException( String message, Throwable cause )
    {
        super( message, cause );
        this.status = 0;
        this.body = "";
    }

    public int status()
    {
        return status;
    }

    public String body()
    {
        return body;
    }
}
