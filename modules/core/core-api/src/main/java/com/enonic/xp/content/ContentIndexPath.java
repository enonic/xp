package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.index.IndexPath;

@Beta
public class ContentIndexPath
{
    private static final String DIVIDER = ".";

    public static final IndexPath MODIFIED_TIME = IndexPath.from( ContentPropertyNames.MODIFIED_TIME );

    public static final IndexPath CREATED_TIME = IndexPath.from( ContentPropertyNames.CREATED_TIME );

    public static final IndexPath PUBLISH_FROM =
        IndexPath.from( PropertyPath.from( ContentPropertyNames.PUBLISH_INFO, ContentPropertyNames.PUBLISH_FROM ).toString() );

    public static final IndexPath PUBLISH_TO =
        IndexPath.from( PropertyPath.from( ContentPropertyNames.PUBLISH_INFO, ContentPropertyNames.PUBLISH_TO ).toString() );
}
