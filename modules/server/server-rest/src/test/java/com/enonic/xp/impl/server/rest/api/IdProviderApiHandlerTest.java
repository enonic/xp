package com.enonic.xp.impl.server.rest.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderConfig;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviders;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebResponse;

import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.request;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IdProviderApiHandlerTest
{
    private SecurityService securityService;

    private IdProviderApiHandler handler;

    @BeforeEach
    void setUp()
    {
        securityService = mock( SecurityService.class );
        handler = new IdProviderApiHandler( securityService );
    }

    @Test
    void listHidesConfig()
    {
        final PropertyTree config = new PropertyTree();
        config.addString( "clientSecret", "hunter2" );

        when( securityService.getIdProviders() ).thenReturn( IdProviders.from( IdProvider.create()
                                                                                    .key( IdProviderKey.from( "oidc" ) )
                                                                                    .displayName( "OIDC" )
                                                                                    .description( "Corporate login" )
                                                                                    .idProviderConfig( IdProviderConfig.create()
                                                                                                           .applicationKey( ApplicationKey.from(
                                                                                                               "com.enonic.app.oidcidprovider" ) )
                                                                                                           .config( config )
                                                                                                           .build() )
                                                                                    .build(), IdProvider.create()
                                                                                    .key( IdProviderKey.system() )
                                                                                    .displayName( "System" )
                                                                                    .build() ) );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/server:idprovider" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        final String body = String.valueOf( response.getBody() );
        assertEquals( "{\"idProviders\":[{\"key\":\"oidc\",\"displayName\":\"OIDC\",\"description\":\"Corporate login\"," +
                          "\"applicationKey\":\"com.enonic.app.oidcidprovider\"},{\"key\":\"system\",\"displayName\":\"System\"}]}",
                      body );
        assertFalse( body.contains( "hunter2" ) );
    }
}
