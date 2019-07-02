package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.content.ContentVersionPublishInfo;
import com.enonic.xp.content.ContentVersions;
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
    private static final String NODE_CONTENT_PUBLISH_MESSAGE = "COM_ENONIC_XP_CONTENT_PUBLISH";

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
            build();
    }

    private ContentVersionPublishInfo doCreateContentVersionPublishInfo( final NodeCommitId nodeCommitId )
    {
        if ( nodeCommitId == null )
        {
            return null;
        }

        NodeCommitEntry nodeCommitEntry = nodeService.getCommit( nodeCommitId );
        return ContentVersionPublishInfo.create().
            message( cleanUpMessage( nodeCommitEntry.getMessage() ) ).
            publisher( nodeCommitEntry.getCommitter() ).
            timestamp( nodeCommitEntry.getTimestamp() ).
            build();
    }

    private String cleanUpMessage( String message )
    {
        if ( message == null )
        {
            return null;
        }

        String res = message.replace( NODE_CONTENT_PUBLISH_MESSAGE, "" ).trim();

        return res.equals( "" ) ? null : res;
    }

    private NodeVersion getNodeVersion( final NodeVersionMetadata nodeVersionMetadata )
    {
        return nodeService.getByNodeVersionKey( nodeVersionMetadata.getNodeVersionKey() );
    }

}
