package com.enonic.xp.portal.impl.services;

import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.image.ImageFilterBuilder;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.xp.portal.impl.controller.ControllerScriptFactory;
import com.enonic.xp.portal.rendering.RendererFactory;

public interface PortalServices
{
    public ModuleService getModuleService();

    public ControllerScriptFactory getControllerScriptFactory();

    public ContentService getContentService();

    public ImageFilterBuilder getImageFilterBuilder();

    public RendererFactory getRendererFactory();

    public PageTemplateService getPageTemplateService();

    public PageDescriptorService getPageDescriptorService();
}
