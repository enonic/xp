package com.enonic.xp.awss3;

import java.io.IOException;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.util.Exceptions;

public class AwsS3BlobRecord
    implements BlobRecord
{
    private ByteSource source;

    private BlobKey key;

    public AwsS3BlobRecord( final ByteSource source, final BlobKey key )
    {
        this.source = source;
        this.key = key;
    }

    @Override
    public BlobKey getKey()
    {
        return this.key;
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
}
