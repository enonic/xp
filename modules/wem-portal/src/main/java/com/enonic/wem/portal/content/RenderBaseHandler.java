package com.enonic.wem.portal.content;

import javax.inject.Inject;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.site.SiteService;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.api.rendering.RenderingMode;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.portal.base.BaseHandler;
import com.enonic.wem.portal.controller.JsControllerFactory;

public abstract class RenderBaseHandler
    extends BaseHandler
{
    @Inject
    protected ContentService contentService;

    @Inject
    protected SiteService siteService;

    @Inject
    protected SiteTemplateService siteTemplateService;

    @Inject
    protected PageTemplateService pageTemplateService;

    @Inject
    protected PageDescriptorService pageDescriptorService;

    @Inject
    protected JsControllerFactory controllerFactory;

    protected final Content getContent( final RenderingMode mode, final String contentSelector )
    {
        final boolean inEditMode = ( mode == RenderingMode.EDIT );
        if ( inEditMode )
        {
            final ContentId contentId = ContentId.from( contentSelector );
            final Content contentById = getContentById( contentId );
            if ( contentById != null )
            {
                return contentById;
            }

            throw notFound();
        }
        else
        {
            final ContentPath contentPath = ContentPath.from( contentSelector );
            final Content content = getContentByPath( contentPath );
            if ( content != null )
            {
                return content;
            }

            throw notFound();
        }
    }

    private Content getContentByPath( final ContentPath contentPath )
    {
        return this.contentService.getByPath( contentPath, ContentConstants.DEFAULT_CONTEXT );
    }

    private Content getContentById( final ContentId contentId )
    {
        return this.contentService.getById( contentId, ContentConstants.DEFAULT_CONTEXT );
    }

    protected final Content getSite( final Content content )
    {
        final Content siteContent = this.siteService.getNearestSite( content.getId() );
        if ( siteContent == null )
        {
            throw notFound();
        }

        return siteContent;
    }

    protected final PageTemplate getDefaultPageTemplate( final ContentTypeName contentType, final Site site )
    {
        final SiteTemplate siteTemplate = this.siteTemplateService.getSiteTemplate( site.getTemplate() );
        return siteTemplate.getDefaultPageTemplate( contentType );
    }

    protected final Page getPage( final Content content )
    {
        if ( !content.isPage() )
        {
            throw notFound();
        }

        return content.getPage();
    }

    protected PageTemplate getPageTemplate( final Page page, final Site site )
    {
        final PageTemplate pageTemplate = this.pageTemplateService.getByKey( page.getTemplate(), site.getTemplate() );
        if ( pageTemplate == null )
        {
            throw notFound();
        }

        return pageTemplate;
    }

    protected final PageDescriptor getPageDescriptor( final PageTemplate pageTemplate )
    {
        final PageDescriptorKey descriptorKey = pageTemplate.getDescriptor();
        final PageDescriptor pageDescriptor = this.pageDescriptorService.getByKey( descriptorKey );
        if ( pageDescriptor == null )
        {
            throw notFound();
        }

        return pageDescriptor;
    }
}
