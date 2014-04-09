package com.enonic.wem.api.content.page;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;

public interface PageService
{
    public Content create( CreatePageParams params );

    public Content update( UpdatePageParams params );

    public Content delete( ContentId contentId );
}
