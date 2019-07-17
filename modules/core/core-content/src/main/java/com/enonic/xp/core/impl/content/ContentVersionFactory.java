package com.enonic.xp.core.impl.content;

import java.util.Map;
import java.util.stream.Collectors;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.content.ContentVersionPublishInfo;
import com.enonic.xp.content.ContentVersions;
import com.enonic.xp.content.WorkflowCheckState;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyArray;
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
            publishInfo( doCreateContentVersionPublishInfo( nodeVersionMetadata.getNodeCommitId() ) ).
            workflowInfo( doCreateContentVersionWorkflowInfo( data.getSet( ContentPropertyNames.DATA ) ) ).
            build();
    }

    private ContentVersionPublishInfo doCreateContentVersionPublishInfo( final NodeCommitId nodeCommitId )
    {
        if ( nodeCommitId != null )
        {
            NodeCommitEntry nodeCommitEntry = nodeService.getCommit( nodeCommitId );
            if ( nodeCommitEntry != null && nodeCommitEntry.getMessage().startsWith( ContentConstants.PUBLISH_COMMIT_PREFIX ) )
            {
                return ContentVersionPublishInfo.create().
                    message( getMessage( nodeCommitEntry ) ).
                    publisher( nodeCommitEntry.getCommitter() ).
                    timestamp( nodeCommitEntry.getTimestamp() ).
                    build();
            }
        }
        return null;
    }

    private WorkflowInfo doCreateContentVersionWorkflowInfo( final PropertySet nodeVersionData )
    {
        final PropertySet workflowInfoSet = nodeVersionData.getPropertySet( ContentPropertyNames.WORKFLOW_INFO );

        if ( workflowInfoSet == null || workflowInfoSet.getPropertySize() == 0 )
        {
            return null;
        }

        final WorkflowInfo.Builder builder = WorkflowInfo.create();
        final String state = workflowInfoSet.getString( ContentPropertyNames.WORKFLOW_INFO_STATE );
        builder.state( state );

        final PropertySet workflowInfoChecksSet = workflowInfoSet.getSet( ContentPropertyNames.WORKFLOW_INFO_CHECKS );

        if ( workflowInfoChecksSet != null && workflowInfoChecksSet.getPropertySize() > 0 )
        {
            final Map<String, WorkflowCheckState> checks = workflowInfoChecksSet.getPropertyArrays().
                stream().
                filter( propertyArray -> !propertyArray.getProperties().isEmpty() ).
                collect( Collectors.toMap( PropertyArray::getName, propertyArray -> propertyArray.getProperties().
                    stream().
                    map( Property::getString ).
                    map( WorkflowCheckState::valueOf ).
                    findFirst().
                    get() ) );

            builder.checks( checks );
        }

        return builder.build();
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
