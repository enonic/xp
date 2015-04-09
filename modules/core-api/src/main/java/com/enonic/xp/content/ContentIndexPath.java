package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.index.IndexPath;

@Beta
class ContentIndexPath
{
    public static final IndexPath MODIFIED_TIME = IndexPath.from( ContentPropertyNames.MODIFIED_TIME );

    public static final IndexPath CREATED_TIME = IndexPath.from( ContentPropertyNames.CREATED_TIME );
}
