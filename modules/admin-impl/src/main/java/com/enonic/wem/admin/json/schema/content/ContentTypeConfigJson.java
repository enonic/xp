package com.enonic.wem.admin.json.schema.content;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.xml.mapper.XmlContentTypeMapper;
import com.enonic.wem.api.xml.model.XmlContentType;
import com.enonic.wem.api.xml.serializer.XmlSerializers;

public class ContentTypeConfigJson
{
    private final String contentTypeXml;

    public ContentTypeConfigJson( final ContentType contentType )
    {
        final XmlContentType contentTypeXml = XmlContentTypeMapper.toXml( contentType );
        this.contentTypeXml = XmlSerializers.contentType().serialize( contentTypeXml );
    }

    public String getContentTypeXml()
    {
        return contentTypeXml;
    }

}
