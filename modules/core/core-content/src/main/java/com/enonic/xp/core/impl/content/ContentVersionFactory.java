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

    public ContentVersionFactory( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    public ContentVersions create( final NodeId nodeId, final NodeVersionsMetadata nodeVersionsMetadata )
    {
        final ContentVersions.Builder contentVersionsBuilder = ContentVersions.create().
            contentId( ContentId.from( nodeId.toString() ) );

        for ( final NodeVersionMetadata nodeVersionMetadata : nodeVersionsMetadata )
        {
            final ContentVersion contentVersion = create( nodeVersionMetadata );
            contentVersionsBuilder.add( contentVersion );
        }

        return contentVersionsBuilder.build();
    }

    public ContentVersion create( final NodeVersionMetadata nodeVersionMetadata )
    {
        final NodeVersion nodeVersion = getNodeVersion( nodeVersionMetadata );
        return doCreateContentVersion( nodeVersionMetadata, nodeVersion );
    }

    private ContentVersion doCreateContentVersion( final NodeVersionMetadata nodeVersionMetadata, final NodeVersion nodeVersion )
    {
        final PropertyTree data = nodeVersion.getData();

        return ContentVersion.create().
            displayName( data.getProperty( ContentPropertyNames.DISPLAY_NAME ).getString() ).
            comment( "No comments" ).
            modified( data.getProperty( ContentPropertyNames.MODIFIED_TIME ).getInstant() ).
            modifier( PrincipalKey.from( data.getProperty( ContentPropertyNames.MODIFIER ).getString() ) ).
            id( ContentVersionId.from( nodeVersionMetadata.getNodeVersionId().toString() ) ).
            publishInfo( doCreateContentVersionPublishInfo( nodeVersionMetadata.getNodeCommitId(), data.getRoot() ) ).
            workflowInfo( doCreateContentVersionWorkflowInfo( data.getRoot() ) ).
            build();
    }

    private ContentVersionPublishInfo doCreateContentVersionPublishInfo( final NodeCommitId nodeCommitId,
                                                                         final PropertySet nodeVersionData )
    {
        if ( nodeCommitId != null )
        {
            final NodeCommitEntry nodeCommitEntry = nodeService.getCommit( nodeCommitId );

            if ( nodeCommitEntry != null )
            {
                if ( nodeCommitEntry.getMessage().startsWith( ContentConstants.PUBLISH_COMMIT_PREFIX ) ||
                    nodeCommitEntry.getMessage().startsWith( ContentConstants.UNPUBLISH_COMMIT_PREFIX ) )
                {
                    final ContentVersionPublishInfo.Builder builder = ContentVersionPublishInfo.create().
                        message( getMessage( nodeCommitEntry ) ).
                        publisher( nodeCommitEntry.getCommitter() ).
                        timestamp( nodeCommitEntry.getTimestamp() );

                    final ContentPublishInfo contentPublishInfo = publishInfoSerializer.serialize( nodeVersionData );

                    if ( contentPublishInfo != null )
                    {
                        builder.contentPublishInfo( contentPublishInfo );
                    }

                    return builder.build();
                }
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
        if ( nodeCommitEntry.getMessage().startsWith(
            ContentConstants.PUBLISH_COMMIT_PREFIX + ContentConstants.PUBLISH_COMMIT_PREFIX_DELIMITER ) )
        {
            return nodeCommitEntry.getMessage().substring(
                ContentConstants.PUBLISH_COMMIT_PREFIX.length() + ContentConstants.PUBLISH_COMMIT_PREFIX_DELIMITER.length() );
        }
        return null;
    }

    private NodeVersion getNodeVersion( final NodeVersionMetadata nodeVersionMetadata )
    {
        return nodeService.getByNodeVersionKey( nodeVersionMetadata.getNodeVersionKey() );
    }

}
