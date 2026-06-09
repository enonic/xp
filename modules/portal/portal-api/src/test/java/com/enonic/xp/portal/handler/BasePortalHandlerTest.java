package com.enonic.xp.portal.handler;

import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.WebHandlerChain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BasePortalHandlerTest
{
    @Test
    void emits_csp_header_when_policy_non_empty()
        throws Exception
    {
        final PortalRequest portalRequest = newPortalRequest();
        portalRequest.getContentSecurityPolicy().add( "script-src", "'self'" );

        final WebResponse response = doHandle( portalRequest, PortalResponse.create().status( HttpStatus.OK ).build() );

        assertThat( response.getHeaders() ).containsEntry( "Content-Security-Policy", "script-src 'self'" );
    }

    @Test
    void no_csp_header_when_policy_never_accessed()
        throws Exception
    {
        final PortalRequest portalRequest = newPortalRequest();

        final WebResponse response = doHandle( portalRequest, PortalResponse.create().status( HttpStatus.OK ).build() );

        assertThat( response.getHeaders() ).doesNotContainKey( "Content-Security-Policy" );
    }

    @Test
    void no_csp_header_when_policy_accessed_but_empty()
        throws Exception
    {
        final PortalRequest portalRequest = newPortalRequest();
        portalRequest.getContentSecurityPolicy();

        final WebResponse response = doHandle( portalRequest, PortalResponse.create().status( HttpStatus.OK ).build() );

        assertThat( response.getHeaders() ).doesNotContainKey( "Content-Security-Policy" );
    }

    @Test
    void preserves_existing_csp_header_on_response()
        throws Exception
    {
        final PortalRequest portalRequest = newPortalRequest();
        portalRequest.getContentSecurityPolicy().add( "script-src", "'self'" );

        final WebResponse seedResponse = PortalResponse.create()
            .status( HttpStatus.OK )
            .header( "Content-Security-Policy", "default-src 'none'" )
            .build();
        final WebResponse response = doHandle( portalRequest, seedResponse );

        assertThat( response.getHeaders() ).containsEntry( "Content-Security-Policy", "default-src 'none'" );
    }

    @Test
    void preserves_portal_response_type_when_adding_csp_header()
        throws Exception
    {
        final PortalRequest portalRequest = newPortalRequest();
        portalRequest.getContentSecurityPolicy().add( "script-src", "'self'" );

        final WebResponse response = doHandle( portalRequest, PortalResponse.create().status( HttpStatus.OK ).build() );

        assertThat( response ).isInstanceOf( PortalResponse.class );
    }

    @Test
    void emits_report_only_header_when_policy_flagged_report_only()
        throws Exception
    {
        final PortalRequest portalRequest = newPortalRequest();
        portalRequest.getContentSecurityPolicy().reportOnly( true ).add( "script-src", "'self'" );

        final WebResponse response = doHandle( portalRequest, PortalResponse.create().status( HttpStatus.OK ).build() );

        assertThat( response.getHeaders() ).containsEntry( "Content-Security-Policy-Report-Only", "script-src 'self'" )
            .doesNotContainKey( "Content-Security-Policy" );
    }

    @Test
    void preserves_existing_report_only_header_on_response()
        throws Exception
    {
        final PortalRequest portalRequest = newPortalRequest();
        portalRequest.getContentSecurityPolicy().reportOnly( true ).add( "script-src", "'self'" );

        final WebResponse seedResponse = PortalResponse.create()
            .status( HttpStatus.OK )
            .header( "Content-Security-Policy-Report-Only", "default-src 'none'" )
            .build();
        final WebResponse response = doHandle( portalRequest, seedResponse );

        assertThat( response.getHeaders() ).containsEntry( "Content-Security-Policy-Report-Only", "default-src 'none'" );
    }

    @Test
    void emits_csp_header_on_exception_path()
        throws Exception
    {
        final PortalRequest portalRequest = newPortalRequest();
        portalRequest.getContentSecurityPolicy().add( "script-src", "'self'" );

        final WebHandlerChain chain = mock( WebHandlerChain.class );
        when( chain.handle( any(), any() ) ).thenThrow( new RuntimeException( "boom" ) );

        final ExceptionRenderer exceptionRenderer = mock( ExceptionRenderer.class );
        when( exceptionRenderer.render( any(), any() ) ).thenReturn(
            PortalResponse.create().status( HttpStatus.INTERNAL_SERVER_ERROR ).build() );

        final TestHandler handler = new TestHandler( exceptionRenderer );
        final WebResponse response = handler.doHandle( portalRequest, WebResponse.create().build(), chain );

        assertThat( response.getHeaders() ).containsEntry( "Content-Security-Policy", "script-src 'self'" );
    }

    private static PortalRequest newPortalRequest()
    {
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setRawRequest( mock( HttpServletRequest.class ) );
        return portalRequest;
    }

    private static WebResponse doHandle( final PortalRequest portalRequest, final WebResponse seedResponse )
        throws Exception
    {
        final WebHandlerChain chain = mock( WebHandlerChain.class );
        when( chain.handle( any(), any() ) ).thenReturn( seedResponse );

        final ExceptionRenderer exceptionRenderer = mock( ExceptionRenderer.class );
        when( exceptionRenderer.maybeThrow( any(), any() ) ).thenAnswer( inv -> inv.getArgument( 1 ) );

        final TestHandler handler = new TestHandler( exceptionRenderer );
        return handler.doHandle( portalRequest, WebResponse.create().build(), chain );
    }

    private static final class TestHandler
        extends BasePortalHandler
    {
        TestHandler( final ExceptionRenderer exceptionRenderer )
        {
            this.exceptionRenderer = exceptionRenderer;
        }

        @Override
        protected boolean canHandle( final WebRequest webRequest )
        {
            return true;
        }

        @Override
        protected PortalRequest createPortalRequest( final WebRequest webRequest, final WebResponse webResponse )
        {
            return new PortalRequest( webRequest );
        }
    }
}
