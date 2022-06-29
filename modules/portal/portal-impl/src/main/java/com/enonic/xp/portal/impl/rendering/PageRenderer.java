package com.enonic.xp.portal.impl.rendering;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.net.MediaType;

import com.enonic.xp.content.Content;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.processor.ProcessorChainResolver;
import com.enonic.xp.portal.impl.processor.ResponseProcessorExecutor;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.web.HttpStatus;

import static com.enonic.xp.portal.RenderMode.EDIT;
import static com.enonic.xp.portal.RenderMode.INLINE;
import static com.enonic.xp.portal.RenderMode.PREVIEW;
import static com.enonic.xp.portal.impl.postprocess.instruction.ComponentInstruction.COMPONENT_INSTRUCTION_PREFIX;
import static com.enonic.xp.portal.impl.postprocess.instruction.ComponentInstruction.FRAGMENT_COMPONENT;
import static com.enonic.xp.portal.impl.rendering.RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE;

@Component(immediate = true, service = Renderer.class)
public final class PageRenderer
    extends PostProcessingRenderer<Content>
{
    private final ControllerScriptFactory controllerScriptFactory;

    private final PortalScriptService portalScriptService;

    @Activate
    public PageRenderer( @Reference final PostProcessor postProcessor, @Reference final PortalScriptService portalScriptService,
                         @Reference final ProcessorChainResolver processorChainResolver,
                         @Reference final ControllerScriptFactory controllerScriptFactory )
    {
        super( postProcessor, new ResponseProcessorExecutor( portalScriptService ), processorChainResolver );
        this.portalScriptService = portalScriptService;
        this.controllerScriptFactory = controllerScriptFactory;
    }

    @Override
    public Class<Content> getType()
    {
        return Content.class;
    }

    @Override
    public PortalResponse doRender( final Content content, final PortalRequest portalRequest )
    {
        final PageDescriptor descriptor = portalRequest.getPageDescriptor();

        if ( descriptor != null )
        {
            final ResourceKey script = descriptor.getComponentPath().resolve( descriptor.getComponentPath().getName() + ".js" );
            if ( portalScriptService.hasScript( script ) )
            {
                return controllerScriptFactory.fromScript( script ).execute( portalRequest );
            }
        }

        final RenderMode mode = portalRequest.getMode();

        if ( ( mode == EDIT || mode == PREVIEW || mode == INLINE ) && portalRequest.getContent().getType().isFragment() )
        {
            return renderDefaultFragmentPage( mode, content );
        }
        else
        {
            return renderForNoPageDescriptor( mode, content );
        }
    }

    private PortalResponse renderDefaultFragmentPage( final RenderMode mode, final Content content )
    {
        String html = "<!DOCTYPE html>" + "<html>" + "<head>" + "<meta charset=\"utf-8\"/><title>" + content.getDisplayName() + "</title>" +
            "</head>";
        if ( mode == EDIT )
        {
            html += "<body " + PORTAL_COMPONENT_ATTRIBUTE + "=\"page\">";
        }
        else
        {
            html += "<body>";
        }
        html += "<!--#" + COMPONENT_INSTRUCTION_PREFIX + " " + FRAGMENT_COMPONENT + "-->";
        html += "</body></html>";

        return PortalResponse.create().status( HttpStatus.OK ).contentType( MediaType.HTML_UTF_8 ).body( html ).postProcess( true ).build();
    }

    private PortalResponse renderForNoPageDescriptor( final RenderMode mode, final Content content )
    {
        String html = "<html>" + "<head>" + "<meta charset=\"utf-8\"/>" + "<title>" + content.getDisplayName() + "</title>" + "</head>";
        if ( mode == EDIT )
        {
            html += "<body " + PORTAL_COMPONENT_ATTRIBUTE + "=\"page\"></body>";
        }
        else
        {
            html += "<body></body>";
        }
        html += "</html>";

        HttpStatus status = ( mode == INLINE || mode == PREVIEW ) ? HttpStatus.IM_A_TEAPOT : HttpStatus.OK;
        return PortalResponse.create().status( status ).contentType( MediaType.HTML_UTF_8 ).body( html ).postProcess( true ).build();
    }
}
