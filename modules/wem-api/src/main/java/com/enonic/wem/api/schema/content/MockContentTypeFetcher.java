package com.enonic.wem.api.schema.content;

import java.util.HashMap;
import java.util.Map;

public class MockContentTypeFetcher
    implements ContentTypeFetcher
{
    private Map<ContentTypeName, ContentType> map = new HashMap<ContentTypeName, ContentType>();

    @Override
    public ContentType getContentType( final ContentTypeName qualifiedContentTypeName )
    {
        return map.get( qualifiedContentTypeName );
    }

    public void add( final ContentType contentType )
    {
        map.put( contentType.getQualifiedName(), contentType );
    }

}
