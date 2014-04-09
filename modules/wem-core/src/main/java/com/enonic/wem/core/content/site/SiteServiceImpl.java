package com.enonic.wem.core.content.site;

import javax.inject.Inject;

import com.enonic.wem.api.content.site.CreateSiteParams;
import com.enonic.wem.api.content.site.UpdateSiteParams;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.site.SiteService;

public final class SiteServiceImpl
    implements SiteService
{
    @Inject
    protected ContentService contentService;

    @Override
    public Content create( final CreateSiteParams params )
    {
        return new CreateSiteCommand().contentService( this.contentService ).params( params ).execute();
    }

    @Override
    public Content update( final UpdateSiteParams params )
    {
        return new UpdateSiteCommand().contentService( this.contentService ).params( params ).execute();
    }

    @Override
    public Content delete( final ContentId contentId )
    {
        return new DeleteSiteCommand().contentService( this.contentService ).contentId( contentId ).execute();
    }

    @Override
    public Content getNearestSite( final ContentId contentId )
    {
        return new GetNearestSiteCommand().contentService( this.contentService ).contentId( contentId ).execute();
    }
}
