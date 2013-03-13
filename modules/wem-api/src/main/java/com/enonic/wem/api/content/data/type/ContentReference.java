package com.enonic.wem.api.content.data.type;

public class ContentReference
    extends BaseDataType
{
    ContentReference( int key )
    {
        super( key, JavaType.CONTENT_ID );
    }
}
