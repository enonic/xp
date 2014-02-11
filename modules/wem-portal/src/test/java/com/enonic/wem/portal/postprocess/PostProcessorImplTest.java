package com.enonic.wem.portal.postprocess;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Charsets;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.rendering.Renderable;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsHttpResponse;
import com.enonic.wem.portal.rendering.Renderer;
import com.enonic.wem.portal.rendering.RendererFactory;

import static com.enonic.wem.api.content.page.PageRegions.newPageRegions;
import static com.enonic.wem.api.content.page.part.PartComponent.newPartComponent;
import static com.enonic.wem.api.content.page.region.Region.newRegion;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

public class PostProcessorImplTest
{

    @Test
    public void testPostProcessing()
        throws Exception
    {
        final String html = readResource( "postProcessSource.html" );

        final RendererFactory rendererFactory = newRendererFactory( "<b>part content</b>" );

        final PostProcessorImpl postProcessor = new PostProcessorImpl();
        postProcessor.rendererFactory = rendererFactory;

        final JsHttpResponse resp = new JsHttpResponse();
        resp.setPostProcess( true );
        resp.setBody( html );
        final JsContext context = new JsContext();
        context.setResponse( resp );
        Content content = createPage( "content-id", "content-name", "content-type" );
        context.setContent( content );
        Content siteContent = createSite( "site-id", "site-name", "content-type" );
        context.setSiteContent( siteContent );
        postProcessor.processResponse( context );

        final String outputHtml = resp.getBody().toString();
        final String expectedResult = readResource( "postProcessResult.html" );

        equalToIgnoringWhiteSpace( expectedResult, outputHtml );
    }

    public String readResource( final String resourceName )
        throws IOException
    {
        try
        {
            final byte[] bytes = Files.readAllBytes( new File( PostProcessorImplTest.class.getResource( resourceName ).toURI() ).toPath() );
            return new String( bytes, Charsets.UTF_8 );
        }
        catch ( URISyntaxException e )
        {
            throw new IllegalStateException( e );
        }
    }

    private Content createPage( final String id, final String name, final String contentTypeName )
    {
        final RootDataSet rootDataSet = new RootDataSet();
        final Property dataSet = new Property( "property1", new Value.String( "value1" ) );
        rootDataSet.add( dataSet );

        final Region region = newRegion().
            name( "myRegion" ).
            add( newPartComponent().
                name( "myPartComponent" ).
                descriptor( PartDescriptorKey.from( "mymodule-1.0.0:myparttemplate" ) ).
                build() ).
            build();

        final PageRegions pageRegions = newPageRegions().add( region ).build();
        Page page = Page.newPage().
            template( PageTemplateKey.from( "mymodule|my-page" ) ).
            regions( pageRegions ).
            build();

        return Content.newContent().
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifier( UserKey.superUser() ).
            type( ContentTypeName.from( contentTypeName ) ).
            page( page ).
            build();
    }

    private Content createSite( final String id, final String name, final String contentTypeName )
    {
        RootDataSet rootDataSet = new RootDataSet();

        Property dataSet = new Property( "property1", new Value.String( "value1" ) );
        rootDataSet.add( dataSet );

        Page page = Page.newPage().
            template( PageTemplateKey.from( "mymodule|my-page" ) ).
            config( rootDataSet ).
            build();

        Site site = Site.newSite().build();

        return Content.newContent().
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifier( UserKey.superUser() ).
            type( ContentTypeName.from( contentTypeName ) ).
            page( page ).
            site( site ).
            build();
    }

    private RendererFactory newRendererFactory( final String renderResult )
    {
        final RendererFactory rendererFactory = mock( RendererFactory.class );
        final Renderer renderer = new Renderer()
        {
            @Override
            public Response render( final Renderable component, final JsContext context )
            {
                return Response.ok( renderResult ).build();
            }
        };
        Mockito.when( rendererFactory.getRenderer( any( Renderable.class ) ) ).thenReturn( renderer );
        return rendererFactory;
    }

    private void equalToIgnoringWhiteSpace( final String expected, final String actual )
    {
        final String expectedWithoutSpaces = expected.replaceAll( "\\s+", "" );
        final String actualWithoutSpaces = actual.replaceAll( "\\s+", "" );
        if ( !expectedWithoutSpaces.equals( actualWithoutSpaces ) )
        {
            assertEquals( expected, actual );
        }
    }
}
