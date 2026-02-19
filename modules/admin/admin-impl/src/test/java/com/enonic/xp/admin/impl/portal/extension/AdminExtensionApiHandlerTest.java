package com.enonic.xp.admin.impl.portal.extension;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.admin.extension.AdminExtensionDescriptor;
import com.enonic.xp.admin.extension.AdminExtensionDescriptorService;
import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminExtensionApiHandlerTest
{
    private AdminExtensionApiHandler handler;

    private ControllerScriptFactory controllerScriptFactory;

    private AdminExtensionDescriptorService extensionDescriptorService;

    private AdminToolDescriptorService adminToolDescriptorService;

    @BeforeEach
    void setUp()
    {
        this.controllerScriptFactory = mock( ControllerScriptFactory.class );
        this.extensionDescriptorService = mock( AdminExtensionDescriptorService.class );
        this.adminToolDescriptorService = mock( AdminToolDescriptorService.class );

        this.handler =
            new AdminExtensionApiHandler( this.extensionDescriptorService, this.controllerScriptFactory, this.adminToolDescriptorService );
    }


    @Test
    void testInvalidPattern()
    {
        final WebRequest webRequest1 = new WebRequest();
        webRequest1.setMethod( HttpMethod.GET );
        webRequest1.setRawPath( "/path/to/some/resource" );

        NullPointerException npe = assertThrows( NullPointerException.class, () -> this.handler.handle( webRequest1 ) );
        assertEquals( "Endpoint path cannot be null", npe.getMessage() );

        final WebRequest webRequest2 = new WebRequest();
        webRequest2.setMethod( HttpMethod.GET );
        webRequest2.setRawPath( "/path/_/somePath" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( webRequest2 ) );
        assertEquals( "Invalid Extension API path: /somePath", ex.getMessage() );
    }

    @Test
    void testInvalidApplicationKey()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setMethod( HttpMethod.GET );
        webRequest.setRawPath( "/path/_/admin:widget/<app>:widgetName" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( webRequest ) );
        assertEquals( "Invalid Extension API path: /admin:widget/<app>:widgetName", ex.getMessage() );
    }

    @Test
    void testNoWidgetDescriptor()
    {
        when( extensionDescriptorService.getByKey( eq( DescriptorKey.from( ApplicationKey.from( "app" ), "extensionName" ) ) ) ).thenReturn(
            null );

        final WebRequest webRequest = new WebRequest();
        webRequest.setMethod( HttpMethod.GET );
        webRequest.setRawPath( "/path/_/admin:extension/app:extensionName" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( webRequest ) );
        assertEquals( "Extension [app:extensionName] not found", ex.getMessage() );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
    }

    @Test
    void testWidgetDescriptorAccessDenied()
    {
        final DescriptorKey descriptorKey = DescriptorKey.from( ApplicationKey.from( "app" ), "widgetName" );

        AdminExtensionDescriptor widgetDescriptor = mock( AdminExtensionDescriptor.class );
        when( widgetDescriptor.isAccessAllowed( any( PrincipalKeys.class ) ) ).thenReturn( false );
        when( widgetDescriptor.getKey() ).thenReturn( descriptorKey );

        when( extensionDescriptorService.getByKey( eq( descriptorKey ) ) ).thenReturn( widgetDescriptor );

        final WebRequest webRequest = new WebRequest();
        webRequest.setMethod( HttpMethod.GET );
        webRequest.setRawPath( "/path/_/admin:extension/app:widgetName" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( webRequest ) );
        assertEquals( HttpStatus.UNAUTHORIZED, ex.getStatus() );
    }

    @Test
    void testHandle()
    {
        final DescriptorKey descriptorKey = DescriptorKey.from( ApplicationKey.from( "app" ), "widgetName" );

        final AdminExtensionDescriptor widgetDescriptor =
            AdminExtensionDescriptor.create().key( descriptorKey ).interfaces( "myInterface" ).build();

        when( extensionDescriptorService.getByKey( eq( descriptorKey ) ) ).thenReturn( widgetDescriptor );

        final DescriptorKey adminToolDescriptorKey = DescriptorKey.from( ApplicationKey.from( "myapp" ), "toolName" );

        final AdminToolDescriptor adminToolDescriptor =
            AdminToolDescriptor.create().key( adminToolDescriptorKey ).interfaces( "myInterface" ).build();

        when( adminToolDescriptorService.getByKey( eq( adminToolDescriptorKey ) ) ).thenReturn( adminToolDescriptor );

        final WebRequest webRequest = new WebRequest();
        webRequest.setMethod( HttpMethod.GET );
        webRequest.setRawPath( "/admin/myapp/toolName/_/admin:extension/app:widgetName" );

        final ControllerScript controllerScript = mock( ControllerScript.class );
        when( controllerScript.execute( any( PortalRequest.class ) ) ).thenReturn( PortalResponse.create().build() );

        when( controllerScriptFactory.fromScript( any( ResourceKey.class ) ) ).thenReturn( controllerScript );

        WebResponse response = this.handler.handle( webRequest );
        assertEquals( HttpStatus.OK, response.getStatus() );
    }

    @Test
    void testVerifyMounts()
    {
        final DescriptorKey widgetDescriptorKey = DescriptorKey.from( ApplicationKey.from( "app" ), "widgetName" );

        final AdminExtensionDescriptor widgetDescriptor = mock( AdminExtensionDescriptor.class );
        when( widgetDescriptor.isAccessAllowed( any( PrincipalKeys.class ) ) ).thenReturn( true );
        when( widgetDescriptor.getKey() ).thenReturn( widgetDescriptorKey );
        when( widgetDescriptor.getInterfaces() ).thenReturn( Set.of( "widgetInterface" ) );

        when( extensionDescriptorService.getByKey( eq( widgetDescriptorKey ) ) ).thenReturn( widgetDescriptor );

        final DescriptorKey adminToolDescriptorKey = DescriptorKey.from( ApplicationKey.from( "myapp" ), "toolName" );

        final AdminToolDescriptor adminToolDescriptor =
            AdminToolDescriptor.create().key( adminToolDescriptorKey ).interfaces( "widgetInterface" ).build();

        when( adminToolDescriptorService.getByKey( eq( adminToolDescriptorKey ) ) ).thenReturn( adminToolDescriptor );

        final WebRequest webRequest = new WebRequest();
        webRequest.setMethod( HttpMethod.GET );
        webRequest.setRawPath( "/admin/myapp/toolName/_/admin:extension/app:widgetName" );

        final ControllerScript controllerScript = mock( ControllerScript.class );
        when( controllerScript.execute( any( PortalRequest.class ) ) ).thenReturn( PortalResponse.create().build() );

        when( controllerScriptFactory.fromScript( any( ResourceKey.class ) ) ).thenReturn( controllerScript );

        WebResponse response = this.handler.handle( webRequest );
        assertEquals( HttpStatus.OK, response.getStatus() );
    }

    @Test
    void testWidgetDoesNotMountedToAdminTool()
    {
        final DescriptorKey widgetDescriptorKey = DescriptorKey.from( ApplicationKey.from( "app" ), "widgetName" );

        final AdminExtensionDescriptor widgetDescriptor = mock( AdminExtensionDescriptor.class );
        when( widgetDescriptor.isAccessAllowed( any( PrincipalKeys.class ) ) ).thenReturn( true );
        when( widgetDescriptor.getKey() ).thenReturn( widgetDescriptorKey );
        when( widgetDescriptor.getInterfaces() ).thenReturn( Set.of( "widgetInterface" ) );

        when( extensionDescriptorService.getByKey( eq( widgetDescriptorKey ) ) ).thenReturn( widgetDescriptor );

        final DescriptorKey adminToolDescriptorKey = DescriptorKey.from( ApplicationKey.from( "myapp" ), "toolName" );

        final AdminToolDescriptor adminToolDescriptor =
            AdminToolDescriptor.create().key( adminToolDescriptorKey ).interfaces( "admin.dashboard" ).build();

        when( adminToolDescriptorService.getByKey( eq( adminToolDescriptorKey ) ) ).thenReturn( adminToolDescriptor );

        final WebRequest webRequest = new WebRequest();
        webRequest.setMethod( HttpMethod.GET );
        webRequest.setRawPath( "/admin/myapp/toolName/_/admin:extension/app:widgetName" );

        final ControllerScript controllerScript = mock( ControllerScript.class );
        when( controllerScript.execute( any( PortalRequest.class ) ) ).thenReturn( PortalResponse.create().build() );

        when( controllerScriptFactory.fromScript( any( ResourceKey.class ) ) ).thenReturn( controllerScript );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( webRequest ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "Extension [app:widgetName] is not mounted to admin tool [myapp:toolName]", ex.getMessage() );
    }

    @Test
    void testGenericWidgetAvailableInAdminToolWhenWidgetNotInInterfaces()
    {
        final DescriptorKey widgetDescriptorKey = DescriptorKey.from( ApplicationKey.from( "app" ), "widgetName" );

        final AdminExtensionDescriptor widgetDescriptor =
            AdminExtensionDescriptor.create().key( widgetDescriptorKey ).interfaces( "generic" ).build();

        when( extensionDescriptorService.getByKey( eq( widgetDescriptorKey ) ) ).thenReturn( widgetDescriptor );

        final DescriptorKey adminToolDescriptorKey = DescriptorKey.from( ApplicationKey.from( "myapp" ), "toolName" );

        final AdminToolDescriptor adminToolDescriptor =
            AdminToolDescriptor.create().key( adminToolDescriptorKey ).interfaces( "admin.dashboard" ).build();

        when( adminToolDescriptorService.getByKey( eq( adminToolDescriptorKey ) ) ).thenReturn( adminToolDescriptor );

        final WebRequest webRequest = new WebRequest();
        webRequest.setMethod( HttpMethod.GET );
        webRequest.setRawPath( "/admin/myapp/toolName/_/admin:extension/app:widgetName" );

        final ControllerScript controllerScript = mock( ControllerScript.class );
        when( controllerScript.execute( any( PortalRequest.class ) ) ).thenReturn( PortalResponse.create().build() );

        when( controllerScriptFactory.fromScript( any( ResourceKey.class ) ) ).thenReturn( controllerScript );

        WebResponse res = this.handler.handle( webRequest );
        assertEquals( HttpStatus.OK, res.getStatus() );
    }

    @Test
    void testWidgetInWhenAdminToolDoesNotHaveInterfaces()
    {
        final DescriptorKey widgetDescriptorKey = DescriptorKey.from( ApplicationKey.from( "app" ), "widgetName" );

        final AdminExtensionDescriptor widgetDescriptor =
            AdminExtensionDescriptor.create().key( widgetDescriptorKey ).interfaces( "myInterface" ).build();

        when( extensionDescriptorService.getByKey( eq( widgetDescriptorKey ) ) ).thenReturn( widgetDescriptor );

        final DescriptorKey adminToolDescriptorKey = DescriptorKey.from( ApplicationKey.from( "myapp" ), "toolName" );

        final AdminToolDescriptor adminToolDescriptor = AdminToolDescriptor.create().key( adminToolDescriptorKey ).build();

        when( adminToolDescriptorService.getByKey( eq( adminToolDescriptorKey ) ) ).thenReturn( adminToolDescriptor );

        final WebRequest webRequest = new WebRequest();
        webRequest.setMethod( HttpMethod.GET );
        webRequest.setRawPath( "/admin/myapp/toolName/_/admin:extension/app:widgetName" );

        final ControllerScript controllerScript = mock( ControllerScript.class );
        when( controllerScript.execute( any( PortalRequest.class ) ) ).thenReturn( PortalResponse.create().build() );

        when( controllerScriptFactory.fromScript( any( ResourceKey.class ) ) ).thenReturn( controllerScript );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( webRequest ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "Extension [app:widgetName] is not mounted to admin tool [myapp:toolName]", ex.getMessage() );
    }

    @Test
    void testWidgetInWhenAdminToolDoesNotHaveDescriptor()
    {
        final DescriptorKey widgetDescriptorKey = DescriptorKey.from( ApplicationKey.from( "app" ), "extensionName" );

        final AdminExtensionDescriptor widgetDescriptor =
            AdminExtensionDescriptor.create().key( widgetDescriptorKey ).interfaces( "myInterface" ).build();

        when( extensionDescriptorService.getByKey( eq( widgetDescriptorKey ) ) ).thenReturn( widgetDescriptor );

        final DescriptorKey adminToolDescriptorKey = DescriptorKey.from( ApplicationKey.from( "myapp" ), "toolName" );

        when( adminToolDescriptorService.getByKey( eq( adminToolDescriptorKey ) ) ).thenReturn( null );

        final WebRequest webRequest = new WebRequest();
        webRequest.setMethod( HttpMethod.GET );
        webRequest.setRawPath( "/admin/myapp/toolName/_/admin:extension/app:extensionName" );

        final ControllerScript controllerScript = mock( ControllerScript.class );
        when( controllerScript.execute( any( PortalRequest.class ) ) ).thenReturn( PortalResponse.create().build() );

        when( controllerScriptFactory.fromScript( any( ResourceKey.class ) ) ).thenReturn( controllerScript );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( webRequest ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "Extension [app:extensionName] is not mounted to admin tool [myapp:toolName]", ex.getMessage() );
    }

    @Test
    void testWidgetOnInvalidAdminToolUrl()
    {
        final DescriptorKey widgetDescriptorKey = DescriptorKey.from( ApplicationKey.from( "app" ), "widgetName" );

        final AdminExtensionDescriptor widgetDescriptor =
            AdminExtensionDescriptor.create().key( widgetDescriptorKey ).interfaces( "myInterface" ).build();

        when( extensionDescriptorService.getByKey( eq( widgetDescriptorKey ) ) ).thenReturn( widgetDescriptor );

        final WebRequest webRequest = new WebRequest();
        webRequest.setMethod( HttpMethod.GET );
        webRequest.setRawPath( "/admin/app/_/admin:extension/app:widgetName" );

        final ControllerScript controllerScript = mock( ControllerScript.class );
        when( controllerScript.execute( any( PortalRequest.class ) ) ).thenReturn( PortalResponse.create().build() );

        when( controllerScriptFactory.fromScript( any( ResourceKey.class ) ) ).thenReturn( controllerScript );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( webRequest ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "Invalid admin tool URL [/admin/app/_/admin:extension/app:widgetName]", ex.getMessage() );
    }
}