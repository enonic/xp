package com.enonic.xp.portal.impl.rendering;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.net.MediaType;

import com.enonic.xp.content.Content;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.filter.FilterChainResolver;
import com.enonic.xp.portal.impl.filter.FilterExecutor;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.portal.rendering.Renderer;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.site.filter.FilterDescriptor;
import com.enonic.xp.site.filter.FilterDescriptors;
import com.enonic.xp.web.HttpStatus;

import static com.enonic.xp.portal.RenderMode.EDIT;
import static com.enonic.xp.portal.RenderMode.PREVIEW;
import static com.enonic.xp.portal.impl.postprocess.instruction.ComponentInstruction.COMPONENT_INSTRUCTION_PREFIX;
import static com.enonic.xp.portal.impl.postprocess.instruction.ComponentInstruction.FRAGMENT_COMPONENT;
import static com.enonic.xp.portal.impl.rendering.RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE;

@Component(immediate = true, service = Renderer.class)
public final class PageRenderer
    implements Renderer<Content>
{
    private ControllerScriptFactory controllerScriptFactory;

    private PostProcessor postProcessor;

    private FilterExecutor filterExecutor;

    private FilterChainResolver filterChainResolver;

    @Override
    public Class<Content> getType()
    {
        return Content.class;
    }

    @Override
    public PortalResponse render( final Content content, final PortalRequest portalRequest )
    {
        final PageDescriptor pageDescriptor = portalRequest.getPageDescriptor();
        final RenderMode mode = portalRequest.getMode();
        PortalResponse portalResponse;
        if ( pageDescriptor != null )
        {
            final ControllerScript controllerScript = this.controllerScriptFactory.fromDir( pageDescriptor.getResourceKey() );
            portalResponse = controllerScript.execute( portalRequest );
        }
        else if ( portalRequest.getControllerScript() != null )
        {
            portalResponse = portalRequest.getControllerScript().execute( portalRequest );
        }
        else if ( ( mode == EDIT || mode == PREVIEW ) && portalRequest.getContent().getType().isFragment() )
        {
            portalResponse = renderDefaultFragmentPage( portalRequest, content );
        }
        else
        {
            portalResponse = renderForNoPageDescriptor( portalRequest, content );
        }

        portalResponse = this.postProcessor.processResponseInstructions( portalRequest, portalResponse );
        portalResponse = executeResponseFilters( portalRequest, portalResponse );
        portalResponse = this.postProcessor.processResponseContributions( portalRequest, portalResponse );
        return portalResponse;
    }

    private PortalResponse renderDefaultFragmentPage( final PortalRequest portalRequest, final Content content )
    {
        String html = "<html>" +
            "<head>" +
            "<meta charset=\"utf-8\"/><title>" + content.getDisplayName() + "</title>" +
            "</head>";
        if ( portalRequest.getMode() == EDIT )
        {
            html += "<body " + PORTAL_COMPONENT_ATTRIBUTE + "=\"page\">";
        }
        else
        {
            html += "<body>";
        }
        html += "<!--#" + COMPONENT_INSTRUCTION_PREFIX + FRAGMENT_COMPONENT + "-->";
        html += "</body></html>";

        return PortalResponse.create().
            status( HttpStatus.OK ).
            contentType( MediaType.HTML_UTF_8 ).
            body( html ).
            postProcess( true ).
            build();
    }

    private PortalResponse renderForNoPageDescriptor( final PortalRequest portalRequest, final Content content )
    {
        String html = "<html>" +
            "<head>" +
            "<meta charset=\"utf-8\"/>" +
            "<title>" + content.getDisplayName() + "</title>" +
            "</head>";
        if ( portalRequest.getMode() == EDIT )
        {
            html += "<body " + PORTAL_COMPONENT_ATTRIBUTE + "=\"page\"></body>";
        }
        else
        {
            html += "<body></body>";
        }
        html += "</html>";

        return PortalResponse.create().
            status( HttpStatus.OK ).
            contentType( MediaType.HTML_UTF_8 ).
            body( html ).
            postProcess( true ).
            build();
    }

    private PortalResponse executeResponseFilters( final PortalRequest portalRequest, final PortalResponse portalResponse )
    {
        final FilterDescriptors filters = this.filterChainResolver.resolve( portalRequest );
        if ( !portalResponse.applyFilters() || filters.isEmpty() )
        {
            return portalResponse;
        }

        PortalResponse filterResponse = portalResponse;
        for ( FilterDescriptor filter : filters )
        {
            filterResponse = this.filterExecutor.executeResponseFilter( filter, portalRequest, filterResponse );
            if ( !filterResponse.applyFilters() )
            {
                break;
            }
        }

        return filterResponse;
    }

    @Reference
    public void setControllerScriptFactory( final ControllerScriptFactory controllerScriptFactory )
    {
        this.controllerScriptFactory = controllerScriptFactory;
    }

    @Reference
    public void setPostProcessor( final PostProcessor postProcessor )
    {
        this.postProcessor = postProcessor;
    }

    @Reference
    public void setScriptService( final PortalScriptService scriptService )
    {
        this.filterExecutor = new FilterExecutor( scriptService );
    }


    @Reference
    public void setFilterChainResolver( final FilterChainResolver filterChainResolver )
    {
        this.filterChainResolver = filterChainResolver;
    }
}
