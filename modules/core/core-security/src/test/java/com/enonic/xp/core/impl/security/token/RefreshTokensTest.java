package com.enonic.xp.core.impl.security.token;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokensTest
{
    private static final byte[] KEY = RefreshTokens.newGrantKey();

    private static final byte[] SECRET = new byte[64];

    @Test
    void token_is_a_secret_the_grant_id_the_generation_and_a_tag()
    {
        final String token = RefreshTokens.newToken( KEY, "019a-grant", 7 );
        assertThat( token ).matches( "[A-Za-z0-9_-]{43}\\.019a-grant\\.7\\.[A-Za-z0-9_-]{43}" );
        final RefreshTokens.Parts parts = RefreshTokens.parse( token ).orElseThrow();
        assertThat( parts.grantId() ).isEqualTo( "019a-grant" );
        assertThat( parts.generation() ).isEqualTo( 7 );
        assertThat( RefreshTokens.isIssuedUnder( parts, KEY ) ).isTrue();
        assertThat( RefreshTokens.newToken( KEY, "g", 1 ) ).isNotEqualTo( RefreshTokens.newToken( KEY, "g", 1 ) );
    }

    @Test
    void token_is_parsed_strictly()
    {
        final String token = RefreshTokens.newToken( KEY, "g", 1 );
        for ( String bad : new String[]{"", "abc", token.substring( 1 ), token + "x", token.replace( ".g.", ".g/h." ), token.replace( ".1.", ".x." ),
            token.replace( ".1.", ".-1." ), token.replace( ".1.", ".01." ), token.replace( ".1.", ".+1." ),
            token.replace( ".1.", "." + "9".repeat( 19 ) + "." ), token.substring( 0, token.lastIndexOf( '.' ) )} )
        {
            assertThat( RefreshTokens.parse( bad ) ).as( bad ).isEmpty();
        }
        assertThat( RefreshTokens.parse( token.replace( ".1.", "." + "9".repeat( 18 ) + "." ) ) ).isPresent();
        assertThat( RefreshTokens.parse( RefreshTokens.newToken( KEY, "g", 0 ) ) ).isPresent();
        assertThat( RefreshTokens.parse( RefreshTokens.newToken( KEY, "g", 10 ) ).orElseThrow().generation() ).isEqualTo( 10 );
        assertThat( RefreshTokens.parse( token ).orElseThrow().signingInput() ).isEqualTo( token.substring( 0, token.lastIndexOf( '.' ) ) );
    }

    @Test
    void tag_binds_the_token_to_the_grant_key_and_the_generation()
    {
        final String token = RefreshTokens.newToken( KEY, "g", 3 );
        final RefreshTokens.Parts parts = RefreshTokens.parse( token ).orElseThrow();
        assertThat( RefreshTokens.isIssuedUnder( parts, RefreshTokens.newGrantKey() ) ).as( "another grant's key" ).isFalse();
        assertThat( RefreshTokens.isIssuedUnder( RefreshTokens.parse( token.replace( ".3.", ".2." ) ).orElseThrow(), KEY ) )
            .as( "the generation is covered" )
            .isFalse();
        assertThat( RefreshTokens.isIssuedUnder( RefreshTokens.parse( token.replace( ".g.", ".h." ) ).orElseThrow(), KEY ) )
            .as( "the grant id is covered" )
            .isFalse();
        assertThat( RefreshTokens.isIssuedUnder( RefreshTokens.parse( "A".repeat( 43 ) + token.substring( 43 ) ).orElseThrow(), KEY ) )
            .as( "the secret is covered" )
            .isFalse();
    }

    @Test
    void hash_is_stable_and_not_the_token()
    {
        final String token = RefreshTokens.newToken( KEY, "g", 0 );
        assertThat( RefreshTokens.hash( token ) ).isEqualTo( RefreshTokens.hash( token ) ).matches( "[A-Za-z0-9_-]{43}" ).doesNotContain( token );
        assertThat( RefreshTokens.hash( token ) ).isNotEqualTo( RefreshTokens.hash( token + "x" ) );
    }

    @Test
    void successor_unwraps_only_under_the_retired_token_and_the_cluster_secret()
    {
        final String presented = RefreshTokens.newToken( KEY, "g", 0 );
        final String next = RefreshTokens.newToken( KEY, "g", 1 );
        final String wrapped = RefreshTokens.wrap( SECRET, presented, next );

        assertThat( wrapped ).doesNotContain( next.substring( 0, 43 ) ).doesNotContain( presented.substring( 0, 43 ) );
        assertThat( RefreshTokens.wrap( SECRET, presented, next ) ).as( "fresh iv each time" ).isNotEqualTo( wrapped );
        assertThat( RefreshTokens.unwrap( SECRET, presented, wrapped ) ).contains( next );
        assertThat( RefreshTokens.unwrap( SECRET, RefreshTokens.newToken( KEY, "g", 0 ), wrapped ) ).as( "another token" ).isEmpty();
        assertThat( RefreshTokens.unwrap( SECRET, RefreshTokens.hash( presented ), wrapped ) ).as( "the stored hash is not the token" ).isEmpty();
        assertThat( RefreshTokens.unwrap( new byte[64], presented, wrapped ) ).as( "same bytes, same secret" ).contains( next );
        assertThat( RefreshTokens.unwrap( RefreshTokens.newGrantKey(), presented, wrapped ) ).as( "the stored grant key is not the secret" )
            .isEmpty();
        final byte[] other = new byte[64];
        other[0] = 1;
        assertThat( RefreshTokens.unwrap( other, presented, wrapped ) ).as( "the token without the cluster secret" ).isEmpty();
        assertThat( RefreshTokens.unwrap( SECRET, presented, wrapped.substring( 1 ) ) ).isEmpty();
        assertThat( RefreshTokens.unwrap( SECRET, presented, "" ) ).isEmpty();
        assertThat( RefreshTokens.unwrap( SECRET, presented, "!!!" ) ).isEmpty();
    }
}
