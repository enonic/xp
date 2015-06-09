package com.enonic.xp.portal.impl.services;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.image.ImageFilterBuilder;
import com.enonic.xp.image.ImageScaleFunctionBuilder;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.portal.impl.controller.ControllerScriptFactory;
import com.enonic.xp.portal.rendering.RendererFactory;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.site.SiteService;

@Component
public final class PortalServicesImpl
    implements PortalServices
{
    private ModuleService moduleService;

    private ControllerScriptFactory controllerScriptFactory;

    private ContentService contentService;

    private ImageFilterBuilder imageFilterBuilder;

    private RendererFactory rendererFactory;

    private PageTemplateService pageTemplateService;

    private PageDescriptorService pageDescriptorService;

    private ImageScaleFunctionBuilder imageScaleFunctionBuilder;

    private PortalUrlService portalUrlService;

    private SiteService siteService;

    @Override
    public ModuleService getModuleService()
    {
        return this.moduleService;
    }

    @Override
    public ControllerScriptFactory getControllerScriptFactory()
    {
        return this.controllerScriptFactory;
    }

    @Override
    public ContentService getContentService()
    {
        return this.contentService;
    }

    @Override
    public ImageFilterBuilder getImageFilterBuilder()
    {
        return this.imageFilterBuilder;
    }

    @Override
    public RendererFactory getRendererFactory()
    {
        return this.rendererFactory;
    }

    @Override
    public PageTemplateService getPageTemplateService()
    {
        return this.pageTemplateService;
    }

    @Override
    public PageDescriptorService getPageDescriptorService()
    {
        return this.pageDescriptorService;
    }

    @Override
    public ImageScaleFunctionBuilder getImageScaleFunctionBuilder()
    {
        return this.imageScaleFunctionBuilder;
    }

    ;

    @Override
    public PortalUrlService getPortalUrlService()
    {
        return this.portalUrlService;
    }

    @Override
    public SiteService getSiteService()
    {
        return this.siteService;
    }

    @Reference
    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }

    @Reference
    public void setControllerScriptFactory( final ControllerScriptFactory controllerScriptFactory )
    {
        this.controllerScriptFactory = controllerScriptFactory;
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setImageFilterBuilder( final ImageFilterBuilder imageFilterBuilder )
    {
        this.imageFilterBuilder = imageFilterBuilder;
    }

    @Reference
    public void setRendererFactory( final RendererFactory rendererFactory )
    {
        this.rendererFactory = rendererFactory;
    }

    @Reference
    public void setPageTemplateService( final PageTemplateService pageTemplateService )
    {
        this.pageTemplateService = pageTemplateService;
    }

    @Reference
    public void setPageDescriptorService( final PageDescriptorService pageDescriptorService )
    {
        this.pageDescriptorService = pageDescriptorService;
    }

    @Reference
    public void setImageScaleFunctionBuilder( final ImageScaleFunctionBuilder imageScaleFunctionBuilder )
    {
        this.imageScaleFunctionBuilder = imageScaleFunctionBuilder;
    }

    @Reference
    public void setPortalUrlService( final PortalUrlService portalUrlService )
    {
        this.portalUrlService = portalUrlService;
    }

    @Reference
    public void setSiteService( final SiteService siteService )
    {
        this.siteService = siteService;
    }
}
