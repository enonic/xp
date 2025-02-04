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
import com.enonic.xp.portal.impl.html.HtmlBuilder;
import com.enonic.xp.portal.impl.processor.ProcessorChainResolver;
import com.enonic.xp.portal.impl.processor.ResponseProcessorExecutor;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.web.HttpStatus;

import static com.enonic.xp.portal.RenderMode.EDIT;
import static com.enonic.xp.portal.RenderMode.INLINE;
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

        if ( ( mode == INLINE || mode == EDIT ) && portalRequest.getContent().getType().isFragment() )
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
        final HtmlBuilder html = new HtmlBuilder();
        html.text( "<!DOCTYPE html>" );
        html.open( "html" );

        html.open( "head" );
        html.open( "title" ).escapedText( content.getDisplayName() ).close();
        html.close();

        html.open( "body" );
        if ( mode == EDIT )
        {
            html.attribute( PORTAL_COMPONENT_ATTRIBUTE, "page" );
        }
        html.text( "<!--#" + COMPONENT_INSTRUCTION_PREFIX + " " + FRAGMENT_COMPONENT + "-->" );
        html.close();

        html.close();

        return PortalResponse.create()
            .status( HttpStatus.OK )
            .contentType( MediaType.HTML_UTF_8 )
            .body( html.toString() )
            .postProcess( true )
            .build();
    }

    private PortalResponse renderForNoPageDescriptor( final RenderMode mode, final Content content )
    {
        final HtmlBuilder html = new HtmlBuilder();

        html.open( "html" );

        html.open( "head" );
        html.open( "title" ).escapedText( content.getDisplayName() ).close();
        html.close();

        if ( mode == EDIT )
        {
            html.open( "body" ).attribute( PORTAL_COMPONENT_ATTRIBUTE, "page" ).text( "" ).close();
        }
        else
        {
            html.open( "body" ).text( "" ).close();
        }

        html.close();

        final HttpStatus status = mode == INLINE || mode == EDIT ? HttpStatus.IM_A_TEAPOT : HttpStatus.SERVICE_UNAVAILABLE;

        return PortalResponse.create()
            .status( status )
            .contentType( MediaType.HTML_UTF_8 )
            .body( html.toString() )
            .postProcess( true )
            .build();
    }
}
