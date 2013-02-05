package com.enonic.wem.web.rest.rpc.content;


import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.web.rest.resource.content.ContentTypeImageUriResolver;

final class ContentJsonTemplate
{
    /**
     * Sets fields needed for content listing.
     */
    static void forContentListing( final ObjectNode contentNode, final Content content )
    {
        contentNode.put( "id", content.getId() == null ? null : content.getId().toString() );
        contentNode.put( "path", content.getPath().toString() );
        contentNode.put( "name", content.getName() );
        contentNode.put( "type", content.getType() != null ? content.getType().toString() : null );
        contentNode.put( "displayName", content.getDisplayName() );
        contentNode.put( "owner", content.getOwner() != null ? content.getOwner().toString() : null );
        contentNode.put( "createdTime", content.getCreatedTime().toString() );
        contentNode.put( "modifier", content.getModifier() != null ? content.getModifier().toString() : null );
        contentNode.put( "modifiedTime", content.getModifiedTime() != null ? content.getModifiedTime().toString() : null );
        contentNode.put( "editable", true );
        contentNode.put( "deletable", true );
        contentNode.put( "iconUrl", ContentTypeImageUriResolver.resolve( content.getType() ) );
    }
}
