package com.enonic.xp.core.impl.content.page;


import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.page.CreatePageTemplateParams;
import com.enonic.xp.page.GetDefaultPageTemplateParams;
import com.enonic.xp.page.PageService;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.page.PageTemplates;

@Component(immediate = true)
public final class PageTemplateServiceImpl
    implements PageTemplateService
{
    protected ContentService contentService;

    protected PageService pageService;

    @Override
    public PageTemplate create( final CreatePageTemplateParams params )
    {
        return new CreatePageTemplateCommand().
            site( params.getSite() ).
            name( params.getName() ).
            displayName( params.getDisplayName() ).
            controller( params.getController() ).
            supports( params.getSupports() ).
            pageRegions( params.getPageRegions() ).
            pageConfig( params.getPageConfig() ).
            contentService( this.contentService ).
            pageService( this.pageService ).
            execute();
    }

    @Override
    public PageTemplate getByKey( final PageTemplateKey pageTemplateKey )
    {
        Preconditions.checkNotNull( pageTemplateKey, "A PageTemplateKey is required" );
        return new GetPageTemplateByKeyCommand().
            pageTemplateKey( pageTemplateKey ).
            contentService( this.contentService ).
            execute();
    }

    @Override
    public PageTemplate getDefault( GetDefaultPageTemplateParams params )
    {
        return new GetDefaultPageTemplateCommand().
            contentType( params.getContentType() ).
            site( params.getSite() ).
            sitePath( params.getSitePath() ).
            contentService( this.contentService ).
            execute();
    }

    @Override
    public PageTemplates getBySite( final ContentId siteId )
    {
        Preconditions.checkNotNull( siteId, "A ContentId is required" );
        return new GetPageTemplateBySiteCommand().
            site( siteId ).
            contentService( this.contentService ).
            execute();
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setPageService( final PageService pageService )
    {
        this.pageService = pageService;
    }
}
