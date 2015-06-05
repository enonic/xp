package com.enonic.xp.portal.impl.postprocess.instruction;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.content.page.Page;
import com.enonic.xp.content.page.PageRegions;
import com.enonic.xp.content.page.PageTemplate;
import com.enonic.xp.content.page.PageTemplateKey;
import com.enonic.xp.content.page.region.Component;
import com.enonic.xp.content.page.region.ComponentName;
import com.enonic.xp.content.page.region.ComponentService;
import com.enonic.xp.content.page.region.Region;
import com.enonic.xp.content.site.Site;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.rendering.Renderer;
import com.enonic.xp.portal.rendering.RendererFactory;
import com.enonic.xp.rendering.Renderable;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;

import static com.enonic.xp.content.page.PageRegions.newPageRegions;
import static com.enonic.xp.content.page.region.PartComponent.newPartComponent;
import static com.enonic.xp.content.page.region.Region.newRegion;
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
        ComponentService componentService = Mockito.mock( ComponentService.class );
        ComponentInstruction instruction = new ComponentInstruction();
        instruction.setRendererFactory( rendererFactory );
        instruction.setComponentService( componentService );

        PortalRequest portalRequest = new PortalRequest();
        Content content = createPage( "content-id", "content-name", "mymodule:content-type" );
        portalRequest.setContent( content );
        Site site = createSite( "site-id", "site-name", "mymodule:content-type" );
        portalRequest.setSite( site );

        String outputHtml = instruction.evaluate( portalRequest, "COMPONENT myRegion/0" );
        assertEquals( "<b>part content</b>", outputHtml );
    }

    @Test
    public void testInstructionRenderByName()
        throws Exception
    {
        RendererFactory rendererFactory = newRendererFactory( "<b>part content</b>" );
        ComponentService componentService = Mockito.mock( ComponentService.class );

        Component component = createPartComponent();
        doReturn( component ).when( componentService ).getByName( isA( ModuleKey.class ), isA( ComponentName.class ) );
        ComponentInstruction instruction = new ComponentInstruction();
        instruction.setRendererFactory( rendererFactory );
        instruction.setComponentService( componentService );

        PortalRequest portalRequest = new PortalRequest();
        Content content = createPage( "content-id", "content-name", "mymodule:content-type" );
        portalRequest.setContent( content );
        Site site = createSite( "site-id", "site-name", "mymodule:content-type" );
        portalRequest.setSite( site );
        PageTemplate pageTemplate = createPageTemplate();
        portalRequest.setPageTemplate( pageTemplate );

        String outputHtml = instruction.evaluate( portalRequest, "COMPONENT module:myPartComponent" );
        assertEquals( "<b>part content</b>", outputHtml );
    }

    private PageTemplate createPageTemplate()
    {
        return PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "my-page" ) ).
            controller( DescriptorKey.from( "mymodule:mypagetemplate" ) ).
            name( "my-page-template" ).
            parentPath( ContentPath.ROOT ).
            build();
    }

    private Component createPartComponent()
    {
        return newPartComponent().
            name( "myPartComponent" ).
            descriptor( DescriptorKey.from( "mymodule:myparttemplate" ) ).
            build();
    }

    private Content createPage( final String id, final String name, final String contentTypeName )
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.addString( "property1", "value1" );

        Region region = newRegion().
            name( "myRegion" ).
            add( newPartComponent().
                name( "myPartComponent" ).
                descriptor( DescriptorKey.from( "mymodule:myparttemplate" ) ).
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
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) ).
            page( page ).
            build();
    }

    private Site createSite( final String id, final String name, final String contentTypeName )
    {
        PropertyTree rootDataSet = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        rootDataSet.addString( "property1", "value1" );

        Page page = Page.newPage().
            template( PageTemplateKey.from( "my-page" ) ).
            config( rootDataSet ).
            build();

        return Site.newSite().
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) ).
            page( page ).
            build();
    }

    private RendererFactory newRendererFactory( final String renderResult )
    {
        RendererFactory rendererFactory = mock( RendererFactory.class );
        Renderer<Renderable> renderer = new Renderer<Renderable>()
        {
            @Override
            public Class<Renderable> getType()
            {
                return Renderable.class;
            }

            @Override
            public PortalResponse render( final Renderable component, final PortalRequest portalRequest )
            {
                return PortalResponse.create().body( renderResult ).build();
            }
        };

        when( rendererFactory.getRenderer( isA( Renderable.class ) ) ).thenReturn( renderer );
        return rendererFactory;
    }
}
