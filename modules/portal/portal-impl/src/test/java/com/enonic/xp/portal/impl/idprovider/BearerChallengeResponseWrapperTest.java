package com.enonic.xp.portal.impl.idprovider;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.vhost.VirtualHostHelper;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostIdProvidersMapping;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BearerChallengeResponseWrapperTest
{
    private final HttpServletRequest request = mock( HttpServletRequest.class );

    private final HttpServletResponse response = mock( HttpServletResponse.class );

    @Test
    void a_bearer_challenge_on_a_management_mapping_names_the_resource_metadata()
    {
        final Map<String, Object> attributes = new HashMap<>();
        doAnswer( i -> attributes.put( i.getArgument( 0 ), i.getArgument( 1 ) ) ).when( request ).setAttribute( any(), any() );
        when( request.getAttribute( any() ) ).thenAnswer( i -> attributes.get( i.<String>getArgument( 0 ) ) );
        VirtualHostHelper.setVirtualHost( request, new VirtualHostMapping( "mgmt", "mgmt.example.com", "/", "/",
                                                                           VirtualHostIdProvidersMapping.create().build(), 0, Map.of(),
                                                                           DispatchConstants.MANAGEMENT_CONNECTOR ) );
        VirtualHostHelper.setRequestedPath( request, "/repo:list" );
        when( request.getScheme() ).thenReturn( "https" );
        when( request.getServerName() ).thenReturn( "mgmt.example.com" );
        when( request.getServerPort() ).thenReturn( 443 );

        final BearerChallengeResponseWrapper wrapper = new BearerChallengeResponseWrapper( request, response );
        wrapper.addHeader( "WWW-Authenticate", "Basic" );
        wrapper.addHeader( "WWW-Authenticate", "Bearer" );
        wrapper.setHeader( "Content-Type", "text/html" );

        verify( response ).addHeader( "WWW-Authenticate", "Basic" );
        verify( response ).addHeader( "WWW-Authenticate",
                                      "Bearer resource_metadata=\"https://mgmt.example.com/.well-known/oauth-protected-resource/repo:list\"" );
        verify( response ).setHeader( "Content-Type", "text/html" );
    }

    @Test
    void a_bearer_challenge_without_a_mapping_is_left_as_it_is()
    {
        new BearerChallengeResponseWrapper( request, response ).addHeader( "WWW-Authenticate", "Bearer" );

        verify( response ).addHeader( "WWW-Authenticate", "Bearer" );
    }
}
