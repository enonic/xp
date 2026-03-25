package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.core.internal.security.MessageDigests;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.reader.BlobStoreAccess;
import com.enonic.xp.repo.impl.dump.upgrade.NodeVersionUpgrader;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.util.BinaryReference;

public class AttachmentSha512Upgrader
    implements NodeVersionUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( AttachmentSha512Upgrader.class );

    private final BlobStoreAccess blobStoreAccess;

    public AttachmentSha512Upgrader( final BlobStoreAccess blobStoreAccess )
    {
        this.blobStoreAccess = blobStoreAccess;
    }

    @Override
    public NodeStoreVersion upgradeNodeVersion( final RepositoryId repositoryId, final NodeStoreVersion nodeVersion )
    {
        if ( repositoryId.toString().startsWith( ProjectConstants.PROJECT_REPO_ID_PREFIX ) &&
            ContentConstants.CONTENT_NODE_COLLECTION.equals( nodeVersion.nodeType() ) )
        {
            return upgradeContentAttachments( repositoryId, nodeVersion );
        }

        if ( SystemConstants.SYSTEM_REPO_ID.equals( repositoryId ) )
        {
            return upgradeProjectIcon( repositoryId, nodeVersion );
        }

        return null;
    }

    private NodeStoreVersion upgradeContentAttachments( final RepositoryId repositoryId, final NodeStoreVersion nodeVersion )
    {
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
            if ( computeAndSetSha512( attachmentSet, nodeVersion, binarySegment ) )
            {
                modified = true;
            }
        }

        if ( modified )
        {
            LOG.info( "Added sha512 to attachments for node [{}] in repository [{}]", nodeVersion.id(), repositoryId );
        }
        return modified ? nodeVersion : null;
    }

    private NodeStoreVersion upgradeProjectIcon( final RepositoryId repositoryId, final NodeStoreVersion nodeVersion )
    {
        final PropertySet projectData = nodeVersion.data().getSet( ProjectConstants.PROJECT_DATA_SET_NAME );
        if ( projectData == null )
        {
            return null;
        }

        final PropertySet iconData = projectData.getSet( ProjectConstants.PROJECT_ICON_PROPERTY );
        if ( iconData == null )
        {
            return null;
        }

        final Segment binarySegment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.BINARY_SEGMENT_LEVEL );

        if ( computeAndSetSha512( iconData, nodeVersion, binarySegment ) )
        {
            LOG.info( "Added sha512 to project icon for node [{}] in repository [{}]", nodeVersion.id(), repositoryId );
            return nodeVersion;
        }
        return null;
    }

    private boolean computeAndSetSha512( final PropertySet attachmentSet, final NodeStoreVersion nodeVersion, final Segment binarySegment )
    {
        if ( attachmentSet.getString( ContentPropertyNames.ATTACHMENT_SHA512 ) != null )
        {
            return false;
        }

        final BinaryReference binaryRef = attachmentSet.getBinaryReference( ContentPropertyNames.ATTACHMENT_BINARY_REF );
        if ( binaryRef == null )
        {
            return false;
        }

        final AttachedBinary attachedBinary = nodeVersion.attachedBinaries().getByBinaryReference( binaryRef );
        if ( attachedBinary == null )
        {
            return false;
        }

        final String sha512 = computeSha512( binarySegment, BlobKey.from( attachedBinary.getBlobKey() ) );
        if ( sha512 != null )
        {
            attachmentSet.addString( ContentPropertyNames.ATTACHMENT_SHA512, sha512 );
            return true;
        }
        return false;
    }

    private String computeSha512( final Segment binarySegment, final BlobKey blobKey )
    {
        try
        {
            final BlobRecord record = blobStoreAccess.getRecord( binarySegment, blobKey );
            return MessageDigests.formatHex( MessageDigests.updateWithStream( MessageDigests.sha256(), record.getBytes()::openStream ) );
        }
        catch ( IOException e )
        {
            LOG.warn( "Failed to compute sha512 for blob [{}]", blobKey, e );
            return null;
        }
    }
}
