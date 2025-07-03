package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.content.ContentVersionPublishInfo;
import com.enonic.xp.content.ContentVersions;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.core.impl.content.serializer.PublishInfoSerializer;
import com.enonic.xp.core.impl.content.serializer.WorkflowInfoSerializer;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionsMetadata;
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

    public ContentVersions create( final NodeId nodeId, final NodeVersionsMetadata nodeVersionsMetadata )
    {
        final ContentVersions.Builder contentVersionsBuilder = ContentVersions.create().contentId( ContentId.from( nodeId ) );

        for ( final NodeVersionMetadata nodeVersionMetadata : nodeVersionsMetadata )
        {
            final ContentVersion contentVersion = create( nodeVersionMetadata );
            contentVersionsBuilder.add( contentVersion );
        }

        return contentVersionsBuilder.build();
    }

    public ContentVersion create( final NodeVersionMetadata nodeVersionMetadata )
    {
        final NodeVersion nodeVersion = nodeService.getByNodeVersionKey( nodeVersionMetadata.getNodeVersionKey() );
        return doCreateContentVersion( nodeVersionMetadata, nodeVersion );
    }

    private ContentVersion doCreateContentVersion( final NodeVersionMetadata nodeVersionMetadata, final NodeVersion nodeVersion )
    {
        final PropertyTree data = nodeVersion.getData();

        return ContentVersion.create()
            .displayName( data.getProperty( ContentPropertyNames.DISPLAY_NAME ).getString() )
            .path( ContentNodeHelper.translateNodePathToContentPath( nodeVersionMetadata.getNodePath() ) )
            .comment( "No comments" )
            .modified( data.getProperty( ContentPropertyNames.MODIFIED_TIME ).getInstant() )
            .timestamp( nodeVersionMetadata.getTimestamp() )
            .childOrder( nodeVersion.getChildOrder() )
            .modifier( PrincipalKey.from( data.getProperty( ContentPropertyNames.MODIFIER ).getString() ) )
            .id( ContentVersionId.from( nodeVersionMetadata.getNodeVersionId().toString() ) )
            .publishInfo( doCreateContentVersionPublishInfo( nodeVersionMetadata.getNodeCommitId(), data.getRoot() ) )
            .workflowInfo( doCreateContentVersionWorkflowInfo( data.getRoot() ) )
            .permissions( nodeVersion.getPermissions() )
            .build();
    }

    private ContentVersionPublishInfo doCreateContentVersionPublishInfo( final NodeCommitId nodeCommitId,
                                                                         final PropertySet nodeVersionData )
    {
        if ( nodeCommitId != null )
        {
            final NodeCommitEntry nodeCommitEntry = nodeService.getCommit( nodeCommitId );

            if ( nodeCommitEntry != null )
            {
                final ContentVersionPublishInfo.Builder builder = ContentVersionPublishInfo.create()
                    .message( getMessage( nodeCommitEntry ) )
                    .type( getType( nodeCommitEntry ) )
                    .publisher( nodeCommitEntry.getCommitter() )
                    .timestamp( nodeCommitEntry.getTimestamp() );

                if ( nodeCommitEntry.getMessage().startsWith( ContentConstants.PUBLISH_COMMIT_PREFIX ) ||
                    nodeCommitEntry.getMessage().startsWith( ContentConstants.UNPUBLISH_COMMIT_PREFIX ) )
                {
                    final ContentPublishInfo contentPublishInfo = publishInfoSerializer.serialize( nodeVersionData );

                    if ( contentPublishInfo != null )
                    {
                        builder.contentPublishInfo( contentPublishInfo );
                    }
                }

                return builder.build();
            }
        }
        return null;
    }

    private WorkflowInfo doCreateContentVersionWorkflowInfo( final PropertySet nodeVersionData )
    {
        final PropertySet workflowInfoSet = nodeVersionData.getPropertySet( ContentPropertyNames.WORKFLOW_INFO );
        return workflowInfoSerializer.extract( workflowInfoSet );
    }

    private String getMessage( final NodeCommitEntry nodeCommitEntry )
    {
        if ( nodeCommitEntry.getMessage()
            .startsWith( ContentConstants.PUBLISH_COMMIT_PREFIX + ContentConstants.PUBLISH_COMMIT_PREFIX_DELIMITER ) )
        {
            return nodeCommitEntry.getMessage()
                .substring( ContentConstants.PUBLISH_COMMIT_PREFIX.length() + ContentConstants.PUBLISH_COMMIT_PREFIX_DELIMITER.length() );
        }

        if ( nodeCommitEntry.getMessage()
            .startsWith( ContentConstants.ARCHIVE_COMMIT_PREFIX + ContentConstants.PUBLISH_COMMIT_PREFIX_DELIMITER ) )
        {
            return nodeCommitEntry.getMessage()
                .substring( ContentConstants.ARCHIVE_COMMIT_PREFIX.length() + ContentConstants.PUBLISH_COMMIT_PREFIX_DELIMITER.length() );
        }

        return null;
    }

    private ContentVersionPublishInfo.CommitType getType( final NodeCommitEntry nodeCommitEntry )
    {
        if ( nodeCommitEntry.getMessage().startsWith( ContentConstants.PUBLISH_COMMIT_PREFIX ) )
        {
            return ContentVersionPublishInfo.CommitType.PUBLISHED;
        }

        if ( nodeCommitEntry.getMessage().startsWith( ContentConstants.UNPUBLISH_COMMIT_PREFIX ) )
        {
            return ContentVersionPublishInfo.CommitType.UNPUBLISHED;
        }

        if ( nodeCommitEntry.getMessage().startsWith( ContentConstants.ARCHIVE_COMMIT_PREFIX ) )
        {
            return ContentVersionPublishInfo.CommitType.ARCHIVED;
        }

        if ( nodeCommitEntry.getMessage().startsWith( ContentConstants.RESTORE_COMMIT_PREFIX ) )
        {
            return ContentVersionPublishInfo.CommitType.RESTORED;
        }

        return ContentVersionPublishInfo.CommitType.CUSTOM;
    }

}
