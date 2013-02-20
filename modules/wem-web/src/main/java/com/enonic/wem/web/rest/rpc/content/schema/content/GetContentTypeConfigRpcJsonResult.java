package com.enonic.wem.web.rest.rpc.content.schema.content;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.core.content.schema.content.ContentTypeXmlSerializer;
import com.enonic.wem.web.json.JsonResult;

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
