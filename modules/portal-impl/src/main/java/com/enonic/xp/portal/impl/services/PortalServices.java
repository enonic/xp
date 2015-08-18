package com.enonic.xp.portal.impl.services;

import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.image.ImageFilterBuilder;
import com.enonic.xp.image.ImageScaleFunctionBuilder;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.portal.impl.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.rest.PortalRestServiceRegistry;
import com.enonic.xp.portal.rendering.RendererFactory;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.SiteService;

public interface PortalServices
{
    ApplicationService getApplicationService();

    ControllerScriptFactory getControllerScriptFactory();

    ContentService getContentService();

    ImageFilterBuilder getImageFilterBuilder();

    RendererFactory getRendererFactory();

    PageTemplateService getPageTemplateService();

    PageDescriptorService getPageDescriptorService();

    PortalUrlService getPortalUrlService();

    SiteService getSiteService();

    ImageScaleFunctionBuilder getImageScaleFunctionBuilder();

    ResourceService getResourceService();

    PortalRestServiceRegistry getPortalRestServiceRegistry();
}
