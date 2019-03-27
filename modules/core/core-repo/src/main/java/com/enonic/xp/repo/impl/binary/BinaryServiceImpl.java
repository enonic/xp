package com.enonic.xp.repo.impl.binary;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repository.RepositoryExeption;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;

@Component
public class BinaryServiceImpl
    implements BinaryService
{
    private BlobStore blobStore;

    @Override
    public AttachedBinary store( final RepositoryId repositoryId, final BinaryAttachment binaryAttachment )
    {
        final Segment segment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.BINARY_SEGMENT_LEVEL );
        final BlobRecord blob = this.blobStore.addRecord( segment, binaryAttachment.getByteSource() );
        return new AttachedBinary( binaryAttachment.getReference(), blob.getKey().toString() );
    }

    @Override
    public ByteSource get( final RepositoryId repositoryId, final AttachedBinary attachedBinary )
    {
        final Segment segment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.BINARY_SEGMENT_LEVEL );
        final BlobRecord record = blobStore.getRecord( segment, BlobKey.from( attachedBinary.getBlobKey() ) );

        if ( record == null )
        {
            throw new RepositoryExeption( "Cannot load binary with key [" + attachedBinary.getBlobKey() + "], not found" );
        }

        return record.getBytes();
    }

    @Reference
    public void setBlobStore( final BlobStore blobStore )
    {
        this.blobStore = blobStore;
    }
}
