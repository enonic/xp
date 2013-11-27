/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.core.blobstore.memory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.core.blobstore.BlobKeyCreator;
import com.enonic.wem.core.blobstore.BlobRecord;


final public class MemoryBlobRecord
    extends BlobRecord
{
    private final byte[] data;

    public MemoryBlobRecord( final BlobKey key, final byte[] data )
    {
        super( key );
        this.data = data;
    }

    public MemoryBlobRecord( final byte[] data )
    {
        this( new BlobKey( BlobKeyCreator.createKey( data ).toString() ), data );
    }

    public File getAsFile()
    {
        return null;
    }

    public long getLength()
    {
        return this.data.length;
    }

    public InputStream getStream()
    {
        return new ByteArrayInputStream( this.data );
    }
}
