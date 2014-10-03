package com.enonic.wem.portal.internal.content;

import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.portal.internal.controller.JsControllerFactory;
import com.enonic.wem.servlet.jaxrs.ResourceProvider;

public abstract class RenderBaseResourceProvider<T extends RenderBaseResource>
    implements ResourceProvider<T>
{
    private JsControllerFactory controllerFactory;

    private PageDescriptorService pageDescriptorService;

    private PageTemplateService pageTemplateService;

    private ContentService contentService;

    protected final void configure( final T instance )
    {
        instance.controllerFactory = this.controllerFactory;
        instance.pageDescriptorService = this.pageDescriptorService;
        instance.pageTemplateService = this.pageTemplateService;
        instance.contentService = this.contentService;
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

    public final void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

}
