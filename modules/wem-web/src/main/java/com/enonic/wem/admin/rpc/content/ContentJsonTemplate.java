package com.enonic.wem.admin.rpc.content;


import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.rest.resource.content.ContentImageUriResolver;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;

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
        contentNode.put( "allowsChildren", contentTypeAllowsChildren( content.getType() ) );
        if ( content.getId() == null )
        {
            contentNode.put( "iconUrl", SchemaImageUriResolver.resolve( content.getType() ) );
        }
        else
        {
            contentNode.put( "iconUrl", ContentImageUriResolver.resolve( content ) );
        }
    }

    private static boolean contentTypeAllowsChildren( QualifiedContentTypeName contentTypeName )
    {
        // quick hack to avoid refactoring ContentJsonTemplate and related classes before 18/04
        // TODO retrieve content type and check value of allow-children flag
        return !( contentTypeName.isImageMedia() || contentTypeName.isArchiveMedia() || contentTypeName.isAudioMedia() ||
            contentTypeName.isCodeMedia() || contentTypeName.isDataMedia() || contentTypeName.isDocumentMedia() ||
            contentTypeName.isExecutableMedia() || contentTypeName.isMedia() || contentTypeName.isSpreadsheetMedia() ||
            contentTypeName.isPresentationMedia() || contentTypeName.isTextMedia() || contentTypeName.isVectorMedia() ||
            contentTypeName.isVideoMedia() );
    }
}
