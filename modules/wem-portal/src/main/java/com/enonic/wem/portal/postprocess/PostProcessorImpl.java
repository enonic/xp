package com.enonic.wem.portal.postprocess;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import com.google.gson.Gson;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.ComponentPath;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsHttpResponse;
import com.enonic.wem.portal.rendering.RenderException;
import com.enonic.wem.portal.rendering.Renderer;
import com.enonic.wem.portal.rendering.RendererFactory;

public final class PostProcessorImpl
    implements PostProcessor
{

    protected static final String WEM_COMPONENT_ATTRIBUTE = "wem:component";

    @Inject
    protected RendererFactory rendererFactory;

    public PostProcessorImpl()
    {
    }

    @Override
    public void processResponse( final JsContext context )
    {
        final JsHttpResponse response = context.getResponse();
        if ( !response.isPostProcess() )
        {
            return;
        }
        if ( !( response.getBody() instanceof String ) )
        {
            return;
        }
        final String responseBody = (String) response.getBody();
        final Document htmlDoc = parseHtmlFragment( responseBody );

        boolean updated = false;
        while ( processDocument( htmlDoc, context ) )
        {
            updated = true;
        }

        if ( updated )
        {
            htmlDoc.outputSettings().prettyPrint( false );
            final String processedHtml = htmlDoc.html();
            response.setBody( processedHtml );
        }
    }

    private boolean processDocument( final Document htmlDoc, final JsContext context )
    {
        boolean updated = false;
        final Elements elements = htmlDoc.getElementsByAttribute( WEM_COMPONENT_ATTRIBUTE );
        for ( Element element : elements )
        {
            processComponentElement( element, context );
            updated = true;
        }
        return updated;
    }

    private void processComponentElement( final Element element, final JsContext context )
    {
        final String componentPathAsString = element.attr( WEM_COMPONENT_ATTRIBUTE );
        final ComponentPath componentPath = ComponentPath.from( componentPathAsString );

        final PageComponent component = resolveComponent( context, componentPath );

        final Renderer renderer = rendererFactory.getRenderer( component );
        final Response componentResult = renderer.render( component, context );

        final String componentBody = serializeResponseBody( componentResult );
        final Document componentDoc = parseHtmlFragment( componentBody );
        replaceComponentElement( element, componentDoc );
    }

    private void replaceComponentElement( final Element targetElement, final Element sourceParentElement )
    {
        if ( sourceParentElement.childNodes().isEmpty() )
        {
            targetElement.text( sourceParentElement.ownText() ).unwrap();
        }
        else
        {
            for ( Node el : sourceParentElement.childNodes() )
            {
                targetElement.appendChild( el.clone() );
            }
            targetElement.unwrap();
        }
    }

    private Document parseHtmlFragment( final String html )
    {
        final List<Node> nodes = Parser.parseXmlFragment( html, "" );
        final Document doc = new Document( "" );
        for ( Node node : nodes )
        {
            doc.appendChild( node.clone() );
        }
        return doc;
    }

    private PageComponent resolveComponent( final JsContext context, final ComponentPath componentPath )
    {
        final Content content = context.getContent();
        if ( content == null || content.getPage() == null )
        {
            return null;
        }
        final Page page = content.getPage();
        final PageRegions pageRegions = resolvePageRegions( page, context.getPageTemplate() );

        PageComponent component = pageRegions.getComponent( componentPath );
        if ( component == null )
        {
            // TODO: Hack: See if component still exist in page template
            component = context.getPageTemplate().getRegions().getComponent( componentPath );
            if ( component == null )
            {
                throw new RenderException( "Component not found: [{0}]", componentPath );
            }
        }
        return component;
    }

    private PageRegions resolvePageRegions( final Page page, final PageTemplate pageTemplate )
    {
        if ( page.hasRegions() )
        {
            return page.getRegions();
        }
        else
        {
            return pageTemplate.getRegions();
        }
    }

    private String serializeResponseBody( final Response response )
    {
        final Object value = response.getEntity();
        if ( value == null )
        {
            return null;
        }
        if ( value instanceof String )
        {
            return (String) value;
        }
        if ( value instanceof byte[] )
        {
            return new String( (byte[]) value );
        }
        if ( value instanceof Map )
        {
            return new Gson().toJson( value );
        }
        return value.toString();
    }

    public void setRendererFactory( final RendererFactory rendererFactory )
    {
        this.rendererFactory = rendererFactory;
    }
}
