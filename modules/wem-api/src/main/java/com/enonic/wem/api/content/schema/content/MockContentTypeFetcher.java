package com.enonic.wem.api.content.schema.content;

import java.util.HashMap;
import java.util.Map;

public class MockContentTypeFetcher
    implements ContentTypeFetcher
{
    private Map<QualifiedContentTypeName, ContentType> map = new HashMap<QualifiedContentTypeName, ContentType>();

    @Override
    public ContentType getContentType( final QualifiedContentTypeName qualifiedContentTypeName )
    {
        return map.get( qualifiedContentTypeName );
    }

    public void add( final ContentType contentType )
    {
        map.put( contentType.getQualifiedName(), contentType );
    }

}
