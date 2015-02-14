package com.enonic.xp.portal.impl.rendering;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.core.content.Content;
import com.enonic.xp.core.content.page.PageDescriptor;
import com.enonic.xp.portal.impl.controller.ControllerScript;
import com.enonic.xp.portal.impl.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.controller.PortalResponseSerializer;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.portal.rendering.RenderResult;
import com.enonic.xp.portal.rendering.Renderer;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;

@Component(immediate = true, service = Renderer.class)
public final class PageRenderer
    implements Renderer<Content>
{
    private ControllerScriptFactory controllerScriptFactory;

    private PostProcessor postProcessor;

    @Override
    public Class<Content> getType()
    {
        return Content.class;
    }

    @Override
    public RenderResult render( final Content content, final PortalContext context )
    {
        final PageDescriptor pageDescriptor = context.getPageDescriptor();

        if ( pageDescriptor != null )
        {
            final ControllerScript controllerScript = this.controllerScriptFactory.newController( pageDescriptor.getResourceKey() );
            controllerScript.execute( context );
        }
        else
        {
            renderForNoPageDescriptor( context, content );
        }

        return new PortalResponseSerializer( context.getResponse() ).serialize();
    }

    private void renderForNoPageDescriptor( final PortalContext context, final Content content )
    {
        String html = "<html>" +
            "<head>" +
            "<meta charset=\"utf-8\"/>" +
            "<title>" + content.getDisplayName() + "</title>" +
            "</head>";
        if ( RenderMode.EDIT.equals( context.getMode() ) )
        {
            html += "<body " + RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE + "=\"page\"></body>";
        }
        else
        {
            html += "<body></body>";
        }
        html += "</html>";

        final PortalResponse response = context.getResponse();
        response.setContentType( "text/html" );
        response.setStatus( 200 );
        response.setBody( html );
        response.setPostProcess( true );

        this.postProcessor.processResponse( context );
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
}
