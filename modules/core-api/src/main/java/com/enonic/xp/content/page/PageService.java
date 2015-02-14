package com.enonic.xp.content.page;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;

public interface PageService
{
    public Content create( CreatePageParams params );

    public Content update( UpdatePageParams params );

    public Content delete( ContentId contentId );
}
