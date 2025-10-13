package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.content.ContentVersionPublishInfo;
import com.enonic.xp.core.impl.content.serializer.PublishInfoSerializer;
import com.enonic.xp.core.impl.content.serializer.WorkflowInfoSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.security.PrincipalKey;

class ContentVersionFactory
{
    private final NodeService nodeService;

    private final WorkflowInfoSerializer workflowInfoSerializer = new WorkflowInfoSerializer();

    private final PublishInfoSerializer publishInfoSerializer = new PublishInfoSerializer();

    ContentVersionFactory( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    public ContentVersion create( final NodeVersionMetadata nodeVersionMetadata )
    {
        final NodeVersion nodeVersion = nodeService.getByNodeVersionKey( nodeVersionMetadata.getNodeVersionKey() );
        final PropertyTree data = nodeVersion.getData();

        ContentVersionPublishInfo publishInfo = null;
        if ( nodeVersionMetadata.getNodeCommitId() != null )
        {
            final NodeCommitEntry nodeCommitEntry = nodeService.getCommit( nodeVersionMetadata.getNodeCommitId() );
            if ( nodeCommitEntry != null )
            {
                final String commitMessage = nodeCommitEntry.getMessage();
                final ContentVersionPublishInfo.Builder builder = ContentVersionPublishInfo.create()
                    .message( getCommentPart( commitMessage ) )
                    .type( getType( nodeCommitEntry ) )
                    .publisher( nodeCommitEntry.getCommitter() )
                    .timestamp( nodeCommitEntry.getTimestamp() );

                if ( commitMessage.startsWith( ContentConstants.PUBLISH_COMMIT_PREFIX ) ||
                    commitMessage.startsWith( ContentConstants.UNPUBLISH_COMMIT_PREFIX ) )
                {
                    builder.contentPublishInfo( publishInfoSerializer.serialize( data.getRoot() ) );
                }

                publishInfo = builder.build();
            }
        }

        return ContentVersion.create()
            .displayName( data.getString( ContentPropertyNames.DISPLAY_NAME ) )
            .path( ContentNodeHelper.translateNodePathToContentPath( nodeVersionMetadata.getNodePath() ) )
            .comment( "No comments" )
            .modified( data.getInstant( ContentPropertyNames.MODIFIED_TIME ) )
            .timestamp( nodeVersionMetadata.getTimestamp() )
            .childOrder( nodeVersion.getChildOrder() )
            .modifier( PrincipalKey.from( data.getString( ContentPropertyNames.MODIFIER ) ) )
            .id( ContentVersionId.from( nodeVersionMetadata.getNodeVersionId().toString() ) )
            .publishInfo( publishInfo )
            .workflowInfo( workflowInfoSerializer.extract( data.getRoot().getPropertySet( ContentPropertyNames.WORKFLOW_INFO ) ) )
            .permissions( nodeVersion.getPermissions() )
            .build();
    }

    private static String getCommentPart( final String message )
    {
        if ( message.startsWith( ContentConstants.PUBLISH_COMMIT_PREFIX + ContentConstants.PUBLISH_COMMIT_PREFIX_DELIMITER ) )
        {
            return message.substring(
                ContentConstants.PUBLISH_COMMIT_PREFIX.length() + ContentConstants.PUBLISH_COMMIT_PREFIX_DELIMITER.length() );
        }
        else if ( message.startsWith( ContentConstants.ARCHIVE_COMMIT_PREFIX + ContentConstants.PUBLISH_COMMIT_PREFIX_DELIMITER ) )
        {
            return message.substring(
                ContentConstants.ARCHIVE_COMMIT_PREFIX.length() + ContentConstants.PUBLISH_COMMIT_PREFIX_DELIMITER.length() );
        }
        else
        {
            return null;
        }
    }

    private static ContentVersionPublishInfo.CommitType getType( final NodeCommitEntry nodeCommitEntry )
    {
        if ( nodeCommitEntry.getMessage().startsWith( ContentConstants.PUBLISH_COMMIT_PREFIX ) )
        {
            return ContentVersionPublishInfo.CommitType.PUBLISHED;
        }
        else if ( nodeCommitEntry.getMessage().startsWith( ContentConstants.UNPUBLISH_COMMIT_PREFIX ) )
        {
            return ContentVersionPublishInfo.CommitType.UNPUBLISHED;
        }
        else if ( nodeCommitEntry.getMessage().startsWith( ContentConstants.ARCHIVE_COMMIT_PREFIX ) )
        {
            return ContentVersionPublishInfo.CommitType.ARCHIVED;
        }
        else if ( nodeCommitEntry.getMessage().startsWith( ContentConstants.RESTORE_COMMIT_PREFIX ) )
        {
            return ContentVersionPublishInfo.CommitType.RESTORED;
        }
        else
        {
            return ContentVersionPublishInfo.CommitType.CUSTOM;
        }
    }
}
