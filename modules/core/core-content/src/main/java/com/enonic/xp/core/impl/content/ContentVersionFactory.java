package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.ContentVersionCommitInfo;
import com.enonic.xp.content.ContentVersionId;
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

        final ContentVersion.Builder builder = ContentVersion.create()
            .id( ContentVersionId.from( nodeVersionMetadata.getNodeVersionId().toString() ) )
            .path( ContentNodeHelper.translateNodePathToContentPath( nodeVersionMetadata.getNodePath() ) )
            .timestamp( nodeVersionMetadata.getTimestamp() )
            .childOrder( nodeVersion.getChildOrder() )
            .permissions( nodeVersion.getPermissions() )
            .displayName( data.getString( ContentPropertyNames.DISPLAY_NAME ) )
            .publishInfo( publishInfoSerializer.serialize( data.getSet( ContentPropertyNames.PUBLISH_INFO ) ) )
            .workflowInfo( workflowInfoSerializer.extract( data.getSet( ContentPropertyNames.WORKFLOW_INFO ) ) )
            .modifier( PrincipalKey.from( data.getString( ContentPropertyNames.MODIFIER ) ) )
            .modified( data.getInstant( ContentPropertyNames.MODIFIED_TIME ) )
            .comment( "No comments" );

        if ( nodeVersionMetadata.getNodeCommitId() != null )
        {
            final NodeCommitEntry nodeCommitEntry = nodeService.getCommit( nodeVersionMetadata.getNodeCommitId() );
            if ( nodeCommitEntry != null )
            {
                final String commitMessage = nodeCommitEntry.getMessage();
                builder.commitInfo( ContentVersionCommitInfo.create()
                                        .message( getCommentPart( commitMessage ) )
                                        .type( getType( commitMessage ) )
                                        .publisher( nodeCommitEntry.getCommitter() )
                                        .timestamp( nodeCommitEntry.getTimestamp() )
                                        .build() );
            }
        }

        return builder.build();
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

    private static ContentVersionCommitInfo.CommitType getType( final String message )
    {
        if ( message.startsWith( ContentConstants.PUBLISH_COMMIT_PREFIX ) )
        {
            return ContentVersionCommitInfo.CommitType.PUBLISHED;
        }
        else if ( message.startsWith( ContentConstants.UNPUBLISH_COMMIT_PREFIX ) )
        {
            return ContentVersionCommitInfo.CommitType.UNPUBLISHED;
        }
        else if ( message.startsWith( ContentConstants.ARCHIVE_COMMIT_PREFIX ) )
        {
            return ContentVersionCommitInfo.CommitType.ARCHIVED;
        }
        else if ( message.startsWith( ContentConstants.RESTORE_COMMIT_PREFIX ) )
        {
            return ContentVersionCommitInfo.CommitType.RESTORED;
        }
        else
        {
            return ContentVersionCommitInfo.CommitType.CUSTOM;
        }
    }
}
