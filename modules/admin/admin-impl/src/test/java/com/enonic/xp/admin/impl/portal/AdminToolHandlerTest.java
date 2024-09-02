package com.enonic.xp.admin.impl.portal;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseHandlerTest;
import com.enonic.xp.web.handler.WebHandlerChain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        this.adminToolDescriptorService = Mockito.mock( AdminToolDescriptorService.class );
        ControllerScript controllerScript = Mockito.mock( ControllerScript.class );

        this.portalResponse = PortalResponse.create().build();
        Mockito.when( controllerScript.execute( Mockito.any( PortalRequest.class ) ) ).thenReturn( this.portalResponse );

        final ControllerScriptFactory controllerScriptFactory = Mockito.mock( ControllerScriptFactory.class );
        Mockito.when( controllerScriptFactory.fromScript( Mockito.any( ResourceKey.class ) ) ).thenReturn( controllerScript );

        this.handler = new AdminToolHandler();
        this.handler.setAdminToolDescriptorService( this.adminToolDescriptorService );
        this.handler.setControllerScriptFactory( controllerScriptFactory );

        this.rawRequest = Mockito.mock( HttpServletRequest.class );
        Mockito.when( this.rawRequest.isUserInRole( Mockito.anyString() ) ).thenReturn( true );

        this.portalRequest = new PortalRequest();
        this.portalRequest.setRawRequest( this.rawRequest );
        this.portalRequest.setMode( RenderMode.ADMIN );
        final DescriptorKey defaultDescriptorKey = AdminToolPortalHandler.DEFAULT_DESCRIPTOR_KEY;
        this.portalRequest.setBaseUri( AdminToolPortalHandler.ADMIN_TOOL_BASE + "/" + defaultDescriptorKey.getApplicationKey() + "/" +
                                           defaultDescriptorKey.getName() );
        this.portalRequest.setApplicationKey( defaultDescriptorKey.getApplicationKey() );

        this.webResponse = WebResponse.create().build();

        this.chain = Mockito.mock( WebHandlerChain.class );
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
        Mockito.when( this.rawRequest.isUserInRole( Mockito.anyString() ) ).thenReturn( false );
        assertThrows( WebException.class, () -> this.handler.doHandle( this.portalRequest, this.webResponse, this.chain ) );
    }

    @Test
    public void testWithNoDescriptor()
    {
        Mockito.when( this.adminToolDescriptorService.getByKey( Mockito.any( DescriptorKey.class ) ) ).thenReturn( null );
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

    private void mockDescriptor( DescriptorKey descriptorKey, boolean hasAccess )
    {
        AdminToolDescriptor descriptor = Mockito.mock( AdminToolDescriptor.class );
        Mockito.when( descriptor.getKey() ).thenReturn( descriptorKey );
        Mockito.when( descriptor.isAccessAllowed( Mockito.any( PrincipalKeys.class ) ) ).thenReturn( hasAccess );
        Mockito.when( this.adminToolDescriptorService.getByKey( Mockito.any( DescriptorKey.class ) ) ).thenReturn( descriptor );
    }
}
