package com.enonic.xp.portal.impl.handler.render;

import com.enonic.xp.content.Content;
import com.enonic.xp.page.GetDefaultPageTemplateParams;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.handler.ControllerHandlerWorker;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.site.Site;

abstract class RenderHandlerWorker
    extends ControllerHandlerWorker
{
    protected PageTemplateService pageTemplateService;

    protected PageDescriptorService pageDescriptorService;

    public RenderHandlerWorker( final PortalRequest request )
    {
        super( request );
    }

    protected final Page getPage( final Content content )
    {
        if ( !content.hasPage() )
        {
            throw notFound( "Content [%s] is not a page", content.getPath().toString() );
        }

        return content.getPage();
    }

    protected final PageTemplate getPageTemplate( final Page page )
    {
        if ( page.getTemplate() == null )
        {
            throw notFound( "No template set for content" );
        }

        final PageTemplate pageTemplate = this.pageTemplateService.getByKey( page.getTemplate() );
        if ( pageTemplate == null )
        {
            throw notFound( "Page template [%s] not found", page.getTemplate() );
        }

        return pageTemplate;
    }

    protected final PageTemplate getDefaultPageTemplate( final ContentTypeName contentType, final Site site )
    {
        final GetDefaultPageTemplateParams getDefPageTemplate = GetDefaultPageTemplateParams.create().
            sitePath( site.getPath() ).
            contentType( contentType ).
            build();

        final PageTemplate pageTemplate = this.pageTemplateService.getDefault( getDefPageTemplate );
        if ( pageTemplate == null && ( this.request.getMode() != RenderMode.EDIT ) )
        {
            // we can render default empty page in Live-Edit, for selecting controller when page customized
            throw notFound( "No template found for content" );
        }

        return pageTemplate;
    }

    protected final PageDescriptor getPageDescriptor( final PageTemplate pageTemplate )
    {
        final PageDescriptor pageDescriptor = this.pageDescriptorService.getByKey( pageTemplate.getController() );
        if ( pageDescriptor == null )
        {
            throw notFound( "Page descriptor for template [%s] not found", pageTemplate.getName() );
        }

        return pageDescriptor;
    }
}
