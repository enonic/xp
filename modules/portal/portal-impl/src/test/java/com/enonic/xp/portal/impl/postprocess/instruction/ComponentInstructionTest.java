package com.enonic.xp.portal.impl.postprocess.instruction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

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
import com.enonic.xp.portal.impl.rendering.RenderException;
import com.enonic.xp.portal.impl.rendering.RendererDelegate;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentService;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutRegions;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.Site;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ComponentInstructionTest
{

    private RendererDelegate rendererDelegate;

    private ComponentService componentService;

    private ComponentInstruction instruction;

    @BeforeEach
    public final void setup()
        throws Exception
    {
        this.rendererDelegate = mock( RendererDelegate.class );
        this.componentService = mock( ComponentService.class );
        this.instruction = new ComponentInstruction();

        instruction.setRendererDelegate( rendererDelegate );
        instruction.setComponentService( componentService );
    }

    @Test
    public void testInstructionWithPart()
        throws Exception
    {
        returnOnRender( "<b>part content</b>" );

        final PortalRequest portalRequest = new PortalRequest();
        final Content content = createPage( "content-id", "content-name", "myapplication:content-type" );
        portalRequest.setContent( content );
        final Site site = createSite( "site-id", "site-name", "myapplication:content-type" );
        portalRequest.setSite( site );

        assertEquals( "<b>part content</b>", instruction.evaluate( portalRequest, "COMPONENT myRegion/0" ).getBody() );
    }

    @Test
    public void testLayoutNoServiceReturnsSameObject()
    {
        final LayoutComponent layoutComponent =
            LayoutComponent.create().descriptor( DescriptorKey.from( "myapplication:layout" ) ).build();

        testLayoutIsReturned(layoutComponent);
    }

    @Test
    public void testEmptyLayout()
    {
        testLayoutIsReturned(LayoutComponent.create().build());
    }

    private void testLayoutIsReturned( final LayoutComponent layoutComponent )
    {
        final ArgumentCaptor<Component> captor = ArgumentCaptor.forClass( Component.class );
        returnOnRender( "render result", captor.capture() );

        final PortalRequest portalRequest = new PortalRequest();

        final Content content = createPage( "content-id", "content-name", "myapplication:content-type", layoutComponent );
        portalRequest.setContent( content );

        instruction.evaluate( portalRequest, "COMPONENT myRegion/0" );

        assertEquals( captor.getValue(), layoutComponent );
    }

    @Test
    public void testFragmentContentNotLayoutThrowsException()
    {
        final PortalRequest portalRequest = new PortalRequest();

        final Content content = createFragmentPage( "content-id", "content-name" );
        portalRequest.setContent( content );

        assertThrows( RenderException.class, () -> instruction.evaluate( portalRequest, "COMPONENT myRegion/0" ));
    }

    @Test
    public void testFragmentContentThrowsWhenComponentNotFound()
    {
        final DescriptorKey layoutDescriptorKey = DescriptorKey.from( "myapplication:layout" );
        final LayoutRegions regions =
            LayoutRegions.create().add( Region.create().name( "r1" ).build() ).add( Region.create().name( "r2" ).build() ).build();
        final LayoutComponent layoutComponent = LayoutComponent.create().descriptor( layoutDescriptorKey ).regions( regions ).build();

        final PortalRequest portalRequest = new PortalRequest();
        final Content content = createFragmentPage( "content-id", "content-name", layoutComponent );
        portalRequest.setContent( content );

        assertThrows( RenderException.class, () -> instruction.evaluate( portalRequest, "COMPONENT r1/0" ));
    }

    @Test
    public void testFragmentContent()
    {
        final ArgumentCaptor<Component> captor = ArgumentCaptor.forClass( Component.class );
        returnOnRender( "render result", captor.capture() );

        final DescriptorKey layoutDescriptorKey = DescriptorKey.from( "myapplication:layout" );
        final PartComponent partComponent = createPartComponent();
        final LayoutRegions regions = LayoutRegions.create()
            .add( Region.create().name( "r1" ).add( partComponent ).build() )
            .add( Region.create().name( "r2" ).build() )
            .build();
        final LayoutComponent layoutComponent = LayoutComponent.create().descriptor( layoutDescriptorKey ).regions( regions ).build();

        final PortalRequest portalRequest = new PortalRequest();
        final Content content = createFragmentPage( "content-id", "content-name", layoutComponent );
        portalRequest.setContent( content );

        assertEquals( "render result", instruction.evaluate( portalRequest, "COMPONENT r1/0" ).getBody() );
    }

    @Test
    public void testInstructionRenderByName()
        throws Exception
    {
        returnOnRender( "<b>part content</b>" );

        final Component component = createPartComponent();
        doReturn( component ).when( componentService ).getByKey( isA( DescriptorKey.class ) );

        final PortalRequest portalRequest = new PortalRequest();
        final Content content = createPage( "content-id", "content-name", "myapplication:content-type" );
        portalRequest.setContent( content );
        final Site site = createSite( "site-id", "site-name", "myapplication:content-type" );
        portalRequest.setSite( site );

        assertEquals( "<b>part content</b>", instruction.evaluate( portalRequest, "COMPONENT module:myPartComponent" ).getBody() );
    }

    @Test
    public void testInstructionRenderFragment()
    {
        returnOnRender( "<b>part content</b>" );

        final Component component = createPartComponent();
        doReturn( component ).when( componentService ).getByKey( isA( DescriptorKey.class ) );

        final PortalRequest portalRequest = new PortalRequest();
        final Content content = createFragmentPage( "content-id", "content-name" );
        portalRequest.setContent( content );
        final Site site = createSite( "site-id", "site-name", "myapplication:content-type" );
        portalRequest.setSite( site );

        assertEquals( "<b>part content</b>", instruction.evaluate( portalRequest, "COMPONENT fragment" ).getBody() );
    }

    @Test
    public void testInstructionRenderFragmentWithLayout()
    {
        returnOnRender( "<b>part content</b>" );

        final DescriptorKey layoutDescriptorKey = DescriptorKey.from( "myapplication:layout" );
        final LayoutRegions regions =
            LayoutRegions.create().add( Region.create().name( "r1" ).build() ).add( Region.create().name( "r2" ).build() ).build();
        final LayoutComponent layoutComponent = LayoutComponent.create().descriptor( layoutDescriptorKey ).regions( regions ).build();

        doReturn( layoutComponent ).when( componentService ).getByKey( isA( DescriptorKey.class ) );

        final PortalRequest portalRequest = new PortalRequest();
        final LayoutComponent emptyLayoutComponent =
            LayoutComponent.create().descriptor( DescriptorKey.from( "myapplication:layout" ) ).build();
        final Content content = createFragmentPage( "content-id", "content-name", emptyLayoutComponent );

        portalRequest.setContent( content );
        final Site site = createSite( "site-id", "site-name", "myapplication:content-type" );
        portalRequest.setSite( site );

        assertEquals( "<b>part content</b>", instruction.evaluate( portalRequest, "COMPONENT fragment" ).getBody() );
    }

    @Test
    public void testFragmentInstructionWithoutRequestContentReturnsNull()
    {
        final PortalResponse response = instruction.evaluate( new PortalRequest(), "COMPONENT fragment" );
        assertNull( response );
    }

    @Test
    public void testWrongInstruction()
    {
        final PortalResponse response = instruction.evaluate( new PortalRequest(), "WRONG module:myPartComponent" );
        assertNull( response );
    }

    @Test
    public void testWrongInstructionLength()
    {
        final PortalResponse response = instruction.evaluate( new PortalRequest(), "COMPONENT module:myPartComponent tooLong" );
        assertNull( response );
    }

    @Test
    public void testNoContentInRequestReturnsNull()
    {
        final PortalResponse response = instruction.evaluate( new PortalRequest(), "COMPONENT r1/0" );
        assertNull( response );
    }

    @Test
    public void testComponentNotFoundThrows()
    {
        final PortalRequest portalRequest = new PortalRequest();
        final Content content = createPage( "content-id", "content-name", "myapplication:content-type" );
        portalRequest.setContent( content );

        assertThrows( RenderException.class, () -> instruction.evaluate( portalRequest, "COMPONENT myRegion/1" ));
    }

    private PartComponent createPartComponent()
    {
        return PartComponent.create().
            descriptor( DescriptorKey.from( "myapplication:myparttemplate" ) ).
            build();
    }

    private Content createPage( final String id, final String name, final String contentTypeName )
    {
        return createPage( id, name, contentTypeName, createPartComponent() );
    }

    private Content createPage( final String id, final String name, final String contentTypeName, final Component regionComponent )
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.addString( "property1", "value1" );

        Region region = Region.create().
            name( "myRegion" ).
            add( regionComponent ).
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
        return createFragmentPage( id, name, createPartComponent() );
    }

    private Content createFragmentPage( final String id, final String name, final Component fragmentComponent )
    {
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

    private RendererDelegate returnOnRender( final String renderResult, final Object renderObject  )
    {
        when( rendererDelegate.render( renderObject, any() ) ).thenReturn( PortalResponse.create().body( renderResult ).build() );
        return rendererDelegate;
    }

    private RendererDelegate returnOnRender( final String renderResult )
    {
        return returnOnRender( renderResult, any() );
    }
}
