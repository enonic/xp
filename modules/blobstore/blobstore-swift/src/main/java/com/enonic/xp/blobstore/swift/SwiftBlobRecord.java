package com.enonic.xp.blobstore.swift;

import java.io.IOException;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.util.Exceptions;

public class SwiftBlobRecord
    implements BlobRecord
{
    private ByteSource source;

    private BlobKey key;

    public SwiftBlobRecord( final ByteSource source, final BlobKey key )
    {
        this.source = source;
        this.key = key;
    }

    @Override
    public long getLength()
    {
        try
        {
            return this.source.size();
        }
        catch ( IOException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    @Override
    public ByteSource getBytes()
    {
        return this.source;
    }

    @Override
    public BlobKey getKey()
    {
        return key;
    }
}
