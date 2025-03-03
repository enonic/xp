package com.enonic.xp.admin.impl.portal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseHandlerTest;
import com.enonic.xp.web.handler.WebHandlerChain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdminToolHandlerTest
    extends BaseHandlerTest
{

    private AdminToolHandler handler;

    private PortalRequest portalRequest;

    private PortalResponse portalResponse;

    private WebResponse webResponse;

    private WebHandlerChain chain;

    private HttpServletRequest rawRequest;

    private AdminToolDescriptorService adminToolDescriptorService;

    @BeforeEach
    public final void setup()
    {

        this.adminToolDescriptorService = mock( AdminToolDescriptorService.class );
        ControllerScript controllerScript = mock( ControllerScript.class );

        this.portalResponse = PortalResponse.create().build();
        when( controllerScript.execute( any( PortalRequest.class ) ) ).thenReturn( this.portalResponse );

        final ControllerScriptFactory controllerScriptFactory = mock( ControllerScriptFactory.class );
        when( controllerScriptFactory.fromScript( any( ResourceKey.class ) ) ).thenReturn( controllerScript );

        this.handler = new AdminToolHandler();
        this.handler.setAdminToolDescriptorService( this.adminToolDescriptorService );
        this.handler.setControllerScriptFactory( controllerScriptFactory );

        this.rawRequest = mock( HttpServletRequest.class );
        when( this.rawRequest.isUserInRole( Mockito.anyString() ) ).thenReturn( true );

        this.portalRequest = new PortalRequest();
        this.portalRequest.setRawRequest( this.rawRequest );
        this.portalRequest.setMode( RenderMode.ADMIN );
        final DescriptorKey defaultDescriptorKey = AdminToolPortalHandler.DEFAULT_DESCRIPTOR_KEY;
        this.portalRequest.setBaseUri( AdminToolPortalHandler.ADMIN_TOOL_BASE + "/" + defaultDescriptorKey.getApplicationKey() + "/" +
                                           defaultDescriptorKey.getName() );
        this.portalRequest.setApplicationKey( defaultDescriptorKey.getApplicationKey() );

        this.webResponse = WebResponse.create().build();

        this.chain = mock( WebHandlerChain.class );
    }

    @Test
    public void testCanHandle()
    {
        this.portalRequest.setRawPath( "/admin/webapp/tool/1" );
        assertTrue( this.handler.canHandle( this.portalRequest ) );
    }

    @Test
    public void testWithoutPermissions()
    {
        this.portalRequest.setRawPath( "/admin/webapp/tool/1" );
        when( this.rawRequest.isUserInRole( Mockito.anyString() ) ).thenReturn( false );
        assertThrows( WebException.class, () -> this.handler.doHandle( this.portalRequest, this.webResponse, this.chain ) );
    }

    @Test
    public void testWithNoDescriptor()
    {
        when( this.adminToolDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( null );
        this.portalRequest.setRawPath( "/admin/webapp/tool/1" );
        assertThrows( WebException.class, () -> this.handler.doHandle( this.portalRequest, this.webResponse, this.chain ) );
    }

    @Test
    public void testWithNoAccessToApplication()
    {
        this.mockDescriptor( DescriptorKey.from( "app:tool" ), false );
        this.portalRequest.setRawPath( "/admin/webapp/tool/1" );
        assertThrows( WebException.class, () -> this.handler.doHandle( this.portalRequest, this.webResponse, this.chain ) );
    }

    @Test
    public void test()
        throws Exception
    {
        this.mockDescriptor( DescriptorKey.from( "app:tool" ), true );
        this.portalRequest.setBaseUri( "/admin/webapp/tool" );
        this.portalRequest.setRawPath( "/admin/webapp/tool/1" );
        WebResponse response = this.handler.doHandle( this.portalRequest, this.webResponse, this.chain );
        assertEquals( this.portalResponse, response );
        assertEquals( "/admin/webapp/tool", this.portalRequest.getContextPath() );
    }

    @Test
    void testInvalidAdminToolMount()
    {
        this.portalRequest.setBaseUri( "/admin" );
        this.portalRequest.setRawPath( "/admin/tool" );
        WebException ex =
            assertThrows( WebException.class, () -> this.handler.doHandle( this.portalRequest, this.webResponse, this.chain ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "Invalid admin tool mount", ex.getMessage() );

        this.portalRequest.setRawPath( "/admin/tool/" );
        ex = assertThrows( WebException.class, () -> this.handler.doHandle( this.portalRequest, this.webResponse, this.chain ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "Invalid admin tool mount", ex.getMessage() );

        this.portalRequest.setRawPath( "/admin/" );
        ex = assertThrows( WebException.class, () -> this.handler.doHandle( this.portalRequest, this.webResponse, this.chain ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "Invalid admin tool mount", ex.getMessage() );
    }

    private void mockDescriptor( DescriptorKey descriptorKey, boolean hasAccess )
    {
        AdminToolDescriptor descriptor = mock( AdminToolDescriptor.class );
        when( descriptor.getKey() ).thenReturn( descriptorKey );
        when( descriptor.isAccessAllowed( any( PrincipalKeys.class ) ) ).thenReturn( hasAccess );
        when( this.adminToolDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( descriptor );
    }
}
