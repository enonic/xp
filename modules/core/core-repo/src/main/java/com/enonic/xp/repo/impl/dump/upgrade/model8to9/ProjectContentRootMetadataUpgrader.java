package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.attachment.AttachmentNames;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.BinaryReference;


/**
 * For a project repo {@code /content} node, populates its {@code displayName}, project
 * {@code description}, {@code parents}, and project icon as a {@code _thumbnail} attachment from
 * the project's repository config node in the system repo. The source data is collected up-front
 * by {@link DumpUpgrader8to9} during a pre-pass over the system repo.
 */
public class ProjectContentRootMetadataUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( ProjectContentRootMetadataUpgrader.class );

    private final Map<RepositoryId, ProjectMetadata> metadata;

    public ProjectContentRootMetadataUpgrader( final Map<RepositoryId, ProjectMetadata> metadata )
    {
        this.metadata = metadata;
    }

    public NodeStoreVersion upgrade( final RepositoryId repositoryId, final NodePath nodePath, final NodeStoreVersion nodeVersion )
    {
        if ( !repositoryId.toString().startsWith( ProjectConstants.PROJECT_REPO_ID_PREFIX ) )
        {
            return null;
        }
        if ( nodePath == null || !ContentConstants.CONTENT_ROOT_PATH.equals( nodePath ) )
        {
            return null;
        }

        final ProjectMetadata pm = metadata.get( repositoryId );
        if ( pm == null )
        {
            return null;
        }

        final PropertyTree data = nodeVersion.data();
        boolean modified = false;

        if ( pm.displayName() != null )
        {
            data.setString( ContentPropertyNames.DISPLAY_NAME, pm.displayName() );
            modified = true;
        }

        if ( pm.description() != null )
        {
            PropertySet contentData = data.getSet( ContentPropertyNames.DATA );
            if ( contentData == null )
            {
                contentData = data.addSet( ContentPropertyNames.DATA );
            }
            contentData.setString( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY, pm.description() );
            modified = true;
        }

        if ( !pm.parents().isEmpty() )
        {
            PropertySet contentData = data.getSet( ContentPropertyNames.DATA );
            if ( contentData == null )
            {
                contentData = data.addSet( ContentPropertyNames.DATA );
            }
            contentData.removeProperties( ProjectConstants.PROJECT_PARENTS_PROPERTY );
            contentData.addStrings( ProjectConstants.PROJECT_PARENTS_PROPERTY, pm.parents() );
            modified = true;
        }

        NodeStoreVersion result = nodeVersion;
        if ( pm.icon() != null )
        {
            applyThumbnailAttachment( data, pm.icon() );
            result = NodeStoreVersion.create( nodeVersion )
                .data( data )
                .attachedBinaries( appendThumbnailBinary( nodeVersion.attachedBinaries(), pm.icon() ) )
                .build();
            modified = true;
        }

        if ( modified )
        {
            LOG.info( "Populated /content node metadata for project repo [{}]", repositoryId );
        }
        return modified ? result : null;
    }

    private static void applyThumbnailAttachment( final PropertyTree data, final IconBinary icon )
    {
        data.removeProperties( ContentPropertyNames.ATTACHMENT );
        final PropertySet attachmentSet = data.addSet( ContentPropertyNames.ATTACHMENT );
        attachmentSet.addString( ContentPropertyNames.ATTACHMENT_NAME, AttachmentNames.THUMBNAIL );
        attachmentSet.addBinaryReference( ContentPropertyNames.ATTACHMENT_BINARY_REF, BinaryReference.from( AttachmentNames.THUMBNAIL ) );
        attachmentSet.addString( ContentPropertyNames.ATTACHMENT_MIMETYPE, icon.mimeType() );
        attachmentSet.addLong( ContentPropertyNames.ATTACHMENT_SIZE, icon.size() );
    }

    private static AttachedBinaries appendThumbnailBinary( final AttachedBinaries existing, final IconBinary icon )
    {
        return AttachedBinaries.create().addAll( existing )
            .add( new AttachedBinary( BinaryReference.from( AttachmentNames.THUMBNAIL ), icon.blobKey() ) )
            .build();
    }

    public record ProjectMetadata(@Nullable String displayName, @Nullable String description, List<String> parents,
                                  @Nullable IconBinary icon)
    {
        public ProjectMetadata(@Nullable String displayName, @Nullable String description)
        {
            this( displayName, description, List.of(), null );
        }
    }

    /**
     * Carries the legacy project-icon attachment metadata harvested from v8 system-repo project config.
     * {@code blobKey} is the project repo binary segment key after the icon blob has been copied from
     * the system repo binary segment by {@link DumpUpgrader8to9}.
     */
    public record IconBinary(String mimeType, long size, String blobKey)
    {
    }
}
