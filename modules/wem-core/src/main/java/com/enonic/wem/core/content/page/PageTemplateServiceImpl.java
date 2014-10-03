package com.enonic.wem.core.content.page;


import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.page.CreatePageTemplateParams;
import com.enonic.wem.api.content.page.GetDefaultPageTemplateParams;
import com.enonic.wem.api.content.page.PageService;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.page.PageTemplates;
import com.enonic.wem.api.context.Context;

public final class PageTemplateServiceImpl
    implements PageTemplateService
{
    protected ContentService contentService;

    protected PageService pageService;

    public PageTemplate create( final CreatePageTemplateParams params, final Context context )
    {
        return new CreatePageTemplateCommand().
            site( params.getSite() ).
            name( params.getName() ).
            displayName( params.getDisplayName() ).
            controller( params.getController() ).
            supports( params.getSupports() ).
            pageRegions( params.getPageRegions() ).
            pageConfig( params.getPageConfig() ).
            context( context ).
            contentService( this.contentService ).
            pageService( this.pageService ).
            execute();
    }

    public PageTemplate getByKey( final PageTemplateKey pageTemplateKey, final Context context )
    {
        return new GetPageTemplateByKeyCommand().
            pageTemplateKey( pageTemplateKey ).
            contentService( this.contentService ).
            context( context ).
            execute();
    }

    @Override
    public PageTemplate getDefault( GetDefaultPageTemplateParams params, final Context context )
    {
        return new GetDefaultPageTemplateCommand().
            contentType( params.getContentType() ).
            site( params.getSite() ).
            context( context ).
            contentService( this.contentService ).
            execute();
    }

    public PageTemplates getBySite( final ContentId siteId, final Context context )
    {
        return new GetPageTemplateBySiteCommand().
            site( siteId ).
            context( context ).
            contentService( this.contentService ).
            execute();
    }

    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    public void setPageService( final PageService pageService )
    {
        this.pageService = pageService;
    }
}
