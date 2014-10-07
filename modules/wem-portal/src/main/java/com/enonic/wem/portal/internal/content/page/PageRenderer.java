package com.enonic.wem.portal.internal.content.page;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.portal.PortalResponse;
import com.enonic.wem.portal.RenderingMode;
import com.enonic.wem.portal.internal.controller.Controller;
import com.enonic.wem.portal.internal.controller.ControllerFactory;
import com.enonic.wem.portal.internal.controller.PortalResponseSerializer;
import com.enonic.wem.portal.internal.postprocess.PostProcessor;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.wem.portal.internal.rendering.Renderer;

public class PageRenderer
    implements Renderer<Content, PageRendererContext>
{

    private ControllerFactory controllerFactory;

    private PostProcessor postProcessor;

    @Override
    public Class<Content> getType()
    {
        return Content.class;
    }

    @Override
    public RenderResult render( final Content content, final PageRendererContext context )
    {
        final PageDescriptor pageDescriptor = context.getPageDescriptor();

        if ( pageDescriptor != null )
        {
            final Controller controller = this.controllerFactory.newController( pageDescriptor.getResourceKey() );
            controller.execute( context );
        }
        else
        {
            renderForNoPageDescriptor( context, content );
        }

        return new PortalResponseSerializer( context.getResponse() ).serialize();
    }

    private void renderForNoPageDescriptor( final PageRendererContext context, final Content content )
    {
        String html = "<html>" +
            "<head>" +
            "<meta charset=\"utf-8\"/>" +
            "<title>" + content.getDisplayName() + "</title>" +
            "</head>";
        if ( RenderingMode.EDIT.equals( context.getMode() ) )
        {
            html += "<body data-live-edit-type=\"page\"></body>";
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

    public void setControllerFactory( final ControllerFactory controllerFactory )
    {
        this.controllerFactory = controllerFactory;
    }

    public void setPostProcessor( final PostProcessor postProcessor )
    {
        this.postProcessor = postProcessor;
    }
}
