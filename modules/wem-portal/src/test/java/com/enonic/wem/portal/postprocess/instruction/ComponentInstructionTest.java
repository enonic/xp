package com.enonic.wem.portal.postprocess.instruction;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageComponentService;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleKey;
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
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ComponentInstructionTest
{
    @Test
    public void testInstruction()
        throws Exception
    {
        final RendererFactory rendererFactory = newRendererFactory( "<b>part content</b>" );
        final PageComponentService pageComponentService = Mockito.mock( PageComponentService.class );
        final ComponentInstruction instruction = new ComponentInstruction( rendererFactory, pageComponentService );

        final JsHttpResponse resp = new JsHttpResponse();
        resp.setPostProcess( true );
        final JsContext context = new JsContext();
        context.setResponse( resp );
        Content content = createPage( "content-id", "content-name", "content-type" );
        context.setContent( content );
        Content siteContent = createSite( "site-id", "site-name", "content-type" );
        context.setSiteContent( siteContent );

        final String outputHtml = instruction.evaluate( context, "COMPONENT myRegion/myPartComponent" );
        assertEquals( "<b>part content</b>", outputHtml );
    }

    @Test
    public void testInstructionRenderByName()
        throws Exception
    {
        final RendererFactory rendererFactory = newRendererFactory( "<b>part content</b>" );
        final PageComponentService pageComponentService = Mockito.mock( PageComponentService.class );

        final PageComponent<? extends DescriptorKey> component = createPartComponent();
        doReturn( component ).when( pageComponentService ).getByName( isA( ModuleKey.class ), isA( ComponentName.class ) );
        final ComponentInstruction instruction = new ComponentInstruction( rendererFactory, pageComponentService );

        final JsHttpResponse resp = new JsHttpResponse();
        resp.setPostProcess( true );
        final JsContext context = new JsContext();
        context.setResponse( resp );
        Content content = createPage( "content-id", "content-name", "content-type" );
        context.setContent( content );
        Content siteContent = createSite( "site-id", "site-name", "content-type" );
        context.setSiteContent( siteContent );
        PageTemplate pageTemplate = createPageTemplate();
        context.setPageTemplate( pageTemplate );

        final String outputHtml = instruction.evaluate( context, "COMPONENT module:myPartComponent" );
        assertEquals( "<b>part content</b>", outputHtml );
    }

    private PageTemplate createPageTemplate()
    {
        return PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "mymodule|my-page" ) ).
            descriptor( PageDescriptorKey.from( "mymodule-1.0.0:mypagetemplate" ) ).
            build();
    }

    private PageComponent<? extends DescriptorKey> createPartComponent()
    {
        return newPartComponent().
            name( "myPartComponent" ).
            descriptor( PartDescriptorKey.from( "mymodule-1.0.0:myparttemplate" ) ).
            build();
    }

    private Content createPage( final String id, final String name, final String contentTypeName )
    {
        final RootDataSet rootDataSet = new RootDataSet();
        final Property dataSet = new Property( "property1", Value.newString( "value1" ) );
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

        Property dataSet = new Property( "property1", Value.newString( "value1" ) );
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
        final Renderer<Renderable> renderer = ( component, context ) -> Response.ok( renderResult ).build();
        when( rendererFactory.getRenderer( isA( Renderable.class ) ) ).thenReturn( renderer );
        return rendererFactory;
    }
}
