package com.enonic.xp.portal.impl.services;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.page.PageDescriptorService;
import com.enonic.xp.content.page.PageTemplateService;
import com.enonic.xp.image.ImageFilterBuilder;
import com.enonic.xp.image.ImageScaleFunctionBuilder;
import com.enonic.xp.module.ModuleService;
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

    public ImageScaleFunctionBuilder getImageScaleFunctionBuilder();
}
