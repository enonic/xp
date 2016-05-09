package com.enonic.xp.portal.impl.handler.render;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.portal.PortalWebRequest;
import com.enonic.xp.portal.PortalWebResponse;
import com.enonic.xp.portal.rendering.RendererFactory;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.handler.WebRequest;
import com.enonic.xp.web.handler.WebResponse;

@Component(immediate = true, service = WebHandler.class)
public final class PageHandler
    extends BaseWebHandler
{
    private ContentService contentService;

    private RendererFactory rendererFactory;

    private PageDescriptorService pageDescriptorService;

    private PageTemplateService pageTemplateService;

    private PortalUrlService portalUrlService;

    public PageHandler()
    {
        super( 50 );
    }

    @Override
    public boolean canHandle( final WebRequest webRequest )
    {
        return webRequest instanceof PortalWebRequest;
    }


    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
    {
        return PageWebHandlerWorker.create().
            webRequest( (PortalWebRequest) webRequest ).
            webResponse( new PortalWebResponse() ).
            contentService( contentService ).
            rendererFactory( rendererFactory ).
            pageDescriptorService( pageDescriptorService ).
            pageTemplateService( pageTemplateService ).
            portalUrlService( portalUrlService ).
            build().
            execute();
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
