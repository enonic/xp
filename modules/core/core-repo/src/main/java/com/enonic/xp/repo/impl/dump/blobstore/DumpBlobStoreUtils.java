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
        if ( id.startsWith( "sha256:" ) )
        {
            return parent.resolve( reference.getSegment().getLevel( RepositorySegmentUtils.BLOB_TYPE_LEVEL ).getValue() )
                .resolve( "sha256" )
                .resolve( id.substring( 7, 9 ) )
                .resolve( id.substring( 9, 11 ) )
                .resolve( id.substring( 11, 13 ) )
                .resolve( id.substring( 13 ) );
        }
        else
        {
            return parent.resolve( reference.getSegment().getLevel( RepositorySegmentUtils.BLOB_TYPE_LEVEL ).getValue() )
                .resolve( id.substring( 0, 2 ) )
                .resolve( id.substring( 2, 4 ) )
                .resolve( id.substring( 4, 6 ) )
                .resolve( id );
        }
    }
}
