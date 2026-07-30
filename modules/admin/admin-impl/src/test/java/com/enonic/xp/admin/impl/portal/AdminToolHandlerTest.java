package com.enonic.xp.admin.impl.portal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.admin.impl.portal.extension.AdminExtensionResponseProcessorExecutor;
import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.trace.TestTrace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseHandlerTest;
import com.enonic.xp.web.handler.WebHandlerChain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminToolHandlerTest
    extends BaseHandlerTest
{
    private AdminToolHandler handler;

    private PortalRequest portalRequest;

    private PortalResponse portalResponse;

    private WebResponse webResponse;

    private WebHandlerChain chain;

    private HttpServletRequest rawRequest;

    private AdminToolDescriptorService adminToolDescriptorService;

    private ControllerScript controllerScript;

    private AdminExtensionResponseProcessorExecutor extensionResponseProcessorExecutor;

    private PostProcessor postProcessor;

    @BeforeEach
    public final void setup()
    {

        this.adminToolDescriptorService = mock( AdminToolDescriptorService.class );
        this.controllerScript = mock( ControllerScript.class );

        this.portalResponse = PortalResponse.create().build();
        when( this.controllerScript.execute( any( PortalRequest.class ) ) ).thenReturn( this.portalResponse );

        final ControllerScriptFactory controllerScriptFactory = mock( ControllerScriptFactory.class );
        when( controllerScriptFactory.fromScript( any( ResourceKey.class ) ) ).thenReturn( this.controllerScript );

        this.extensionResponseProcessorExecutor = mock( AdminExtensionResponseProcessorExecutor.class );
        when( this.extensionResponseProcessorExecutor.execute( any(), any(), any() ) ).thenAnswer( inv -> inv.getArgument( 2 ) );

        this.postProcessor = mock( PostProcessor.class );

        this.handler = new AdminToolHandler();
        this.handler.setAdminToolDescriptorService( this.adminToolDescriptorService );
        this.handler.setControllerScriptFactory( controllerScriptFactory );
        this.handler.setExtensionResponseProcessorExecutor( this.extensionResponseProcessorExecutor );
        this.handler.setPostProcessor( this.postProcessor );

        this.rawRequest = mock( HttpServletRequest.class );
        when( this.rawRequest.isUserInRole( Mockito.anyString() ) ).thenReturn( true );

        this.portalRequest = new PortalRequest();
        this.portalRequest.setRawRequest( this.rawRequest );
        final DescriptorKey defaultDescriptorKey = AdminToolPortalHandler.DEFAULT_DESCRIPTOR_KEY;
        this.portalRequest.setBaseUri( AdminToolPortalHandler.ADMIN_TOOL_BASE + "/" + defaultDescriptorKey.getApplicationKey() + "/" +
                                           defaultDescriptorKey.getName() );
        this.portalRequest.setApplicationKey( defaultDescriptorKey.getApplicationKey() );

        this.webResponse = WebResponse.create().build();

        this.chain = mock( WebHandlerChain.class );
    }

    @Test
    void testCanHandle()
    {
        this.portalRequest.setBaseUri( "/admin/webapp/tool" );
        assertTrue( this.handler.canHandle( this.portalRequest ) );

        this.portalRequest.setBaseUri( "/admin/" );
        assertTrue( this.handler.canHandle( this.portalRequest ) );

        this.portalRequest.setBaseUri( "/admin" );
        assertTrue( this.handler.canHandle( this.portalRequest ) );

        this.portalRequest.setBaseUri( "/admins" );
        assertFalse( this.handler.canHandle( this.portalRequest ) );
    }

    @Test
    void testWithoutPermissions()
    {
        this.portalRequest.setRawPath( "/admin/webapp/tool/1" );
        when( this.rawRequest.isUserInRole( Mockito.anyString() ) ).thenReturn( false );
        assertThrows( WebException.class, () -> this.handler.doHandle( this.portalRequest, this.webResponse, this.chain ) );
    }

    @Test
    void testWithNoDescriptor()
    {
        when( this.adminToolDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( null );
        this.portalRequest.setRawPath( "/admin/webapp/tool/1" );
        assertThrows( WebException.class, () -> this.handler.doHandle( this.portalRequest, this.webResponse, this.chain ) );
    }

    @Test
    void testWithNoAccessToApplication()
    {
        this.mockDescriptor( DescriptorKey.from( "app:tool" ), false );
        this.portalRequest.setRawPath( "/admin/webapp/tool/1" );
        assertThrows( WebException.class, () -> this.handler.doHandle( this.portalRequest, this.webResponse, this.chain ) );
    }

    @Test
    void test()
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
    void testRecordsTraceAttributes()
        throws Exception
    {
        this.mockDescriptor( DescriptorKey.from( "app:tool" ), true );
        this.portalRequest.setBaseUri( "/admin/webapp/tool" );
        this.portalRequest.setRawPath( "/admin/webapp/tool/1" );
        this.portalRequest.setMethod( HttpMethod.GET );
        this.portalRequest.setPath( "/admin/webapp/tool/1" );
        this.portalRequest.setHost( "localhost" );

        // outside OSGi the @Traced wrapper is inert; a manually bound trace exercises the attribute enrichment code
        final TestTrace trace = TestTrace.of( "portalRequest" );
        final WebResponse response =
            Tracer.traceEx( trace, () -> this.handler.doHandle( this.portalRequest, this.webResponse, this.chain ) );

        assertEquals( this.portalResponse, response );
        assertEquals( "/admin/webapp/tool/1", trace.get( "path" ) );
        assertEquals( "GET", trace.get( "method" ) );
        assertEquals( "localhost", trace.get( "host" ) );
        assertEquals( 200L, trace.get( "status" ) );
        assertInstanceOf( String.class, trace.get( "type" ) );
        assertInstanceOf( Long.class, trace.get( "size" ) );
    }

    @Test
    void testExtensionProcessorResponseWithContributionsIsPostProcessed()
        throws Exception
    {
        this.mockDescriptor( DescriptorKey.from( "app:tool" ), true );
        this.portalRequest.setBaseUri( "/admin/webapp/tool" );
        this.portalRequest.setRawPath( "/admin/webapp/tool/1" );

        final PortalResponse withContributions =
            PortalResponse.create().contribution( HtmlTag.HEAD_END, "<script src=\"widget.js\"></script>" ).build();
        when( this.extensionResponseProcessorExecutor.execute( any(), any(), any() ) ).thenReturn( withContributions );

        final PortalResponse postProcessed = PortalResponse.create().body( "post-processed" ).build();
        when( this.postProcessor.processResponseContributions( any( PortalRequest.class ), any( PortalResponse.class ) ) ).thenReturn(
            postProcessed );

        final WebResponse response = this.handler.doHandle( this.portalRequest, this.webResponse, this.chain );
        assertEquals( postProcessed, response );
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
    }

    private void mockDescriptor( DescriptorKey descriptorKey, boolean hasAccess )
    {
        AdminToolDescriptor descriptor = mock( AdminToolDescriptor.class );
        when( descriptor.getKey() ).thenReturn( descriptorKey );
        when( descriptor.isAccessAllowed( any( PrincipalKeys.class ) ) ).thenReturn( hasAccess );
        when( this.adminToolDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( descriptor );
    }
}
