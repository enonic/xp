package com.enonic.wem.portal.internal.rendering.page;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.portal.internal.controller.ControllerScript;
import com.enonic.wem.portal.internal.controller.ControllerScriptFactory;
import com.enonic.wem.portal.internal.controller.PortalResponseSerializer;
import com.enonic.wem.portal.internal.postprocess.PostProcessor;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.wem.portal.internal.rendering.Renderer;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;

import static com.enonic.wem.portal.internal.rendering.RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE;

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
            html += "<body " + PORTAL_COMPONENT_ATTRIBUTE + "=\"page\"></body>";
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
