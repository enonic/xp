package com.enonic.wem.core.content.page;

import javax.inject.Inject;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.page.CreatePageParams;
import com.enonic.wem.api.content.page.PageService;
import com.enonic.wem.api.content.page.UpdatePageParams;
import com.enonic.wem.api.context.Context;

public final class PageServiceImpl
    implements PageService
{
    @Inject
    protected ContentService contentService;

    @Override
    public Content create( final CreatePageParams params, final Context context )
    {
        return CreatePageCommand.create().
            contentService( this.contentService ).
            params( params ).
            context( context ).
            build().
            execute();
    }

    @Override
    public Content update( final UpdatePageParams params, final Context context )
    {
        return UpdatePageCommand.create().
            contentService( this.contentService ).
            params( params ).
            context( context ).
            build().
            execute();
    }

    @Override
    public Content delete( final ContentId contentId, final Context context )
    {
        return DeletePageCommand.create().
            contentService( this.contentService ).
            contentId( contentId ).
            context( context ).
            build().
            execute();
    }

    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
