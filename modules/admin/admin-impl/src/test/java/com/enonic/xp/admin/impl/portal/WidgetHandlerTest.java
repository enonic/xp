package com.enonic.xp.admin.impl.portal;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.site.Site;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseHandlerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class WidgetHandlerTest
    extends BaseHandlerTest
{

    private WidgetHandler handler;

    private PortalRequest request;

    protected ContentService contentService;

    private WidgetDescriptorService widgetDescriptorService;

    private ControllerScript controllerScript;

    private HttpServletRequest rawRequest;

    @BeforeEach
    public final void setup()
        throws Exception
    {
        this.request = new PortalRequest();
        final ControllerScriptFactory controllerScriptFactory = Mockito.mock( ControllerScriptFactory.class );
        this.controllerScript = Mockito.mock( ControllerScript.class );
        Mockito.when( controllerScriptFactory.fromDir( Mockito.anyObject() ) ).thenReturn( this.controllerScript );

        final PortalResponse portalResponse = PortalResponse.create().build();
        Mockito.when( this.controllerScript.execute( Mockito.anyObject() ) ).thenReturn( portalResponse );

        this.contentService = Mockito.mock( ContentService.class );
        this.widgetDescriptorService = Mockito.mock( WidgetDescriptorService.class );

        this.handler = new WidgetHandler();
        this.handler.setControllerScriptFactory( controllerScriptFactory );
        this.handler.setContentService( this.contentService );
        this.handler.setWidgetDescriptorService( this.widgetDescriptorService );

        this.rawRequest = Mockito.mock( HttpServletRequest.class );
        Mockito.when( this.rawRequest.isUserInRole( Mockito.anyString() ) ).thenReturn( Boolean.TRUE );
        this.request.setRawRequest( this.rawRequest );

        this.request.setMethod( HttpMethod.GET );
        this.request.setContentPath( ContentPath.from( "/site/somepath/content" ) );
        this.request.setEndpointPath( "/_/widgets/demo/mywidget" );
        this.request.setRawPath( "/admin/tool/_/service/demo/myservice" );
    }

    @Test
    public void testOrder()
    {
        assertEquals( 0, this.handler.getOrder() );
    }

    @Test
    public void testMatch()
    {
        this.request.setEndpointPath( null );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/_/other/demo/mywidget" );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/widgets/demo/mywidget" );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/_/widgets/demo/mywidget" );
        assertEquals( true, this.handler.canHandle( this.request ) );
    }

    @Test
    public void testOptions()
        throws Exception
    {
        mockDescriptor( true );
        final PortalResponse portalResponse = PortalResponse.create().status( HttpStatus.METHOD_NOT_ALLOWED ).build();
        Mockito.when( this.controllerScript.execute( Mockito.anyObject() ) ).thenReturn( portalResponse );

        this.request.setMethod( HttpMethod.OPTIONS );
        this.request.setMode( RenderMode.ADMIN );

        final WebResponse response = this.handler.handle( this.request, WebResponse.create().build(), null );
        assertNotNull( response );
        assertEquals( HttpStatus.OK, response.getStatus() );
        assertEquals( "GET,POST,HEAD,OPTIONS,PUT,DELETE,TRACE", response.getHeaders().get( "Allow" ) );
    }

    @Test
    public void testNotValidUrlPattern()
        throws Exception
    {
        this.request.setEndpointPath( "/_/widgets/" );

        try
        {
            this.handler.handle( this.request, WebResponse.create().build(), null );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Not a valid service url pattern", e.getMessage() );
        }
    }

    @Test
    public void executeFailsWithWrongMode()
        throws Exception
    {
        assertThrows(WebException.class, () -> {
            this.request.setEndpointPath("/_/widgets/demo/test");
            this.request.setMode(RenderMode.EDIT);

            final WebResponse response = this.handler.handle(this.request, WebResponse.create().build(), null);
            assertEquals(HttpStatus.OK, response.getStatus());

            Mockito.verify(this.controllerScript).execute(this.request);
        });
    }

    @Test
    public void testForbidden()
        throws Exception
    {
        this.mockDescriptor( false );
        this.request.setEndpointPath( "/_/widgets/demo/test" );
        this.request.setMode( RenderMode.ADMIN );
        assertThrows(WebException.class, () -> this.handler.handle( this.request, WebResponse.create().build(), null ) );
    }

    @Test
    public void executeScript_noContent()
        throws Exception
    {
        mockDescriptor( true );

        this.request.setEndpointPath( "/_/widgets/demo/test" );
        this.request.setMode( RenderMode.ADMIN );

        final WebResponse response = this.handler.handle( this.request, WebResponse.create().build(), null );
        assertEquals( HttpStatus.OK, response.getStatus() );

        Mockito.verify( this.controllerScript ).execute( this.request );

        assertNotNull( this.request.getApplicationKey() );
        assertNull( this.request.getSite() );
        assertNull( this.request.getContent() );
    }

    @Test
    public void executeScript_validSite()
        throws Exception
    {
        setupContentAndSite();
        mockDescriptor( true );

        this.request.setEndpointPath( "/_/widgets/demo/test" );
        this.request.setMode( RenderMode.ADMIN );

        final WebResponse response = this.handler.handle( this.request, WebResponse.create().build(), null );
        assertEquals( HttpStatus.OK, response.getStatus() );

        Mockito.verify( this.controllerScript ).execute( this.request );

        assertNotNull( this.request.getApplicationKey() );
        assertNotNull( this.request.getSite() );
        assertNotNull( this.request.getContent() );
        assertEquals( "/admin/tool/_/widgets/demo/test", this.request.getContextPath() );
    }

    @Test
    public void testContentDependentWidget()
        throws Exception
    {
        final Content content = createPage( "id", "site/somepath/content", "myapplication:ctype", false );
        Mockito.when( this.contentService.getByPath( ContentPath.from( "site/somepath/content" ).asAbsolute() ) ).
            thenReturn( content );
        Mockito.when( this.contentService.getById( content.getId() ) ).
            thenReturn( content );
        mockDescriptor( true );

        this.request.setEndpointPath( "/_/widgets/demo/test" );
        this.request.setMode( RenderMode.ADMIN );

        final WebResponse response = this.handler.handle( this.request, WebResponse.create().build(), null );
        assertEquals( HttpStatus.OK, response.getStatus() );

        Mockito.verify( this.controllerScript ).execute( this.request );

        assertNotNull( this.request.getApplicationKey() );
        assertNotNull( this.request.getContent() );

    }

    private void setupContentAndSite()
        throws Exception
    {
        final Content content = createPage( "id", "site/somepath/content", "myapplication:ctype", true );

        Mockito.when( this.contentService.getByPath( ContentPath.from( "site/somepath/content" ).asAbsolute() ) ).
            thenReturn( content );

        Mockito.when( this.contentService.getNearestSite( Mockito.isA( ContentId.class ) ) ).
            thenReturn( createSite( "id", "site", "myapplication:contenttypename" ) );

        Mockito.when( this.contentService.getById( content.getId() ) ).
            thenReturn( content );
    }

    private Content createPage( final String id, final String path, final String contentTypeName, final boolean withPage )
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.addString( "property1", "value1" );

        final Content.Builder content = Content.create().
            id( ContentId.from( id ) ).
            path( ContentPath.from( path ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) );

        if ( withPage )
        {
            PageRegions pageRegions = PageRegions.create().
                add( Region.create().name( "main-region" ).
                    add( PartComponent.create().
                        build() ).
                    build() ).
                build();

            Page page = Page.create().
                template( PageTemplateKey.from( "my-page" ) ).
                regions( pageRegions ).
                config( rootDataSet ).
                build();
            content.page( page );
        }
        return content.build();
    }

    private Site createSite( final String id, final String path, final String contentTypeName )
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.addString( "property1", "value1" );

        Page page = Page.create().
            template( PageTemplateKey.from( "my-page" ) ).
            config( rootDataSet ).
            build();

        return Site.create().
            id( ContentId.from( id ) ).
            path( ContentPath.from( path ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) ).
            page( page ).
            build();
    }

    private void mockDescriptor( boolean hasAccess )
    {
        WidgetDescriptor descriptor = Mockito.mock( WidgetDescriptor.class );
        Mockito.when( descriptor.isAccessAllowed( Mockito.any( PrincipalKeys.class ) ) ).thenReturn( hasAccess );
        Mockito.when( this.widgetDescriptorService.getByKey( Mockito.any( DescriptorKey.class ) ) ).thenReturn( descriptor );
    }
}
