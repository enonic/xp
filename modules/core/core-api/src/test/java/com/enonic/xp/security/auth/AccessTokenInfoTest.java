package com.enonic.xp.security.auth;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccessTokenInfoTest
{
    @Test
    void issuer_is_required()
    {
        assertThrows( NullPointerException.class, () -> AccessTokenInfo.create().build() );
    }

    @Test
    void the_rest_defaults_to_absent()
    {
        final AccessTokenInfo info = AccessTokenInfo.create().issuer( "xp" ).build();
        assertEquals( "xp", info.getIssuer() );
        assertNull( info.getClientId() );
        assertNull( info.getGrantId() );
        assertEquals( Set.of(), info.getScope() );
        assertEquals( Set.of(), info.getAudience() );
    }

    @Test
    void scopes_and_audiences_accumulate()
    {
        final AccessTokenInfo info = AccessTokenInfo.create()
            .issuer( "https://issuer.example" )
            .clientId( "cli" )
            .grantId( "grant-1" )
            .scope( List.of( "content:read" ) )
            .scope( List.of( "content:read", "profile" ) )
            .audience( List.of( "xp-api" ) )
            .build();
        assertEquals( "cli", info.getClientId() );
        assertEquals( "grant-1", info.getGrantId() );
        assertEquals( Set.of( "content:read", "profile" ), info.getScope() );
        assertEquals( Set.of( "xp-api" ), info.getAudience() );
    }

    @Test
    void equality_is_by_value()
    {
        final AccessTokenInfo a = AccessTokenInfo.create().issuer( "xp" ).scope( Set.of( "a", "b" ) ).audience( Set.of( "api" ) ).build();
        final AccessTokenInfo b = AccessTokenInfo.create().issuer( "xp" ).scope( List.of( "b", "a" ) ).audience( Set.of( "api" ) ).build();
        assertEquals( a, b );
        assertEquals( a.hashCode(), b.hashCode() );
        assertNotEquals( a, AccessTokenInfo.create().issuer( "other" ).scope( Set.of( "a", "b" ) ).audience( Set.of( "api" ) ).build() );
        assertNotEquals( a, AccessTokenInfo.create().issuer( "xp" ).scope( Set.of( "a" ) ).audience( Set.of( "api" ) ).build() );
        assertTrue( a.toString().contains( "issuer=xp" ) );
    }
}
