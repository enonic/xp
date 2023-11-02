package com.enonic.xp.repo.impl.dump.blobstore;

import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repository.RepositorySegmentUtils;

public final class DumpBlobStoreUtils
{
    private DumpBlobStoreUtils()
    {
    }

    public static PathRef getBlobPathRef( final PathRef parent, final BlobReference reference )
    {
        final String id = reference.getKey().toString();
        return parent.resolve( reference.getSegment().getLevel( RepositorySegmentUtils.BLOB_TYPE_LEVEL ).getValue() )
            .resolve( id.substring( 0, 2 ) )
            .resolve( id.substring( 2, 4 ) )
            .resolve( id.substring( 4, 6 ) )
            .resolve( id );
    }
}
