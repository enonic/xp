package com.enonic.xp.portal.impl.mapper;

import org.junit.jupiter.api.Test;

import jakarta.servlet.http.Cookie;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.MapSerializableAssert;

class PortalResponseMapperTest
{
    private final MapSerializableAssert assertHelper = new MapSerializableAssert( PortalResponseMapperTest.class );

    @Test
    void cookies()
        throws Exception
    {
        final Cookie simpleCookie = new Cookie( "simple", "value" );

        final Cookie complexCookie = new Cookie( "complex", "some value" );
        complexCookie.setMaxAge( 100 );
        complexCookie.setPath( "/admin" );
        complexCookie.setDomain( "enonic.com" );
        complexCookie.setSecure( true );
        complexCookie.setHttpOnly( true );

        final Cookie toDeleteCookie = new Cookie( "toDelete", "value" );
        toDeleteCookie.setMaxAge( 0 );

        final PortalResponse response = PortalResponse.create().
            cookie( simpleCookie ).
            cookie( complexCookie ).
            cookie( toDeleteCookie ).
            build();

        assertHelper.assertJson( "response-cookies.json", new PortalResponseMapper( response ) );
    }
}
