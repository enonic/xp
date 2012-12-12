package com.enonic.wem.web.rest.rpc.content.type;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.core.content.type.ContentTypeXmlSerializer;
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
