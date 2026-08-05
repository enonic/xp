package com.enonic.nodb.engine.search;

/**
 * {@code refresh(SEARCH)} gave up waiting for the indexer's checkpoint to reach the caller's seq.
 *
 * <p>A distinct type rather than a generic timeout because it means one specific thing: the
 * read-your-writes contract (DESIGN §3.3) could not be honoured within the caller's budget. It maps
 * to {@code DEADLINE_EXCEEDED} at the gRPC boundary, which is the honest answer — the write IS
 * committed and durable, it is only not yet searchable, so a retry is meaningful and a rollback
 * would be wrong.
 */
public class IndexRefreshTimeoutException
    extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public IndexRefreshTimeoutException( String message )
    {
        super( message );
    }
}
