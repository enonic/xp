package com.enonic.wem.admin.rest.resource.upload;

import java.util.List;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.admin.rest.service.upload.UploadItem;

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
        json.put( "id", item.getId() );
        json.put( "name", item.getName() );
        json.put( "mimeType", item.getMimeType() );
        json.put( "uploadTime", item.getUploadTime() );
        json.put( "size", item.getSize() );
        return json;
    }
}
