package com.enonic.wem.admin.rest.resource.schema.content.model;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.core.schema.content.serializer.ContentTypeXmlSerializer;

public class ContentTypeConfigRpcJson
    extends AbstractContentTypeJson
{
    private final String contentTypeXml;

    public ContentTypeConfigRpcJson( final ContentType contentType )
    {
        this.contentTypeXml = new ContentTypeXmlSerializer().prettyPrint( true ).toString( contentType );
    }

    public String getContentTypeXml()
    {
        return contentTypeXml;
    }
}
