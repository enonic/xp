package com.enonic.wem.portal.internal.content;

import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.portal.internal.base.BaseResourceFactory;
import com.enonic.wem.portal.internal.controller.JsControllerFactory;

public final class PageTemplateResourceFactory
    extends BaseResourceFactory<PageTemplateResource>
{
    private JsControllerFactory controllerFactory;

    private SiteTemplateService siteTemplateService;

    private PageTemplateService pageTemplateService;

    private PageDescriptorService pageDescriptorService;

    public PageTemplateResourceFactory()
    {
        super( PageTemplateResource.class );
    }

    @Override
    protected void configure( final PageTemplateResource instance )
    {
        instance.controllerFactory = this.controllerFactory;
        instance.siteTemplateService = this.siteTemplateService;
        instance.pageTemplateService = this.pageTemplateService;
        instance.pageDescriptorService = this.pageDescriptorService;
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
