package com.enonic.wem.core.content.page;

import javax.inject.Inject;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.page.CreatePageParams;
import com.enonic.wem.api.content.page.PageService;
import com.enonic.wem.api.content.page.UpdatePageParams;

public final class PageServiceImpl
    implements PageService
{
    @Inject
    protected ContentService contentService;

    @Override
    public Content create( final CreatePageParams params )
    {
        return new CreatePageCommand().contentService( this.contentService ).params( params ).execute();
    }

    @Override
    public Content update( final UpdatePageParams params )
    {
        return new UpdatePageCommand().contentService( this.contentService ).params( params ).execute();
    }

    @Override
    public Content delete( final ContentId contentId )
    {
        return new DeletePageCommand().contentService( this.contentService ).contentId( contentId ).execute();
    }
}
