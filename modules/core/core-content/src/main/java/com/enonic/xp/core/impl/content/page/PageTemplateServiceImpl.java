package com.enonic.xp.core.impl.content.page;


import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.page.CreatePageTemplateParams;
import com.enonic.xp.page.GetDefaultPageTemplateParams;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.page.PageTemplates;

@Component(immediate = true)
public final class PageTemplateServiceImpl
    implements PageTemplateService
{
    private final ContentService contentService;

    @Activate
    public PageTemplateServiceImpl( @Reference final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Override
    public PageTemplate create( final CreatePageTemplateParams params )
    {
        return new CreatePageTemplateCommand().
            site( params.getSite() ).
            name( params.getName() ).
            displayName( params.getDisplayName() ).
            controller( params.getController() ).
            supports( params.getSupports() ).regions( params.getRegions() ).
            pageConfig( params.getPageConfig() ).
            contentService( this.contentService ).
            execute();
    }

    @Override
    public PageTemplate getByKey( final PageTemplateKey pageTemplateKey )
    {
        Objects.requireNonNull( pageTemplateKey, "pageTemplateKey is required" );
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
        Objects.requireNonNull( siteId, "siteId is required" );
        return new GetPageTemplateBySiteCommand().
            site( siteId ).
            contentService( this.contentService ).
            execute();
    }
}
