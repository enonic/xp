package com.enonic.wem.api.schema.content;


import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.Property;

public class ImageContentType
    extends ContentType
{
    ImageContentType( final Builder builder )
    {
        super( builder );
    }

    public static String getImageAttachmentName( final Content content )
    {
        final ContentData contentData = content.getContentData();
        final Property imageProperty = contentData.getProperty( "image" );
        return imageProperty == null ? content.getName().toString() : imageProperty.getString();
    }
}
