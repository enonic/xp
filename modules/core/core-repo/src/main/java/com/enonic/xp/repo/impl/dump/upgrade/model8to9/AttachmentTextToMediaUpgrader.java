package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.upgrade.NodeVersionUpgrader;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.schema.content.ContentTypeName;

public class AttachmentTextToMediaUpgrader
    implements NodeVersionUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( AttachmentTextToMediaUpgrader.class );

    private static final String LEGACY_ATTACHMENT_TEXT = "text";

    @Override
    public NodeStoreVersion upgradeNodeVersion( final RepositoryId repositoryId, final NodeStoreVersion nodeVersion )
    {
        if ( !repositoryId.toString().startsWith( ProjectConstants.PROJECT_REPO_ID_PREFIX ) ||
            !ContentConstants.CONTENT_NODE_COLLECTION.equals( nodeVersion.nodeType() ) )
        {
            return null;
        }

        final PropertyTree data = nodeVersion.data();

        final String typeString = data.getString( ContentPropertyNames.TYPE );
        if ( typeString == null || !ContentTypeName.from( typeString ).isTextualMedia() )
        {
            return null;
        }

        final Iterable<PropertySet> attachments = data.getSets( ContentPropertyNames.ATTACHMENT );
        if ( attachments == null )
        {
            return null;
        }

        boolean modified = false;
        String text = null;

        for ( PropertySet attachmentSet : attachments )
        {
            final String value = attachmentSet.getString( LEGACY_ATTACHMENT_TEXT );
            if ( value != null )
            {
                if ( text == null )
                {
                    text = value;
                }
                attachmentSet.removeProperties( LEGACY_ATTACHMENT_TEXT );
                modified = true;
            }
        }

        if ( !modified )
        {
            return null;
        }

        if ( text != null )
        {
            final PropertySet mediaSet = data.getSet( ContentPropertyNames.MEDIA );
            if ( mediaSet != null && mediaSet.getString( ContentPropertyNames.MEDIA_TEXT ) == null )
            {
                mediaSet.addString( ContentPropertyNames.MEDIA_TEXT, text );
            }
        }

        LOG.info( "Moved attachment text to media for node [{}] in repository [{}]", nodeVersion.id(), repositoryId );
        return nodeVersion;
    }
}
