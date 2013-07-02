package com.enonic.wem.admin.rpc.schema.content;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.core.schema.content.serializer.ContentTypeXmlSerializer;

final class GetContentTypeConfigRpcJsonResult
    extends JsonResult
{
    private final static ContentTypeXmlSerializer contentTypeXmlSerializer = new ContentTypeXmlSerializer().prettyPrint( true );

    private final ContentType contentType;

    public GetContentTypeConfigRpcJsonResult( final ContentType contentType )
    {
        this.contentType = contentType;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        final String contentTypeXml = contentTypeXmlSerializer.toString( contentType );
        json.put( "contentTypeXml", contentTypeXml );
    }
}
