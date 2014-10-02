package com.enonic.wem.portal.internal.content;

import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.portal.internal.controller.JsControllerFactory;
import com.enonic.wem.servlet.jaxrs.ResourceProvider;

public final class PageTemplateResourceProvider
    implements ResourceProvider<PageTemplateResource>
{
    private JsControllerFactory controllerFactory;

    private SiteTemplateService siteTemplateService;

    private PageTemplateService pageTemplateService;

    private PageDescriptorService pageDescriptorService;

    @Override
    public Class<PageTemplateResource> getType()
    {
        return PageTemplateResource.class;
    }

    @Override
    public PageTemplateResource newResource()
    {
        final PageTemplateResource instance = new PageTemplateResource();
        instance.controllerFactory = this.controllerFactory;
        instance.siteTemplateService = this.siteTemplateService;
        instance.pageTemplateService = this.pageTemplateService;
        instance.pageDescriptorService = this.pageDescriptorService;
        return instance;
    }

    public void setControllerFactory( final JsControllerFactory controllerFactory )
    {
        this.controllerFactory = controllerFactory;
    }

    public void setSiteTemplateService( final SiteTemplateService siteTemplateService )
    {
        this.siteTemplateService = siteTemplateService;
    }

    public void setPageTemplateService( final PageTemplateService pageTemplateService )
    {
        this.pageTemplateService = pageTemplateService;
    }

    public void setPageDescriptorService( final PageDescriptorService pageDescriptorService )
    {
        this.pageDescriptorService = pageDescriptorService;
    }
}
