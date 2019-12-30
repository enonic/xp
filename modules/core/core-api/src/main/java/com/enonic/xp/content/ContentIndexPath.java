package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.index.IndexPath;

@PublicApi
public class ContentIndexPath
{
    public static final IndexPath MODIFIED_TIME = IndexPath.from( ContentPropertyNames.MODIFIED_TIME );

    public static final IndexPath CREATED_TIME = IndexPath.from( ContentPropertyNames.CREATED_TIME );

    public static final IndexPath PUBLISH_FROM =
        IndexPath.from( PropertyPath.from( ContentPropertyNames.PUBLISH_INFO, ContentPropertyNames.PUBLISH_FROM ).toString() );

    public static final IndexPath PUBLISH_TO =
        IndexPath.from( PropertyPath.from( ContentPropertyNames.PUBLISH_INFO, ContentPropertyNames.PUBLISH_TO ).toString() );

    public static final IndexPath PUBLISH_FIRST =
        IndexPath.from( PropertyPath.from( ContentPropertyNames.PUBLISH_INFO, ContentPropertyNames.PUBLISH_FIRST ).toString() );

    public static final IndexPath REFERENCES = IndexPath.from( "_references" );

    public static final IndexPath ID = IndexPath.from( "_id" );
}
