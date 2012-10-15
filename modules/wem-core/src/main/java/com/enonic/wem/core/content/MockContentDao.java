package com.enonic.wem.core.content;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;

public class MockContentDao
{
    private static final MockContentDao INSTANCE = new MockContentDao();

    private LinkedHashMap<ContentPath, Content> contentByPath = new LinkedHashMap<ContentPath, Content>();

    private MockContentDao()
    {
    }

    public void store( final Content content )
    {
        contentByPath.put( content.getPath(), content );
    }

    public Content getContentByPath( final ContentPath contentPath )
    {
        return contentByPath.get( contentPath );
    }

    public List<Content> getContentByPaths( final ContentPaths contentPaths )
    {
        List<Content> result = new ArrayList<Content>( contentPaths.getSize() );
        for ( ContentPath contentPath : contentPaths )
        {
            result.add( contentByPath.get( contentPath ) );
        }
        return result;
    }

    public static MockContentDao get()
    {
        return INSTANCE;
    }
}
