package com.enonic.wem.admin.rpc.schema.content;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.schema.content.serializer.ContentTypeXmlSerializer;

final class GetContentTypeConfigJsonResult
    extends JsonResult
{
    private final static ContentTypeXmlSerializer contentTypeXmlSerializer = new ContentTypeXmlSerializer().prettyPrint( true );

    private final ContentTypes contentTypes;

    public GetContentTypeConfigJsonResult( final ContentTypes contentTypes )
    {
        this.contentTypes = contentTypes;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        final ArrayNode array = json.putArray( "contentTypeXmls" );

        for ( final ContentType contentType : this.contentTypes )
        {
            final String contentTypeXml = contentTypeXmlSerializer.toString( contentType );
            array.add( contentTypeXml );
        }
    }
}
