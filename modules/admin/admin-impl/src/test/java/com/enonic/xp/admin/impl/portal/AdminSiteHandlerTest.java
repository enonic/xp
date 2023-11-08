package com.enonic.xp.admin.impl.portal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.BaseHandlerTest;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdminSiteHandlerTest
    extends BaseHandlerTest
{
    private AdminSiteHandler handler;

    private WebRequest request;

    private WebResponse response;

    @BeforeEach
    public final void setup()
        throws Exception
    {
        final ExceptionMapper exceptionMapper = Mockito.mock( ExceptionMapper.class );
        final ExceptionRenderer exceptionRenderer = Mockito.mock( ExceptionRenderer.class );

        this.handler = new AdminSiteHandler();
        this.handler.setExceptionMapper( exceptionMapper );
        this.handler.setExceptionRenderer( exceptionRenderer );
        this.handler.activate( mock( AdminConfig.class, invocation -> invocation.getMethod().getDefaultValue() ) );

        final HttpServletRequest rawRequest = Mockito.mock( HttpServletRequest.class );

        this.request = new WebRequest();
        this.request.setRawRequest( rawRequest );

        this.response = WebResponse.create().build();

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getIdProviderKeys() ).thenReturn( IdProviderKeys.from( "otherEnabledIdProvider" ) );

        VirtualHostHelper.setVirtualHost( rawRequest, initVirtualHost( rawRequest, virtualHost ) );
    }

    @Test
    public void testCanHandle()
    {
        this.request.setRawPath( "/admin/site/repo/master/content/1" );
        assertTrue( this.handler.canHandle( this.request ) );
    }

    @Test
    public void testCanHandleRootContent()
    {
        this.request.setRawPath( "/admin/site/repo/master" );
        assertTrue( this.handler.canHandle( this.request ) );

        this.request.setRawPath( "/admin/site/repo/master/" );
        assertTrue( this.handler.canHandle( this.request ) );
    }

    @Test
    public void testCannotHandle()
    {
        this.request.setRawPath( "/admin/repo/master/content/1" );
        assertFalse( this.handler.canHandle( this.request ) );
    }

    @Test
    public void testCreatePortalRequestWithoutMode()
    {
        this.request.setRawPath( "/admin/site/repo/master/content/1" );
        assertThrows( WebException.class, () -> this.handler.createPortalRequest( this.request, this.response ) );
    }

    @Test
    public void testCreatePortalRequest()
    {
        this.request.setRawPath( "/admin/site/edit/repo/master/content/1" );
        PortalRequest portalRequest = this.handler.createPortalRequest( this.request, this.response );

        assertEquals( "/admin/site/edit", portalRequest.getBaseUri() );
        assertEquals( "com.enonic.cms.repo", portalRequest.getRepositoryId().toString() );
        assertEquals( "master", portalRequest.getBranch().toString() );
        assertEquals( "/content/1", portalRequest.getContentPath().toString() );
        assertEquals( "edit", portalRequest.getMode().toString() );
    }

    @Test
    public void testInlineAssetRequest()
    {
        this.request.setRawPath( "/admin/site/inline/repo/draft/_/asset/com.enonic.app.superhero:1622131535374/css/style.css" );
        PortalRequest portalRequest = this.handler.createPortalRequest( this.request, this.response );

        assertEquals( "/admin/site/inline", portalRequest.getBaseUri() );
        assertEquals( "com.enonic.cms.repo", portalRequest.getRepositoryId().toString() );
        assertEquals( "draft", portalRequest.getBranch().toString() );
        assertEquals( "/", portalRequest.getContentPath().toString() );
        assertEquals( RenderMode.INLINE, portalRequest.getMode() );
    }

    @Test
    public void testCreatePortalRequestRootContentPath()
    {
        this.request.setRawPath( "/admin/site/edit/repo/master" );
        PortalRequest portalRequest = this.handler.createPortalRequest( this.request, this.response );

        assertEquals( "/admin/site/edit", portalRequest.getBaseUri() );
        assertEquals( "com.enonic.cms.repo", portalRequest.getRepositoryId().toString() );
        assertEquals( "master", portalRequest.getBranch().toString() );
        assertEquals( "/", portalRequest.getContentPath().toString() );
        assertEquals( RenderMode.EDIT, portalRequest.getMode() );
    }

    @Test
    public void testCreatePortalRequestEmptyContentPath()
    {
        this.request.setRawPath( "/admin/site/edit/repo/master/" );
        PortalRequest portalRequest = this.handler.createPortalRequest( this.request, this.response );

        assertEquals( "/admin/site/edit", portalRequest.getBaseUri() );
        assertEquals( "com.enonic.cms.repo", portalRequest.getRepositoryId().toString() );
        assertEquals( "master", portalRequest.getBranch().toString() );
        assertEquals( "/", portalRequest.getContentPath().toString() );
        assertEquals( RenderMode.EDIT, portalRequest.getMode() );
    }

    @Test
    public void testCreatePortalRequestWithVhostContext()
    {
        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getIdProviderKeys() ).thenReturn( IdProviderKeys.from( "myidprovider" ) );
        when( virtualHost.getDefaultIdProviderKey() ).thenReturn( IdProviderKey.from( "myidprovider" ) );
        when( virtualHost.getContext() ).thenReturn(
            Map.of( RepositoryId.class.getName(), "com.enonic.cms.myrepo", Branch.class.getName(), "draft" ) );

        VirtualHostHelper.setVirtualHost( request.getRawRequest(), initVirtualHost( request.getRawRequest(), virtualHost ) );

        PortalRequest portalRequest = new PortalRequest(request);
        assertEquals( "com.enonic.cms.myrepo", portalRequest.getRepositoryId().toString() );
        assertEquals( "draft", portalRequest.getBranch().toString() );
    }

    private VirtualHost initVirtualHost( final HttpServletRequest rawRequest, final VirtualHost virtualHost )
    {
        when( rawRequest.getAttribute( isA( String.class ) ) ).thenAnswer(
            ( InvocationOnMock invocation ) -> VirtualHost.class.getName().equals( invocation.getArguments()[0] )
                ? virtualHost
                : generateDefaultVirtualHost() );

        return virtualHost;
    }

    private VirtualHost generateDefaultVirtualHost()
    {
        VirtualHost result = mock( VirtualHost.class );

        when( result.getHost() ).thenReturn( "host" );
        when( result.getSource() ).thenReturn( "/" );
        when( result.getTarget() ).thenReturn( "/" );
        when( result.getIdProviderKeys() ).thenReturn( IdProviderKeys.from( IdProviderKey.system() ) );

        return result;
    }
}
