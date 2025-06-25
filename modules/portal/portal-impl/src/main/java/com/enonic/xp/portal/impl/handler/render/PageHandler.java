package com.enonic.xp.portal.impl.handler.render;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.handler.WebHandlerHelper;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.impl.rendering.RendererDelegate;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class, configurationPid = "com.enonic.xp.portal")
public final class PageHandler
    extends BaseWebHandler
{
    private ContentService contentService;

    private ProjectService projectService;

    private RendererDelegate rendererDelegate;

    private PageDescriptorService pageDescriptorService;

    private LayoutDescriptorService layoutDescriptorService;

    private PageTemplateService pageTemplateService;

    private PortalUrlService portalUrlService;

    public PageHandler()
    {
        super( 50 );
    }

    @Override
    public boolean canHandle( final WebRequest webRequest )
    {
        return webRequest instanceof PortalRequest && PortalRequestHelper.isSiteBase( webRequest );
    }

    @Override
    protected PortalResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        WebHandlerHelper.checkAdminAccess( webRequest );

        final PageHandlerWorker worker = new PageHandlerWorker( (PortalRequest) webRequest );
        worker.contentResolver = new ContentResolver( contentService, projectService );
        worker.rendererDelegate = rendererDelegate;
        worker.pageDescriptorService = pageDescriptorService;
        worker.pageResolver = new PageResolver( pageTemplateService, pageDescriptorService, layoutDescriptorService );
        worker.portalUrlService = portalUrlService;
        final Trace trace = Tracer.newTrace( "renderComponent" );
        if ( trace == null )
        {
            return worker.execute();
        }
        return Tracer.traceEx( trace, worker::execute );
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setProjectService( final ProjectService projectService )
    {
        this.projectService = projectService;
    }

    @Reference
    public void setRendererDelegate( final RendererDelegate rendererDelegate )
    {
        this.rendererDelegate = rendererDelegate;
    }

    @Reference
    public void setPageDescriptorService( final PageDescriptorService pageDescriptorService )
    {
        this.pageDescriptorService = pageDescriptorService;
    }

    @Reference
    public void setLayoutDescriptorService( final LayoutDescriptorService layoutDescriptorService )
    {
        this.layoutDescriptorService = layoutDescriptorService;
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
