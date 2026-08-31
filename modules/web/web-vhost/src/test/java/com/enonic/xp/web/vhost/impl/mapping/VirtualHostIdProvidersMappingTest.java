package com.enonic.xp.web.vhost.impl.mapping;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VirtualHostIdProvidersMappingTest
{
    private static final IdProviderKey PROVIDER_A = IdProviderKey.from( "aaa" );

    private static final IdProviderKey PROVIDER_B = IdProviderKey.from( "bbb" );

    @Test
    void defaultIdProviderFirst()
    {
        final VirtualHostIdProvidersMapping mapping = VirtualHostIdProvidersMapping.create()
            .addIdProvider( PROVIDER_A, Set.of( "login" ) )
            .addIdProvider( PROVIDER_B, Set.of( "autologin" ) )
            .setDefaultIdProvider( PROVIDER_B )
            .build();

        assertEquals( List.of( PROVIDER_B, PROVIDER_A ), List.copyOf( mapping.getIdProviders().keySet() ) );
        assertEquals( Set.of( "autologin" ), mapping.getIdProviders().get( PROVIDER_B ) );
        assertEquals( Set.of( "login" ), mapping.getIdProviders().get( PROVIDER_A ) );
    }

    @Test
    void defaultIdProviderKeepsFlowsAddedLater()
    {
        final VirtualHostIdProvidersMapping mapping = VirtualHostIdProvidersMapping.create()
            .setDefaultIdProvider( PROVIDER_B )
            .addIdProvider( PROVIDER_A, Set.of( "login" ) )
            .addIdProvider( PROVIDER_B, Set.of( "autologin" ) )
            .build();

        assertEquals( List.of( PROVIDER_B, PROVIDER_A ), List.copyOf( mapping.getIdProviders().keySet() ) );
        assertEquals( Set.of( "autologin" ), mapping.getIdProviders().get( PROVIDER_B ) );
    }

    @Test
    void addIdProviderKeyKeepsExplicitFlows()
    {
        final VirtualHostIdProvidersMapping mapping = VirtualHostIdProvidersMapping.create()
            .addIdProvider( PROVIDER_A, Set.of( "login" ) )
            .addIdProviderKey( PROVIDER_A )
            .build();

        assertEquals( Set.of( "login" ), mapping.getIdProviders().get( PROVIDER_A ) );
    }

    @Test
    void noFlowRestrictionByDefault()
    {
        final VirtualHostIdProvidersMapping mapping = VirtualHostIdProvidersMapping.create()
            .addIdProviderKey( PROVIDER_A )
            .addIdProvider( PROVIDER_B, null )
            .build();

        assertEquals( Set.of(), mapping.getIdProviders().get( PROVIDER_A ) );
        assertEquals( Set.of(), mapping.getIdProviders().get( PROVIDER_B ) );
    }
}
