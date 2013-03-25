package com.enonic.wem.web.rest.rpc.content;


import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;
import com.enonic.wem.web.rest.resource.content.ContentImageUriResolver;
import com.enonic.wem.web.rest.resource.content.schema.SchemaImageUriResolver;

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
        JsonSerializerUtil.setDateTimeValue( "createdTime", content.getCreatedTime(), contentNode );
        contentNode.put( "modifier", content.getModifier() != null ? content.getModifier().toString() : null );
        JsonSerializerUtil.setDateTimeValue( "modifiedTime", content.getModifiedTime(), contentNode );
        contentNode.put( "editable", true );
        contentNode.put( "deletable", !content.getPath().isRoot() );
        if ( content.getId() == null )
        {
            contentNode.put( "iconUrl", SchemaImageUriResolver.resolve( content.getType() ) );
        }
        else
        {
            contentNode.put( "iconUrl", ContentImageUriResolver.resolve( content ) );
        }
    }
}
