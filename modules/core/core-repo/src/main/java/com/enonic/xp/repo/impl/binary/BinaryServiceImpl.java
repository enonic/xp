package com.enonic.xp.repo.impl.binary;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.repo.impl.node.NodeConstants;

@Component
public class BinaryServiceImpl
    implements BinaryService
{
    private BlobStore blobStore;

    @Override
    public AttachedBinary store( final BinaryAttachment binaryAttachment )
    {
        final BlobRecord blob = this.blobStore.addRecord( NodeConstants.BINARY_SEGMENT, binaryAttachment.getByteSource() );
        return new AttachedBinary( binaryAttachment.getReference(), blob.getKey().toString() );
    }

    @Override
    public ByteSource get( final AttachedBinary attachedBinary )
    {
        return blobStore.getRecord( NodeConstants.BINARY_SEGMENT, new BlobKey( attachedBinary.getBlobKey() ) ).getBytes();
    }

    @Reference
    public void setBlobStore( final BlobStore blobStore )
    {
        this.blobStore = blobStore;
    }
}
