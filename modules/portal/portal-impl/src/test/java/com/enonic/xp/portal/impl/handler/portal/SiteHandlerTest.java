package com.enonic.xp.portal.impl.handler.portal;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;

import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SiteHandlerTest
{
    private SiteHandler handler;

    private WebRequest request;

    private WebResponse response;

    @BeforeEach
    public final void setup()
        throws Exception
    {
        this.handler = new SiteHandler();
        this.handler.activate( mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() ) );
        this.handler.setWebExceptionMapper( mock( ExceptionMapper.class ) );
        this.handler.setExceptionRenderer( mock( ExceptionRenderer.class ) );

        final HttpServletRequest rawRequest = mock( HttpServletRequest.class );
        this.request = new WebRequest();
        this.request.setRawRequest( rawRequest );
        this.request.setRawPath( "/site/myrepo/draft/mycontent" );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getIdProviderKeys() ).thenReturn( IdProviderKeys.from( "otherEnabledIdProvider" ) );

        VirtualHostHelper.setVirtualHost( request.getRawRequest(), initVirtualHost( request.getRawRequest(), virtualHost ) );

        this.response = WebResponse.create().build();
    }

    @Test
    public void testCreateRequestForAnonymousDraft()
    {
        try
        {
            ContextBuilder.create()
                .authInfo(
                    AuthenticationInfo.copyOf( AuthenticationInfo.unAuthenticated() ).principals( PrincipalKey.ofAnonymous() ).build() )
                .build()
                .callWith( () -> handler.createPortalRequest( request, response ) );
        }
        catch ( WebException ex )
        {
            assertEquals( HttpStatus.UNAUTHORIZED, ex.getStatus() );
            assertEquals( "You don't have permission to access this resource", ex.getMessage() );
        }

        PortalRequest result = ContextBuilder.create()
            .authInfo( AuthenticationInfo.create()
                           .principals( RoleKeys.ADMIN )
                           .user( User.create().key( PrincipalKey.ofSuperUser() ).login( PrincipalKey.ofSuperUser().getId() ).build() )
                           .build() )
            .build()
            .callWith( () -> handler.createPortalRequest( request, response ) );

        assertNotNull( result );

        result = ContextBuilder.create()
            .authInfo( AuthenticationInfo.create()
                           .principals( RoleKeys.ADMIN_LOGIN )
                           .user( User.create()
                                      .key( PrincipalKey.ofUser( IdProviderKey.from( "enonic" ), "user1" ) )
                                      .login( "user1" )
                                      .build() )
                           .build() )
            .build()
            .callWith( () -> handler.createPortalRequest( request, response ) );

        assertNotNull( result );
    }

    @Test
    public void testCreatePortalRequestForAssetAndIdproviderEndpoints()
    {
        this.request.setRawPath( "/site/myrepo/draft/mycontent/_/asset/demo/css/main.css" );
        this.request.setEndpointPath( "/_/asset/demo/css/main.css" );

        assertNotNull( createRequestWithAuthenticatedUser() );

        this.request.setRawPath( "/site/default/draft/_/idprovider/system/login" );
        this.request.setEndpointPath( "/_/idprovider/system/login" );

        assertNotNull( createRequestWithAuthenticatedUser() );
    }

    private PortalRequest createRequestWithAuthenticatedUser()
    {
        return ContextBuilder.create()
            .authInfo( AuthenticationInfo.copyOf( AuthenticationInfo.unAuthenticated() ).principals( PrincipalKey.ofAnonymous() ).build() )
            .build()
            .callWith( () -> handler.createPortalRequest( request, response ) );
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
