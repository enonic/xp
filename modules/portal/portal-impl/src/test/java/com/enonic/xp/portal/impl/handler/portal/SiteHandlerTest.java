package com.enonic.xp.portal.impl.handler.portal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.security.IdProviderKey;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

public class SiteHandlerTest
{
    private SiteHandler handler;

    private WebRequest request;

    private WebResponse response;

    @BeforeEach
    public final void setup()
        throws Exception
    {
        this.handler = new SiteHandler( mock( ContentService.class ), mock( ProjectService.class ), mock( ExceptionMapper.class ),
                                        mock( ExceptionRenderer.class ) );
        this.handler.activate( mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() ) );

        final HttpServletRequest rawRequest = mock( HttpServletRequest.class );
        this.request = new WebRequest();
        this.request.setRawRequest( rawRequest );
        this.request.setRawPath( "/site/myrepo/draft/mycontent" );

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
}
