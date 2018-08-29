package com.enonic.xp.portal.impl.handler.render;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.handler.EndpointHandler;
import com.enonic.xp.portal.handler.WebHandlerHelper;
import com.enonic.xp.portal.impl.rendering.RendererFactory;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class)
public final class ComponentHandler
    extends EndpointHandler
{
    private ContentService contentService;

    private RendererFactory rendererFactory;

    private PageDescriptorService pageDescriptorService;

    private PageTemplateService pageTemplateService;

    protected PostProcessor postProcessor;

    public ComponentHandler()
    {
        super( "component" );
    }

    @Override
    public boolean canHandle( final WebRequest webRequest )
    {
        return super.canHandle( webRequest ) && isPortalBase( webRequest );
    }

    @Override
    protected PortalResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        WebHandlerHelper.checkAdminAccess( webRequest );

        final String restPath = findRestPath( webRequest );

        final ComponentHandlerWorker worker = new ComponentHandlerWorker( (PortalRequest) webRequest );
        worker.componentPath = ComponentPath.from( restPath );
        worker.setContentService( this.contentService );
        worker.rendererFactory = rendererFactory;
        worker.pageDescriptorService = pageDescriptorService;
        worker.pageTemplateService = pageTemplateService;
        worker.postProcessor = postProcessor;
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
    public void setPostProcessor( final PostProcessor postProcessor )
    {
        this.postProcessor = postProcessor;
    }
}
