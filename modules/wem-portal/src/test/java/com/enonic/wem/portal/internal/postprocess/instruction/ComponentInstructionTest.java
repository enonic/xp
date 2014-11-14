package com.enonic.wem.portal.internal.postprocess.instruction;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.page.ComponentName;
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
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.portal.PortalContext;
import com.enonic.wem.portal.internal.controller.PortalContextImpl;
import com.enonic.wem.portal.internal.controller.PortalResponseImpl;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.wem.portal.internal.rendering.Renderer;
import com.enonic.wem.portal.internal.rendering.RendererFactory;

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
        RendererFactory rendererFactory = newRendererFactory( "<b>part content</b>" );
        PageComponentService pageComponentService = Mockito.mock( PageComponentService.class );
        ComponentInstruction instruction = new ComponentInstruction( rendererFactory, pageComponentService );

        PortalResponseImpl resp = new PortalResponseImpl();
        resp.setPostProcess( true );
        PortalContextImpl context = new PortalContextImpl();
        context.setResponse( resp );
        Content content = createPage( "content-id", "content-name", "mymodule:content-type" );
        context.setContent( content );
        Site site = createSite( "site-id", "site-name", "mymodule:content-type" );
        context.setSite( site );

        String outputHtml = instruction.evaluate( context, "COMPONENT myRegion/0" );
        assertEquals( "<b>part content</b>", outputHtml );
    }

    @Test
    public void testInstructionRenderByName()
        throws Exception
    {
        RendererFactory rendererFactory = newRendererFactory( "<b>part content</b>" );
        PageComponentService pageComponentService = Mockito.mock( PageComponentService.class );

        PageComponent component = createPartComponent();
        doReturn( component ).when( pageComponentService ).getByName( isA( ModuleKey.class ), isA( ComponentName.class ) );
        ComponentInstruction instruction = new ComponentInstruction( rendererFactory, pageComponentService );

        PortalResponseImpl resp = new PortalResponseImpl();
        resp.setPostProcess( true );
        PortalContextImpl context = new PortalContextImpl();
        context.setResponse( resp );
        Content content = createPage( "content-id", "content-name", "mymodule:content-type" );
        context.setContent( content );
        Site site = createSite( "site-id", "site-name", "mymodule:content-type" );
        context.setSite( site );
        PageTemplate pageTemplate = createPageTemplate();
        context.setPageTemplate( pageTemplate );

        String outputHtml = instruction.evaluate( context, "COMPONENT module:myPartComponent" );
        assertEquals( "<b>part content</b>", outputHtml );
    }

    private PageTemplate createPageTemplate()
    {
        return PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "my-page" ) ).
            controller( PageDescriptorKey.from( "mymodule:mypagetemplate" ) ).
            name( "my-page-template" ).
            parentPath( ContentPath.ROOT ).
            build();
    }

    private PageComponent createPartComponent()
    {
        return newPartComponent().
            name( "myPartComponent" ).
            descriptor( PartDescriptorKey.from( "mymodule:myparttemplate" ) ).
            build();
    }

    private Content createPage( final String id, final String name, final String contentTypeName )
    {
        RootDataSet rootDataSet = new RootDataSet();
        Property dataSet = new Property( "property1", Value.newString( "value1" ) );
        rootDataSet.add( dataSet );

        Region region = newRegion().
            name( "myRegion" ).
            add( newPartComponent().
                name( "myPartComponent" ).
                descriptor( PartDescriptorKey.from( "mymodule:myparttemplate" ) ).
                build() ).
            build();

        PageRegions pageRegions = newPageRegions().add( region ).build();
        Page page = Page.newPage().
            template( PageTemplateKey.from( "my-page" ) ).
            regions( pageRegions ).
            build();

        return Content.newContent().
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            owner( PrincipalKey.from( "myStore:user:me" ) ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "system:user:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) ).
            page( page ).
            build();
    }

    private Site createSite( final String id, final String name, final String contentTypeName )
    {
        RootDataSet rootDataSet = new RootDataSet();

        Property dataSet = new Property( "property1", Value.newString( "value1" ) );
        rootDataSet.add( dataSet );

        Page page = Page.newPage().
            template( PageTemplateKey.from( "my-page" ) ).
            config( rootDataSet ).
            build();

        return Site.newSite().
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            owner( PrincipalKey.from( "myStore:user:me" ) ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "system:user:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) ).
            page( page ).
            build();
    }

    private RendererFactory newRendererFactory( final String renderResult )
    {
        RendererFactory rendererFactory = mock( RendererFactory.class );
        Renderer<Renderable, PortalContext> renderer = new Renderer<Renderable, PortalContext>()
        {
            @Override
            public Class<Renderable> getType()
            {
                return Renderable.class;
            }

            @Override
            public RenderResult render( final Renderable component, final PortalContext context )
            {
                return RenderResult.newRenderResult().entity( renderResult ).build();
            }
        };

        when( rendererFactory.getRenderer( isA( Renderable.class ) ) ).thenReturn( renderer );
        return rendererFactory;
    }
}
