package com.enonic.wem.core.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentVersion;
import com.enonic.wem.api.content.ContentVersionId;
import com.enonic.wem.api.content.ContentVersions;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.entity.NodeVersion;
import com.enonic.wem.api.entity.NodeVersions;

public class ContentVersionFactory
{
    private final ContentNodeTranslator translator;

    private final NodeService nodeService;

    private final Context context;

    public ContentVersionFactory( final ContentNodeTranslator translator, final NodeService nodeService, final Context context )
    {
        this.translator = translator;
        this.nodeService = nodeService;
        this.context = context;
    }

    public ContentVersions create( final EntityId entityId, final NodeVersions nodeVersions )
    {
        final ContentVersions.Builder contentVersionsBuilder = ContentVersions.create().
            contentId( ContentId.from( entityId ) );

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
            comment( "Dummy comment" ).
            displayName( content.getDisplayName() ).
            modified( content.getModifiedTime() ).
            modifier( content.getModifier() ).
            id( ContentVersionId.from( nodeVersion.getId() ) ).
            build();
    }

    private Node getNode( final NodeVersion nodeVersion )
    {
        return nodeService.getByVersionId( nodeVersion.getId(), this.context );
    }

}
