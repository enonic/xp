package com.enonic.wem.api.content.page;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.context.Context;

public interface PageService
{
    public Content create( CreatePageParams params, Context context );

    public Content update( UpdatePageParams params, Context context );

    public Content delete( ContentId contentId, Context context );
}
