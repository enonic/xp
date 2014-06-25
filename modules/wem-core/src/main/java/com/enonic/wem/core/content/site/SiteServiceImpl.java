package com.enonic.wem.core.content.site;

import javax.inject.Inject;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.site.CreateSiteParams;
import com.enonic.wem.api.content.site.SiteService;
import com.enonic.wem.api.content.site.UpdateSiteParams;
import com.enonic.wem.api.context.Context;

public final class SiteServiceImpl
    implements SiteService
{
    @Inject
    protected ContentService contentService;

    @Override
    public Content create( final CreateSiteParams params, final Context context )
    {
        return CreateSiteCommand.create().
            contentService( this.contentService ).
            params( params ).
            context( context ).
            build().
            execute();
    }

    @Override
    public Content update( final UpdateSiteParams params, final Context context )
    {
        return UpdateSiteCommand.create().
            contentService( this.contentService ).
            params( params ).
            context( context ).
            build().
            execute();
    }

    @Override
    public Content delete( final ContentId contentId, final Context context )
    {
        return DeleteSiteCommand.create().
            contentService( this.contentService ).
            contentId( contentId ).
            build().
            execute();
    }

    @Override
    public Content getNearestSite( final ContentId contentId, final Context context )
    {
        return GetNearestSiteCommand.create().
            contentService( this.contentService ).
            contentId( contentId ).
            context( context ).
            build().
            execute();
    }
}
