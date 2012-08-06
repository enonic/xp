package com.enonic.wem.web.rest2.resource.upload;

import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.rest2.common.JsonResult;
import com.enonic.wem.web.rest2.service.upload.UploadItem;

public final class UploadResult
    extends JsonResult
{
    private final List<UploadItem> items;

    public UploadResult( final List<UploadItem> items )
    {
        this.items = items;
    }

    @Override
    public JsonNode toJson()
    {
        final ObjectNode json = objectNode();
        final ArrayNode array = json.putArray( "items" );

        for ( final UploadItem item : this.items )
        {
            array.add( toJson( item ) );
        }

        return json;
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
