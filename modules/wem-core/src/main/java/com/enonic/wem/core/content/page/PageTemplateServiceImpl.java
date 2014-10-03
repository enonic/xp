package com.enonic.wem.core.content.page;


import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.FindContentByParentParams;
import com.enonic.wem.api.content.FindContentByParentResult;
import com.enonic.wem.api.content.page.CreatePageTemplateParams;
import com.enonic.wem.api.content.page.GetDefaultPageTemplateParams;
import com.enonic.wem.api.content.page.PageService;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.page.PageTemplateSpec;
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
        // TODO: Make command
        final PageTemplates pageTemplates = doGetBySite( params.getSite(), context );
        final PageTemplateSpec spec = PageTemplateSpec.newPageTemplateParams().canRender( params.getContentType() ).build();
        final PageTemplates supportedTemplates = pageTemplates.filter( spec );
        return supportedTemplates.first();
    }

    public PageTemplates getBySite( final ContentId siteId, final Context context )
    {
        return doGetBySite( siteId, context );
    }

    private PageTemplates doGetBySite( final ContentId siteId, final Context context )
    {
        // TODO: Make command
        final PageTemplates.Builder pageTemplatesBuilder = PageTemplates.newPageTemplates();
        final Content site = contentService.getById( siteId, context );
        final ContentPath pageTemplatesFolderPath = ContentPath.from( site.getPath(), "templates" );
        final FindContentByParentResult result =
            contentService.findByParent( FindContentByParentParams.create().parentPath( pageTemplatesFolderPath ).build(), context );
        for ( final Content content : result.getContents() )
        {
            if ( content instanceof PageTemplate )
            {
                pageTemplatesBuilder.add( (PageTemplate) content );
            }
        }
        return pageTemplatesBuilder.build();
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
