package com.enonic.xp.portal.impl.handler.render;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.handler.BaseHandler;
import com.enonic.xp.portal.handler.PortalHandler;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.portal.rendering.RendererFactory;
import com.enonic.xp.portal.url.PortalUrlService;

@Component(immediate = true, service = PortalHandler.class)
public final class PageHandler
    extends BaseHandler
{
    private ContentService contentService;

    private RendererFactory rendererFactory;

    private PageDescriptorService pageDescriptorService;

    private PageTemplateService pageTemplateService;

    private PortalUrlService portalUrlService;

    public PageHandler()
    {
        super( 100 );
    }

    @Override
    public boolean canHandle( final PortalRequest req )
    {
        return true;
    }

    @Override
    protected PortalHandlerWorker newWorker( final PortalRequest req )
        throws Exception
    {
        final PageHandlerWorker worker = new PageHandlerWorker();
        worker.setContentService( this.contentService );
        worker.rendererFactory = rendererFactory;
        worker.pageDescriptorService = pageDescriptorService;
        worker.pageTemplateService = pageTemplateService;
        worker.portalUrlService = portalUrlService;

        return worker;
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setRendererFactory( final RendererFactory rendererFactory )
    {
        this.rendererFactory = rendererFactory;
    }

    @Reference
    public void setPageDescriptorService( final PageDescriptorService pageDescriptorService )
    {
        this.pageDescriptorService = pageDescriptorService;
    }

    @Reference
    public void setPageTemplateService( final PageTemplateService pageTemplateService )
    {
        this.pageTemplateService = pageTemplateService;
    }

    @Reference
    public void setPortalUrlService( final PortalUrlService portalUrlService )
    {
        this.portalUrlService = portalUrlService;
    }
}
