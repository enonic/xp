package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.*;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.*;
import com.enonic.xp.security.PrincipalKey;

class ContentVersionFactory
{
    private final ContentNodeTranslator translator;

    private final NodeService nodeService;

    public ContentVersionFactory( final ContentNodeTranslator translator, final NodeService nodeService )
    {
        this.translator = translator;
        this.nodeService = nodeService;
    }

    public ContentVersions create( final NodeId nodeId, final NodeVersionsMetadata nodeVersionsMetadata )
    {
        final ContentVersions.Builder contentVersionsBuilder = ContentVersions.create().
            contentId( ContentId.from( nodeId.toString() ) );

        for ( final NodeVersionMetadata nodeVersionMetadata : nodeVersionsMetadata )
        {
            contentVersionsBuilder.add( doCreateContentVersion( getNodeVersion( nodeVersionMetadata ) ) );
        }

        return contentVersionsBuilder.build();
    }

    public ContentVersion create( final NodeVersionMetadata nodeVersionMetadata )
    {
        return doCreateContentVersion( getNodeVersion( nodeVersionMetadata ) );
    }

    private ContentVersion doCreateContentVersion( final NodeVersion nodeVersion )
    {
        final PropertyTree data = nodeVersion.getData();

        return ContentVersion.create().
            displayName( data.getProperty( ContentPropertyNames.DISPLAY_NAME ).getString() ).
            comment( "No comments" ).
            modified( data.getProperty( ContentPropertyNames.MODIFIED_TIME ).getInstant() ).
            modifier( PrincipalKey.from( data.getProperty( ContentPropertyNames.MODIFIER ).getString() ) ).
            id( ContentVersionId.from( nodeVersion.getVersionId().toString() ) ).
            build();
    }

    private NodeVersion getNodeVersion( final NodeVersionMetadata nodeVersionMetadata )
    {
        return nodeService.getByNodeVersion( nodeVersionMetadata );
    }

}
