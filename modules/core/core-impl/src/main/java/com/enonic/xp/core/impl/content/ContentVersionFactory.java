package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.content.ContentVersions;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersions;

class ContentVersionFactory
{
    private final ContentNodeTranslator translator;

    private final NodeService nodeService;

    public ContentVersionFactory( final ContentNodeTranslator translator, final NodeService nodeService )
    {
        this.translator = translator;
        this.nodeService = nodeService;
    }

    public ContentVersions create( final NodeId nodeId, final NodeVersions nodeVersions )
    {
        final ContentVersions.Builder contentVersionsBuilder = ContentVersions.create().
            contentId( ContentId.from( nodeId.toString() ) );

        for ( final NodeVersion nodeVersion : nodeVersions )
        {
            contentVersionsBuilder.add( doCreateContentVersion( nodeVersion, getNode( nodeVersion ) ) );
        }

        return contentVersionsBuilder.build();
    }

    public ContentVersion create( final NodeVersion nodeVersion )
    {
        return doCreateContentVersion( nodeVersion, getNode( nodeVersion ) );
    }

    private ContentVersion doCreateContentVersion( final NodeVersion nodeVersion, final Node node )
    {
        final Content content = translator.fromNode( node, false );

        return ContentVersion.create().
            comment( "No comments" ).
            displayName( content.getDisplayName() ).
            modified( content.getModifiedTime() ).
            modifier( content.getModifier() ).
            id( ContentVersionId.from( nodeVersion.getNodeVersionId().toString() ) ).
            build();
    }

    private Node getNode( final NodeVersion nodeVersion )
    {
        return nodeService.getByNodeVersion( nodeVersion );
    }

}
