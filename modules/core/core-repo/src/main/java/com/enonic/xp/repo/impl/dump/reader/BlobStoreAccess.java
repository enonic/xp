package com.enonic.xp.repo.impl.dump.reader;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;

public interface BlobStoreAccess
{
    BlobRecord getRecord( Segment segment, BlobKey key );
}
