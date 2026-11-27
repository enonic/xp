package com.enonic.xp.web;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WebRequestTest
{
    @Test
    void idProviderSettings()
    {
        final IdProviderKey key = IdProviderKey.from( "enonic" );
        final VirtualHostIdProvider settings = VirtualHostIdProvider.create().build();
        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getIdProviders() ).thenReturn( Map.of( key, settings ) );

        final WebRequest request = new WebRequest();
        assertNull( request.getIdProviderSettings() );

        request.setIdProvider( IdProvider.create().key( key ).build() );
        assertNull( request.getIdProviderSettings() );

        request.setVirtualHost( virtualHost );
        assertSame( settings, request.getIdProviderSettings() );
        assertSame( settings, new WebRequest( request ).getIdProviderSettings() );

        request.setIdProvider( IdProvider.create().key( IdProviderKey.from( "other" ) ).build() );
        assertNull( request.getIdProviderSettings() );

        request.setIdProvider( IdProvider.create().build() );
        assertNull( request.getIdProviderSettings() );
    }
}
