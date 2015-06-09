package com.enonic.xp.page;

import com.google.common.annotations.Beta;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;

@Beta
public interface PageService
{
    Content create( CreatePageParams params );

    Content update( UpdatePageParams params );

    Content delete( ContentId contentId );
}
