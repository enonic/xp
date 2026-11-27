package com.enonic.xp.core.impl.security.token;

import java.util.Base64;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.assertj.core.api.Assertions.assertThat;

class JwsHs512Test
{
    private static final SigningKey KEY = new SigningKey( "k1", new byte[64] );

    private static final SigningKey OTHER = new SigningKey( "k2", otherSecret() );

    private static byte[] otherSecret()
    {
        final byte[] bytes = new byte[64];
        bytes[0] = 1;
        return bytes;
    }

    @Test
    void sign_parse_verify()
    {
        final ObjectNode payload = JwsHs512.newPayload().put( "iss", "xp" ).put( "sub", "user:system:alice" );
        final String token = JwsHs512.sign( KEY, payload );

        final JwsHs512.Parsed parsed = JwsHs512.parse( token ).orElseThrow();
        assertThat( parsed.header().get( "alg" ).asText() ).isEqualTo( "HS512" );
        assertThat( parsed.header().get( "typ" ).asText() ).isEqualTo( JwsHs512.TYP );
        assertThat( parsed.kid() ).contains( "k1" );
        assertThat( parsed.payload().get( "sub" ).asText() ).isEqualTo( "user:system:alice" );
        assertThat( JwsHs512.verify( parsed, KEY ) ).isTrue();
        assertThat( JwsHs512.verify( parsed, OTHER ) ).isFalse();
    }

    @Test
    void tampered_payload_fails()
    {
        final String token = JwsHs512.sign( KEY, JwsHs512.newPayload().put( "sub", "user:system:alice" ) );
        final String[] parts = token.split( "\\." );
        final String forgedPayload = Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString( "{\"sub\":\"user:system:admin\"}".getBytes() );
        final JwsHs512.Parsed forged = JwsHs512.parse( parts[0] + "." + forgedPayload + "." + parts[2] ).orElseThrow();

        assertThat( JwsHs512.verify( forged, KEY ) ).isFalse();
    }

    @Test
    void alg_none_is_rejected_even_with_empty_signature()
    {
        final Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        final String header = enc.encodeToString( "{\"alg\":\"none\",\"kid\":\"k1\"}".getBytes() );
        final String payload = enc.encodeToString( "{\"sub\":\"user:system:admin\"}".getBytes() );
        final JwsHs512.Parsed parsed = JwsHs512.parse( header + "." + payload + "." ).orElseThrow();

        assertThat( JwsHs512.verify( parsed, KEY ) ).isFalse();
    }

    @Test
    void token_type_is_required_and_pinned()
    {
        final Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        final String payload = enc.encodeToString( "{\"sub\":\"user:system:alice\"}".getBytes() );
        for ( String header : new String[]{"{\"alg\":\"HS512\",\"kid\":\"k1\"}", "{\"alg\":\"HS512\",\"typ\":\"JWT\",\"kid\":\"k1\"}",
            "{\"alg\":\"HS512\",\"typ\":\"jwt+at\",\"kid\":\"k1\"}", "{\"alg\":\"HS512\",\"typ\":\"text/at+jwt\",\"kid\":\"k1\"}",
            "{\"alg\":\"HS512\",\"typ\":\"at+jwt\",\"kid\":\"k1\",\"crit\":[\"exp\"]}", "{\"alg\":\"HS512\",\"typ\":\"at+jwt\",\"kid\":\"k1\",\"crit\":[]}"} )
        {
            final JwsHs512.Parsed parsed = JwsHs512.parse( enc.encodeToString( header.getBytes() ) + "." + payload + ".AAAA" ).orElseThrow();
            assertThat( JwsHs512.hasExpectedHeader( parsed ) ).as( header ).isFalse();
            assertThat( JwsHs512.verify( parsed, KEY ) ).as( header ).isFalse();
        }
        for ( String typ : new String[]{"at+jwt", "AT+JWT", "application/at+jwt", "Application/AT+jwt"} )
        {
            final String header = "{\"alg\":\"HS512\",\"typ\":\"" + typ + "\",\"kid\":\"k1\"}";
            assertThat( JwsHs512.hasExpectedHeader(
                JwsHs512.parse( enc.encodeToString( header.getBytes() ) + "." + payload + ".AAAA" ).orElseThrow() ) ).as( header ).isTrue();
        }
    }

    @Test
    void parse_rejects_garbage()
    {
        assertThat( JwsHs512.parse( "" ) ).isEmpty();
        assertThat( JwsHs512.parse( "abc" ) ).isEmpty();
        assertThat( JwsHs512.parse( "a.b" ) ).isEmpty();
        assertThat( JwsHs512.parse( "a.b.c.d" ) ).isEmpty();
        assertThat( JwsHs512.parse( "!!!.???.***" ) ).isEmpty();
        final String notObjects = Base64.getUrlEncoder().withoutPadding().encodeToString( "[1]".getBytes() );
        assertThat( JwsHs512.parse( notObjects + "." + notObjects + ".x" ) ).isEmpty();
    }

    @Test
    void kid_missing()
    {
        final Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        final String header = enc.encodeToString( "{\"alg\":\"HS512\"}".getBytes() );
        final String payload = enc.encodeToString( "{}".getBytes() );
        final Optional<JwsHs512.Parsed> parsed = JwsHs512.parse( header + "." + payload + ".sig" );
        assertThat( parsed.orElseThrow().kid() ).isEmpty();
    }

    @Test
    void a_token_of_another_type_never_passes_as_an_access_token()
    {
        final String typed = JwsHs512.sign( KEY, "client-id+jwt", JwsHs512.newPayload().put( "client_name", "x" ) );
        final JwsHs512.Parsed parsed = JwsHs512.parse( typed ).orElseThrow();

        assertThat( parsed.header().get( "typ" ).asText() ).isEqualTo( "client-id+jwt" );
        assertThat( JwsHs512.verify( parsed, KEY, "client-id+jwt" ) ).isTrue();
        assertThat( JwsHs512.verify( parsed, KEY ) ).as( "not an access token" ).isFalse();
        assertThat( JwsHs512.hasExpectedHeader( parsed ) ).isFalse();
        assertThat( JwsHs512.verify( JwsHs512.parse( JwsHs512.sign( KEY, JwsHs512.newPayload() ) ).orElseThrow(), KEY, "client-id+jwt" ) )
            .as( "nor the other way" ).isFalse();
    }
}
