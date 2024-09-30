package com.enonic.xp.admin.impl.portal.widget;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
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

public class WidgetApiHandlerTest
{
    private WidgetApiHandler handler;

    private ControllerScriptFactory controllerScriptFactory;

    private WidgetDescriptorService widgetDescriptorService;

    private AdminToolDescriptorService adminToolDescriptorService;

    @BeforeEach
    public void setUp()
    {
        this.controllerScriptFactory = mock( ControllerScriptFactory.class );
        this.widgetDescriptorService = mock( WidgetDescriptorService.class );
        this.adminToolDescriptorService = mock( AdminToolDescriptorService.class );

        this.handler = new WidgetApiHandler( this.widgetDescriptorService, this.controllerScriptFactory, this.adminToolDescriptorService );
    }


    @Test
    void testInvalidPattern()
    {
        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );

        when( webRequest.getEndpointPath() ).thenReturn( null );
        when( webRequest.getRawPath() ).thenReturn( "/path/to/some/resource" );

        // path must start with `/api/` or contains `/_/` as endpoint part
        IllegalArgumentException ex = assertThrows( IllegalArgumentException.class, () -> this.handler.handle( webRequest ) );
        assertEquals( "Invalid Widget API path: /path/to/some/resource", ex.getMessage() );
    }

    @Test
    void testInvalidApplicationKey()
    {
        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );
        when( webRequest.getEndpointPath() ).thenReturn( "/_/admin/widget/<app>/widgetName" );
        when( webRequest.getRawPath() ).thenReturn( "/path/_/admin/widget/<app>/widgetName" );

        IllegalArgumentException ex = assertThrows( IllegalArgumentException.class, () -> this.handler.handle( webRequest ) );
        assertEquals( "Invalid application key: <app>", ex.getMessage() );
    }

    @Test
    void testNoWidgetDescriptor()
    {
        when( widgetDescriptorService.getByKey( eq( DescriptorKey.from( ApplicationKey.from( "app" ), "widgetName" ) ) ) ).thenReturn(
            null );

        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );
        when( webRequest.getEndpointPath() ).thenReturn( "/_/admin/widget/app/widgetName" );
        when( webRequest.getRawPath() ).thenReturn( "/path/_/admin/widget/app/widgetName" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( webRequest ) );
        assertEquals( "Widget [app:widgetName] not found", ex.getMessage() );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
    }

    @Test
    void testWidgetDescriptorAccessDenied()
    {
        final DescriptorKey descriptorKey = DescriptorKey.from( ApplicationKey.from( "app" ), "widgetName" );

        WidgetDescriptor widgetDescriptor = mock( WidgetDescriptor.class );
        when( widgetDescriptor.isAccessAllowed( any( PrincipalKeys.class ) ) ).thenReturn( false );
        when( widgetDescriptor.getKey() ).thenReturn( descriptorKey );

        when( widgetDescriptorService.getByKey( eq( descriptorKey ) ) ).thenReturn( widgetDescriptor );

        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );
        when( webRequest.getEndpointPath() ).thenReturn( "/_/admin/widget/app/widgetName" );
        when( webRequest.getRawPath() ).thenReturn( "/path/_/admin/widget/app/widgetName" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( webRequest ) );
        assertEquals( HttpStatus.UNAUTHORIZED, ex.getStatus() );
    }

    @Test
    void testHandle()
    {
        final DescriptorKey descriptorKey = DescriptorKey.from( ApplicationKey.from( "app" ), "widgetName" );

        WidgetDescriptor widgetDescriptor = mock( WidgetDescriptor.class );
        when( widgetDescriptor.isAccessAllowed( any( PrincipalKeys.class ) ) ).thenReturn( true );
        when( widgetDescriptor.getKey() ).thenReturn( descriptorKey );

        when( widgetDescriptorService.getByKey( eq( descriptorKey ) ) ).thenReturn( widgetDescriptor );

        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );
        when( webRequest.getEndpointPath() ).thenReturn( "/_/admin/widget/app/widgetName" );
        when( webRequest.getRawPath() ).thenReturn( "/path/_/admin/widget/app/widgetName" );

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

        final WidgetDescriptor widgetDescriptor = mock( WidgetDescriptor.class );
        when( widgetDescriptor.isAccessAllowed( any( PrincipalKeys.class ) ) ).thenReturn( true );
        when( widgetDescriptor.getKey() ).thenReturn( widgetDescriptorKey );
        when( widgetDescriptor.getInterfaces() ).thenReturn( Set.of( "widgetInterface" ) );

        when( widgetDescriptorService.getByKey( eq( widgetDescriptorKey ) ) ).thenReturn( widgetDescriptor );

        final DescriptorKey adminToolDescriptorKey = DescriptorKey.from( ApplicationKey.from( "myapp" ), "toolName" );

        final AdminToolDescriptor adminToolDescriptor =
            AdminToolDescriptor.create().key( adminToolDescriptorKey ).addInterface( "widgetInterface" ).build();

        when( adminToolDescriptorService.getByKey( eq( adminToolDescriptorKey ) ) ).thenReturn( adminToolDescriptor );

        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );
        when( webRequest.getEndpointPath() ).thenReturn( "/_/admin/widget/app/widgetName" );
        when( webRequest.getRawPath() ).thenReturn( "/admin/myapp/toolName/_/admin/widget/app/widgetName" );

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

        final WidgetDescriptor widgetDescriptor = mock( WidgetDescriptor.class );
        when( widgetDescriptor.isAccessAllowed( any( PrincipalKeys.class ) ) ).thenReturn( true );
        when( widgetDescriptor.getKey() ).thenReturn( widgetDescriptorKey );
        when( widgetDescriptor.getInterfaces() ).thenReturn( Set.of( "widgetInterface" ) );

        when( widgetDescriptorService.getByKey( eq( widgetDescriptorKey ) ) ).thenReturn( widgetDescriptor );

        final DescriptorKey adminToolDescriptorKey = DescriptorKey.from( ApplicationKey.from( "myapp" ), "toolName" );

        final AdminToolDescriptor adminToolDescriptor =
            AdminToolDescriptor.create().key( adminToolDescriptorKey ).addInterface( "admin.dashboard" ).build();

        when( adminToolDescriptorService.getByKey( eq( adminToolDescriptorKey ) ) ).thenReturn( adminToolDescriptor );

        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );
        when( webRequest.getEndpointPath() ).thenReturn( "/_/admin/widget/app/widgetName" );
        when( webRequest.getRawPath() ).thenReturn( "/admin/myapp/toolName/_/admin/widget/app/widgetName" );

        final ControllerScript controllerScript = mock( ControllerScript.class );
        when( controllerScript.execute( any( PortalRequest.class ) ) ).thenReturn( PortalResponse.create().build() );

        when( controllerScriptFactory.fromScript( any( ResourceKey.class ) ) ).thenReturn( controllerScript );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( webRequest ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "Widget [app:widgetName] is not mounted to admin tool [myapp:toolName]", ex.getMessage() );
    }
}
