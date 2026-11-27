package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.upgrade.NodeVersionUpgrader;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.schema.content.ContentTypeName;

/**
 * Moves the extracted text of textual media from {@code attachment.text} to {@code data.media.text} and replaces the
 * {@code attachment.text} index config with the fulltext {@code data.media.text} index config that XP 8 writes for textual media.
 */
public class AttachmentTextToMediaUpgrader
    implements NodeVersionUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( AttachmentTextToMediaUpgrader.class );

    private static final String LEGACY_ATTACHMENT_TEXT = "text";

    private static final IndexPath LEGACY_ATTACHMENT_TEXT_PATH = IndexPath.from( "attachment.text" );

    private static final IndexPath MEDIA_TEXT_PATH = IndexPath.from( ContentPropertyNames.MEDIA_TEXT_COMPONENT );

    private static final IndexConfig MEDIA_TEXT_INDEX_CONFIG = IndexConfig.create()
        .enabled( true )
        .fulltext( true )
        .includeInAllText( true )
        .nGram( true )
        .decideByType( false )
        .build();

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

        String text = null;
        String textAttachmentName = null;

        for ( PropertySet attachmentSet : attachments )
        {
            final String value = attachmentSet.getString( LEGACY_ATTACHMENT_TEXT );
            if ( value != null )
            {
                if ( text == null )
                {
                    text = value;
                    textAttachmentName = attachmentSet.getString( ContentPropertyNames.ATTACHMENT_NAME );
                }
                attachmentSet.removeProperties( LEGACY_ATTACHMENT_TEXT );
            }
        }

        if ( text == null )
        {
            return null;
        }

        final PropertySet mediaSet = resolveMediaSet( data, textAttachmentName );
        if ( mediaSet == null )
        {
            LOG.warn( "Removed attachment text without media data for node [{}] in repository [{}]", nodeVersion.id(), repositoryId );
            return nodeVersion;
        }

        if ( mediaSet.getString( ContentPropertyNames.MEDIA_TEXT ) == null )
        {
            mediaSet.addString( ContentPropertyNames.MEDIA_TEXT, text );
        }

        LOG.info( "Moved attachment text to media for node [{}] in repository [{}]", nodeVersion.id(), repositoryId );

        return NodeStoreVersion.create( nodeVersion ).indexConfigDocument( upgradeIndexConfig( nodeVersion.indexConfigDocument() ) ).build();
    }

    private static @Nullable PropertySet resolveMediaSet( final PropertyTree data, final @Nullable String textAttachmentName )
    {
        final PropertySet contentData = data.getSet( ContentPropertyNames.DATA );
        if ( contentData == null )
        {
            return null;
        }

        final Property mediaProperty = contentData.getProperty( ContentPropertyNames.MEDIA );
        if ( mediaProperty != null && ValueTypes.PROPERTY_SET.equals( mediaProperty.getType() ) )
        {
            return mediaProperty.getSet();
        }

        final String attachmentName =
            mediaProperty != null && ValueTypes.STRING.equals( mediaProperty.getType() ) ? mediaProperty.getString() : textAttachmentName;
        if ( attachmentName == null )
        {
            return null;
        }

        contentData.removeProperties( ContentPropertyNames.MEDIA );
        final PropertySet mediaSet = contentData.addSet( ContentPropertyNames.MEDIA );
        mediaSet.addString( ContentPropertyNames.MEDIA_ATTACHMENT, attachmentName );
        return mediaSet;
    }

    private static IndexConfigDocument upgradeIndexConfig( final IndexConfigDocument indexConfigDocument )
    {
        if ( !( indexConfigDocument instanceof PatternIndexConfigDocument patternIndexConfigDocument ) )
        {
            return indexConfigDocument;
        }

        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create( patternIndexConfigDocument );
        builder.analyzer( patternIndexConfigDocument.getAnalyzer() );

        for ( final PathIndexConfig pathIndexConfig : patternIndexConfigDocument.getPathIndexConfigs() )
        {
            final IndexPath indexPath = pathIndexConfig.getIndexPath();
            if ( LEGACY_ATTACHMENT_TEXT_PATH.equals( indexPath ) || MEDIA_TEXT_PATH.equals( indexPath ) )
            {
                builder.remove( pathIndexConfig );
            }
        }

        builder.add( MEDIA_TEXT_PATH, MEDIA_TEXT_INDEX_CONFIG );

        return builder.build();
    }
}
