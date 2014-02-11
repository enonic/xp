package com.enonic.wem.admin.json.schema.content;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeXml;
import com.enonic.wem.xml.XmlSerializers;

public class ContentTypeConfigJson
{
    private final String contentTypeXml;

    public ContentTypeConfigJson( final ContentType contentType )
    {
        final ContentTypeXml contentTypeXml = new ContentTypeXml();
        contentTypeXml.from( contentType );
        this.contentTypeXml = XmlSerializers.contentType().serialize( contentTypeXml );
    }

    public String getContentTypeXml()
    {
        return contentTypeXml;
    }

}
