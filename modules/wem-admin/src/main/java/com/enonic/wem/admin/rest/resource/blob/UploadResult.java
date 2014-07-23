package com.enonic.wem.admin.rest.resource.blob;

import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;

final class UploadResult
    extends JsonResult
{
    private final List<UploadItem> items;

    public UploadResult( final List<UploadItem> items )
    {
        this.items = items;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        final ArrayNode array = json.putArray( "items" );
        for ( final UploadItem item : this.items )
        {
            array.add( toJson( item ) );
        }
    }

    private ObjectNode toJson( final UploadItem item )
    {
        final ObjectNode json = objectNode();
        json.put( "id", item.getBlobKey().toString() );
        json.put( "name", item.getName() );
        json.put( "mimeType", item.getMimeType() );
        json.put( "size", item.getSize() );
        return json;
    }
}
