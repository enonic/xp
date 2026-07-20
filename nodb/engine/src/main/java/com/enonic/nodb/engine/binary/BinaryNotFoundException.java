package com.enonic.nodb.engine.binary;

import java.io.IOException;

/**
 * Thrown by {@link BinaryStore#get} when no object matches the given hash under the
 * caller's tenant prefix. A dedicated checked type (rather than translating {@code
 * NoSuchKeyException} ad hoc at every call site) so the gRPC layer can map this outcome to
 * {@code Status.NOT_FOUND} structurally — same convention as {@code
 * com.enonic.nodb.engine.store.UnknownRepoException}.
 */
public final class BinaryNotFoundException
    extends IOException
{
    public BinaryNotFoundException( String hash )
    {
        super( "Unknown binary hash: " + hash );
    }
}
