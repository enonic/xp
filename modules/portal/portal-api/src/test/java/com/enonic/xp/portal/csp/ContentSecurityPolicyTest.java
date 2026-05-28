package com.enonic.xp.portal.csp;

import java.security.MessageDigest;
import java.util.Base64;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContentSecurityPolicyTest
{
    @Test
    void empty_policy_builds_empty_string()
    {
        assertThat( new ContentSecurityPolicy().build() ).isEmpty();
    }

    @Test
    void add_single_source()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().add( "script-src", "'self'" );
        assertThat( csp.build() ).isEqualTo( "script-src 'self'" );
    }

    @Test
    void add_then_add_unions_and_dedupes()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().add( "script-src", "'self'" )
            .add( "script-src", "'self'", "https://cdn.example.com" );
        assertThat( csp.build() ).isEqualTo( "script-src 'self' https://cdn.example.com" );
    }

    @Test
    void set_after_add_resets_directive()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().add( "script-src", "'self'", "https://cdn.example.com" )
            .set( "script-src", "'none'" );
        assertThat( csp.build() ).isEqualTo( "script-src 'none'" );
    }

    @Test
    void add_after_set_extends_without_freeze()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().set( "script-src", "'self'" ).add( "script-src", "https://cdn.example.com" );
        assertThat( csp.build() ).isEqualTo( "script-src 'self' https://cdn.example.com" );
    }

    @Test
    void addSha_bytes_encodes_sha256_of_content()
        throws Exception
    {
        final byte[] content = "alert('hi');".getBytes();
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().addSha( "script-src", content );

        final String expectedDigest = Base64.getEncoder().encodeToString( MessageDigest.getInstance( "SHA-256" ).digest( content ) );
        assertThat( csp.build() ).isEqualTo( "script-src 'sha256-" + expectedDigest + "'" );
    }

    @Test
    void addSha_precomputed_emits_token_dash_value()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().addSha( "style-src", HashAlgo.SHA384, "abc123def456" );
        assertThat( csp.build() ).isEqualTo( "style-src 'sha384-abc123def456'" );
    }

    @Test
    void addSha_precomputed_supports_all_algorithms()
    {
        assertThat( new ContentSecurityPolicy().addSha( "script-src", HashAlgo.SHA256, "AAAA" ).build() ).isEqualTo(
            "script-src 'sha256-AAAA'" );
        assertThat( new ContentSecurityPolicy().addSha( "script-src", HashAlgo.SHA384, "AAAA" ).build() ).isEqualTo(
            "script-src 'sha384-AAAA'" );
        assertThat( new ContentSecurityPolicy().addSha( "script-src", HashAlgo.SHA512, "AAAA" ).build() ).isEqualTo(
            "script-src 'sha512-AAAA'" );
    }

    @Test
    void nonce_lazy_and_cached()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String first = csp.nonce();
        final String second = csp.nonce();
        assertThat( first ).isEqualTo( second );

        final byte[] decoded = Base64.getUrlDecoder().decode( first );
        assertThat( decoded.length ).isGreaterThanOrEqualTo( 16 );

        assertThat( csp.build() ).isEqualTo( "script-src 'nonce-" + first + "'" );
    }

    @Test
    void nonce_uniqueness_across_instances()
    {
        final String first = new ContentSecurityPolicy().nonce();
        final String second = new ContentSecurityPolicy().nonce();
        assertThat( first ).isNotEqualTo( second );
    }

    @Test
    void nonce_never_called_emits_no_nonce_entry()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().add( "script-src", "'self'" ).add( "style-src", "'self'" );
        assertThat( csp.build() ).doesNotContain( "nonce-" );
    }

    @Test
    void applyNonceTo_before_nonce_call_extends_default_directives()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().applyNonceTo( "style-src" );
        final String nonce = csp.nonce();
        assertThat( csp.build() ).isEqualTo( "script-src 'nonce-" + nonce + "'; style-src 'nonce-" + nonce + "'" );
    }

    @Test
    void applyNonceTo_after_nonce_call_immediately_adds_to_new_directive()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String nonce = csp.nonce();
        csp.applyNonceTo( "style-src" );
        assertThat( csp.build() ).isEqualTo( "script-src 'nonce-" + nonce + "'; style-src 'nonce-" + nonce + "'" );
    }

    @Test
    void boolean_directive_serializes_without_sources()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().add( "upgrade-insecure-requests" );
        assertThat( csp.build() ).isEqualTo( "upgrade-insecure-requests" );
    }

    @Test
    void frame_ancestors_unions_sources()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().add( "frame-ancestors", "'self'" ).add( "frame-ancestors", "https://example.com" );
        assertThat( csp.build() ).isEqualTo( "frame-ancestors 'self' https://example.com" );
    }

    @Test
    void directives_are_emitted_in_alphabetical_order()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().add( "script-src", "'self'" ).add( "img-src", "data:" ).add( "default-src", "'none'" );
        assertThat( csp.build() ).isEqualTo( "default-src 'none'; img-src data:; script-src 'self'" );
    }

    @Test
    void unsafe_inline_and_nonce_coexist_in_output()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().add( "script-src", "'unsafe-inline'" );
        final String nonce = csp.nonce();
        assertThat( csp.build() ).isEqualTo( "script-src 'unsafe-inline' 'nonce-" + nonce + "'" );
    }

    @Test
    void late_mutation_lands_in_subsequent_build()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().add( "script-src", "'self'" );
        assertThat( csp.build() ).isEqualTo( "script-src 'self'" );
        csp.add( "img-src", "data:" );
        assertThat( csp.build() ).isEqualTo( "img-src data:; script-src 'self'" );
    }

    @Test
    void set_with_no_sources_creates_empty_directive()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().set( "upgrade-insecure-requests" );
        assertThat( csp.build() ).isEqualTo( "upgrade-insecure-requests" );
    }

    @Test
    void add_null_directive_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().add( null, "'self'" ) ).isInstanceOf( NullPointerException.class );
    }

    @Test
    void add_null_source_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().add( "script-src", (String) null ) ).isInstanceOf(
            NullPointerException.class );
    }

    @Test
    void set_null_directive_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().set( null, "'self'" ) ).isInstanceOf( NullPointerException.class );
    }

    @Test
    void addSha_null_content_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().addSha( "script-src", (byte[]) null ) ).isInstanceOf(
            NullPointerException.class );
    }

    @Test
    void addSha_null_algo_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().addSha( "script-src", null, "AAAA" ) ).isInstanceOf(
            NullPointerException.class );
    }

    @Test
    void applyNonceTo_null_directive_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().applyNonceTo( (String) null ) ).isInstanceOf( NullPointerException.class );
    }
}
