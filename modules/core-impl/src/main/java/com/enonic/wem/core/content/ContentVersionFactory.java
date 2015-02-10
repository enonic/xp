package com.enonic.wem.core.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentVersion;
import com.enonic.wem.api.content.ContentVersionId;
import com.enonic.wem.api.content.ContentVersions;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.api.node.NodeVersion;
import com.enonic.wem.api.node.NodeVersions;

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
        final Content content = translator.fromNode( node );

        return ContentVersion.create().
            comment( "No comments" ).
            displayName( content.getDisplayName() ).
            modified( content.getModifiedTime() ).
            modifier( content.getModifier() ).
            id( ContentVersionId.from( nodeVersion.getId().toString() ) ).
            build();
    }

    private Node getNode( final NodeVersion nodeVersion )
    {
        return nodeService.getByVersionId( nodeVersion.getId() );
    }

}
