package com.enonic.xp.web.jetty.impl.websocket;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SameOriginCheckTest
{
    @Test
    void absent_origin_accepted()
    {
        assertThat( SameOriginCheck.check( null, "https", "example.com", 443 ) ).isTrue();
    }

    @Test
    void literal_null_origin_rejected()
    {
        assertThat( SameOriginCheck.check( "null", "https", "example.com", 443 ) ).isFalse();
    }

    @Test
    void same_origin_https_default_port_omitted_in_header()
    {
        assertThat( SameOriginCheck.check( "https://example.com", "https", "example.com", 443 ) ).isTrue();
    }

    @Test
    void same_origin_http_default_port_omitted_in_header()
    {
        assertThat( SameOriginCheck.check( "http://example.com", "http", "example.com", 80 ) ).isTrue();
    }

    @Test
    void same_origin_explicit_default_port_in_header_still_matches()
    {
        assertThat( SameOriginCheck.check( "https://example.com:443", "https", "example.com", 443 ) ).isTrue();
        assertThat( SameOriginCheck.check( "http://example.com:80", "http", "example.com", 80 ) ).isTrue();
    }

    @Test
    void same_origin_non_default_port_matches()
    {
        assertThat( SameOriginCheck.check( "https://example.com:8443", "https", "example.com", 8443 ) ).isTrue();
    }

    @Test
    void host_comparison_is_case_insensitive()
    {
        assertThat( SameOriginCheck.check( "https://Example.COM", "https", "example.com", 443 ) ).isTrue();
        assertThat( SameOriginCheck.check( "https://example.com", "HTTPS", "example.com", 443 ) ).isTrue();
    }

    @Test
    void different_host_rejected()
    {
        assertThat( SameOriginCheck.check( "https://evil.example.org", "https", "example.com", 443 ) ).isFalse();
    }

    @Test
    void subdomain_is_different_origin()
    {
        assertThat( SameOriginCheck.check( "https://api.example.com", "https", "example.com", 443 ) ).isFalse();
        assertThat( SameOriginCheck.check( "https://example.com", "https", "api.example.com", 443 ) ).isFalse();
    }

    @Test
    void different_scheme_rejected()
    {
        assertThat( SameOriginCheck.check( "http://example.com", "https", "example.com", 443 ) ).isFalse();
        assertThat( SameOriginCheck.check( "https://example.com", "http", "example.com", 80 ) ).isFalse();
    }

    @Test
    void different_port_rejected()
    {
        assertThat( SameOriginCheck.check( "https://example.com:8443", "https", "example.com", 9443 ) ).isFalse();
        assertThat( SameOriginCheck.check( "https://example.com", "https", "example.com", 8443 ) ).isFalse();
        assertThat( SameOriginCheck.check( "https://example.com:8443", "https", "example.com", 443 ) ).isFalse();
    }

    @Test
    void malformed_origin_rejected()
    {
        assertThat( SameOriginCheck.check( "not a uri", "https", "example.com", 443 ) ).isFalse();
        assertThat( SameOriginCheck.check( "https://", "https", "example.com", 443 ) ).isFalse();
        assertThat( SameOriginCheck.check( "://example.com", "https", "example.com", 443 ) ).isFalse();
    }

    @Test
    void origin_with_path_ignored_for_comparison()
    {
        // Browsers should not send a path in Origin, but be lenient: only compare scheme/host/port.
        assertThat( SameOriginCheck.check( "https://example.com/", "https", "example.com", 443 ) ).isTrue();
    }
}
