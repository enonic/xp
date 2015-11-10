package com.enonic.xp.repo.impl.blob.cache;

import com.google.common.cache.Weigher;

import com.enonic.xp.repo.impl.blob.BlobKey;
import com.enonic.xp.repo.impl.blob.BlobRecord;

final class BlobRecordWeigher
    implements Weigher<BlobKey, BlobRecord>
{
    @Override
    public int weigh( final BlobKey key, final BlobRecord record )
    {
        return (int) record.getLength();
    }
}
