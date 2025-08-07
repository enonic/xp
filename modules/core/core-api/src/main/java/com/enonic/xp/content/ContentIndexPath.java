package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.index.IndexPath;

@PublicApi
public final class ContentIndexPath
{
    public static final IndexPath MODIFIED_TIME = IndexPath.from( ContentPropertyNames.MODIFIED_TIME );

    public static final IndexPath CREATED_TIME = IndexPath.from( ContentPropertyNames.CREATED_TIME );

    public static final IndexPath PUBLISH_FROM =
        IndexPath.from( String.join(".", ContentPropertyNames.PUBLISH_INFO, ContentPropertyNames.PUBLISH_FROM ) );

    public static final IndexPath PUBLISH_TO =
        IndexPath.from( String.join(".", ContentPropertyNames.PUBLISH_INFO, ContentPropertyNames.PUBLISH_TO ) );

    public static final IndexPath PUBLISH_FIRST =
        IndexPath.from( String.join(".", ContentPropertyNames.PUBLISH_INFO, ContentPropertyNames.PUBLISH_FIRST ) );

    public static final IndexPath REFERENCES = IndexPath.from( "_references" );

    public static final IndexPath ID = IndexPath.from( "_id" );

    private ContentIndexPath()
    {
    }
}
