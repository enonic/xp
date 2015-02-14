package com.enonic.xp.core.impl.content.page;


import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Preconditions;

import com.enonic.xp.core.content.ContentId;
import com.enonic.xp.core.content.ContentService;
import com.enonic.xp.core.content.page.CreatePageTemplateParams;
import com.enonic.xp.core.content.page.GetDefaultPageTemplateParams;
import com.enonic.xp.core.content.page.PageService;
import com.enonic.xp.core.content.page.PageTemplate;
import com.enonic.xp.core.content.page.PageTemplateKey;
import com.enonic.xp.core.content.page.PageTemplateService;
import com.enonic.xp.core.content.page.PageTemplates;

@Component(immediate = true)
public final class PageTemplateServiceImpl
    implements PageTemplateService
{
    protected ContentService contentService;

    protected PageService pageService;

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
            contentService( this.contentService ).
            execute();
    }

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
