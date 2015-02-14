package com.enonic.xp.core.content.page;

import com.enonic.xp.core.content.Content;
import com.enonic.xp.core.content.ContentId;

public interface PageService
{
    public Content create( CreatePageParams params );

    public Content update( UpdatePageParams params );

    public Content delete( ContentId contentId );
}
