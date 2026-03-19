package com.enonic.xp.repo.impl.dump.upgrade.v8;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.blobstore.FileDumpBlobRecord;
import com.enonic.xp.repo.impl.dump.reader.FileDumpReaderV7;
import com.enonic.xp.repo.impl.dump.upgrade.NodeVersionUpgrader;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.util.BinaryReference;

public class AttachmentSha512Upgrader
    implements NodeVersionUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( AttachmentSha512Upgrader.class );

    private final FileDumpReaderV7 dumpReader;

    public AttachmentSha512Upgrader( final FileDumpReaderV7 dumpReader )
    {
        this.dumpReader = dumpReader;
    }

    @Override
    public NodeStoreVersion upgradeNodeVersion( final RepositoryId repositoryId, final NodeStoreVersion nodeVersion )
    {
        if ( !repositoryId.toString().startsWith( ProjectConstants.PROJECT_REPO_ID_PREFIX ) )
        {
            return null;
        }

        if ( !ContentConstants.CONTENT_NODE_COLLECTION.equals( nodeVersion.nodeType() ) )
        {
            return null;
        }

        final PropertyTree data = nodeVersion.data();
        final Iterable<PropertySet> attachments = data.getSets( ContentPropertyNames.ATTACHMENT );
        if ( attachments == null )
        {
            return null;
        }

        final Segment binarySegment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.BINARY_SEGMENT_LEVEL );
        boolean modified = false;

        for ( PropertySet attachmentSet : attachments )
        {
            if ( attachmentSet.getString( ContentPropertyNames.ATTACHMENT_SHA512 ) != null )
            {
                continue;
            }

            final BinaryReference binaryRef = attachmentSet.getBinaryReference( ContentPropertyNames.ATTACHMENT_BINARY_REF );
            if ( binaryRef == null )
            {
                continue;
            }

            final AttachedBinary attachedBinary = nodeVersion.attachedBinaries().getByBinaryReference( binaryRef );
            if ( attachedBinary == null )
            {
                continue;
            }

            final String sha512 = computeSha512( binarySegment, attachedBinary.getBlobKey() );
            if ( sha512 != null )
            {
                attachmentSet.addString( ContentPropertyNames.ATTACHMENT_SHA512, sha512 );
                modified = true;
            }
        }

        if ( modified )
        {
            LOG.info( "Added sha512 to attachments for node [{}] in repository [{}]", nodeVersion.id(), repositoryId );
        }
        return modified ? nodeVersion : null;
    }

    private String computeSha512( final Segment binarySegment, final String blobKey )
    {
        try
        {
            final FileDumpBlobRecord record = dumpReader.getRecord( binarySegment, BlobKey.from( blobKey ) );
            try (InputStream is = record.getBytes().openStream(); DigestInputStream dis = new DigestInputStream( is,
                                                                                                                 MessageDigest.getInstance(
                                                                                                                     "SHA-512" ) ))
            {
                ByteStreams.exhaust( dis );
                return HexFormat.of().formatHex( dis.getMessageDigest().digest() );
            }
        }
        catch ( IOException | NoSuchAlgorithmException e )
        {
            LOG.warn( "Failed to compute sha512 for blob [{}]", blobKey, e );
            return null;
        }
    }
}
