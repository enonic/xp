package com.enonic.wem.api.content.type;

import java.util.HashMap;
import java.util.Map;

public class MockContentTypeFetcher
    implements ContentTypeFetcher
{
    private Map<ContentTypeQualifiedName, ContentType> map = new HashMap<ContentTypeQualifiedName, ContentType>();

    @Override
    public ContentType getContentType( final ContentTypeQualifiedName qualifiedName )
    {
        return map.get( qualifiedName );
    }

    public void add( final ContentType contentType )
    {
        map.put( contentType.getQualifiedName(), contentType );
    }

}
