package com.enonic.wem.portal.internal.content;

import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.site.SiteService;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.portal.internal.controller.JsControllerFactory;
import com.enonic.wem.servlet.jaxrs.ResourceProvider;

public abstract class RenderBaseResourceProvider<T extends RenderBaseResource>
    implements ResourceProvider<T>
{
    private JsControllerFactory controllerFactory;

    private PageDescriptorService pageDescriptorService;

    private PageTemplateService pageTemplateService;

    private SiteTemplateService siteTemplateService;

    private ContentService contentService;

    private SiteService siteService;

    protected final void configure( final T instance )
    {
        instance.controllerFactory = this.controllerFactory;
        instance.pageDescriptorService = this.pageDescriptorService;
        instance.pageTemplateService = this.pageTemplateService;
        instance.siteTemplateService = this.siteTemplateService;
        instance.contentService = this.contentService;
        instance.siteService = this.siteService;
    }

    public final void setControllerFactory( final JsControllerFactory controllerFactory )
    {
        this.controllerFactory = controllerFactory;
    }

    public final void setPageDescriptorService( final PageDescriptorService pageDescriptorService )
    {
        this.pageDescriptorService = pageDescriptorService;
    }

    public final void setPageTemplateService( final PageTemplateService pageTemplateService )
    {
        this.pageTemplateService = pageTemplateService;
    }

    public final void setSiteTemplateService( final SiteTemplateService siteTemplateService )
    {
        this.siteTemplateService = siteTemplateService;
    }

    public final void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    public final void setSiteService( final SiteService siteService )
    {
        this.siteService = siteService;
    }
}
