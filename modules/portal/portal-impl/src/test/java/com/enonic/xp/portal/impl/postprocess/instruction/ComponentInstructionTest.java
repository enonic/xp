package com.enonic.xp.portal.impl.postprocess.instruction;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.rendering.RendererDelegate;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentService;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.Site;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ComponentInstructionTest
{
    @Test
    public void testInstruction()
        throws Exception
    {
        RendererDelegate rendererDelegate = newRendererFactory( "<b>part content</b>" );
        ComponentService componentService = mock( ComponentService.class );
        ComponentInstruction instruction = new ComponentInstruction();
        instruction.setRendererDelegate( rendererDelegate );
        instruction.setComponentService( componentService );

        PortalRequest portalRequest = new PortalRequest();
        Content content = createPage( "content-id", "content-name", "myapplication:content-type" );
        portalRequest.setContent( content );
        Site site = createSite( "site-id", "site-name", "myapplication:content-type" );
        portalRequest.setSite( site );

        String outputHtml = instruction.evaluate( portalRequest, "COMPONENT myRegion/0" ).getAsString();
        assertEquals( "<b>part content</b>", outputHtml );
    }

    @Test
    public void testInstructionRenderByName()
        throws Exception
    {
        RendererDelegate rendererFactory = newRendererFactory( "<b>part content</b>" );
        ComponentService componentService = mock( ComponentService.class );

        Component component = createPartComponent();
        doReturn( component ).when( componentService ).getByKey( isA( DescriptorKey.class ) );
        ComponentInstruction instruction = new ComponentInstruction();
        instruction.setRendererDelegate( rendererFactory );
        instruction.setComponentService( componentService );

        PortalRequest portalRequest = new PortalRequest();
        Content content = createPage( "content-id", "content-name", "myapplication:content-type" );
        portalRequest.setContent( content );
        Site site = createSite( "site-id", "site-name", "myapplication:content-type" );
        portalRequest.setSite( site );

        String outputHtml = instruction.evaluate( portalRequest, "COMPONENT module:myPartComponent" ).getAsString();
        assertEquals( "<b>part content</b>", outputHtml );
    }

    @Test
    public void testInstructionRenderFragment()
        throws Exception
    {
        RendererDelegate rendererDelegate = newRendererFactory( "<b>part content</b>" );
        ComponentService componentService = mock( ComponentService.class );

        Component component = createPartComponent();
        doReturn( component ).when( componentService ).getByKey( isA( DescriptorKey.class ) );
        ComponentInstruction instruction = new ComponentInstruction();
        instruction.setRendererDelegate( rendererDelegate );
        instruction.setComponentService( componentService );

        PortalRequest portalRequest = new PortalRequest();
        Content content = createFragmentPage( "content-id", "content-name" );
        portalRequest.setContent( content );
        Site site = createSite( "site-id", "site-name", "myapplication:content-type" );
        portalRequest.setSite( site );

        String outputHtml = instruction.evaluate( portalRequest, "COMPONENT fragment" ).getAsString();
        assertEquals( "<b>part content</b>", outputHtml );
    }

    private Component createPartComponent()
    {
        return PartComponent.create().
            descriptor( DescriptorKey.from( "myapplication:myparttemplate" ) ).
            build();
    }

    private Content createPage( final String id, final String name, final String contentTypeName )
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.addString( "property1", "value1" );

        Region region = Region.create().
            name( "myRegion" ).
            add( PartComponent.create().
                descriptor( DescriptorKey.from( "myapplication:myparttemplate" ) ).
                build() ).
            build();

        PageRegions pageRegions = PageRegions.create().add( region ).build();
        Page page = Page.create().
            template( PageTemplateKey.from( "my-page" ) ).
            regions( pageRegions ).
            build();

        return Content.create().
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) ).
            page( page ).
            build();
    }

    private Content createFragmentPage( final String id, final String name )
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.addString( "property1", "value1" );

        PartComponent fragmentComponent = PartComponent.create().
            descriptor( DescriptorKey.from( "myapplication:myparttemplate" ) ).
            build();

        Page page = Page.create().
            template( PageTemplateKey.from( "my-page" ) ).
            fragment( fragmentComponent ).
            build();

        return Content.create().
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.fragment() ).
            page( page ).
            build();
    }

    private Site createSite( final String id, final String name, final String contentTypeName )
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.addString( "property1", "value1" );

        Page page = Page.create().
            template( PageTemplateKey.from( "my-page" ) ).
            config( rootDataSet ).
            build();

        return Site.create().
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) ).
            page( page ).
            build();
    }

    private RendererDelegate newRendererFactory( final String renderResult )
    {
        RendererDelegate rendererDelegate = mock( RendererDelegate.class );

        when( rendererDelegate.render( any(), any() ) ).thenReturn( PortalResponse.create().body( renderResult ).build() );
        return rendererDelegate;
    }
}
