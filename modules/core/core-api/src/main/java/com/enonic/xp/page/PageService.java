package com.enonic.xp.page;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;

@PublicApi
public interface PageService
{
    Content create( CreatePageParams params );

    Content update( UpdatePageParams params );

    Content delete( ContentId contentId );
}
