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

        assertThat( csp.build() ).isEqualTo( "script-src 'nonce-" + first + "'; style-src 'nonce-" + first + "'" );
    }

    @Test
    void nonceScriptSrc_targets_script_src_only()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String nonce = csp.nonceScriptSrc();
        assertThat( csp.build() ).isEqualTo( "script-src 'nonce-" + nonce + "'" );
    }

    @Test
    void nonceStyleSrc_targets_style_src_only()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String nonce = csp.nonceStyleSrc();
        assertThat( csp.build() ).isEqualTo( "style-src 'nonce-" + nonce + "'" );
    }

    @Test
    void nonce_value_stable_across_methods()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String fromScript = csp.nonceScriptSrc();
        assertThat( csp.nonceStyleSrc() ).isEqualTo( fromScript );
        assertThat( csp.nonce() ).isEqualTo( fromScript );
        assertThat( csp.build() ).isEqualTo(
            "script-src 'nonce-" + fromScript + "'; style-src 'nonce-" + fromScript + "'" );
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
        final String nonce = csp.nonceScriptSrc();
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
    void scriptSrc_single_typed_source()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        assertThat( csp.build() ).isEqualTo( "script-src 'self'" );
    }

    @Test
    void scriptSrc_multiple_typed_sources()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF, CspSource.UNSAFE_INLINE );
        assertThat( csp.build() ).isEqualTo( "script-src 'self' 'unsafe-inline'" );
    }

    @Test
    void scriptSrc_raw_host_emitted_unquoted()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( "https://cdn.example.com" );
        assertThat( csp.build() ).isEqualTo( "script-src https://cdn.example.com" );
    }

    @Test
    void scriptSrc_typed_and_raw_are_unioned()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().scriptSrc( CspSource.SELF ).scriptSrc( "https://cdn.example.com" );
        assertThat( csp.build() ).isEqualTo( "script-src 'self' https://cdn.example.com" );
    }

    @Test
    void styleSrc_typed_source()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().styleSrc( CspSource.SELF );
        assertThat( csp.build() ).isEqualTo( "style-src 'self'" );
    }

    @Test
    void imgSrc_mixed_typed_and_raw()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().imgSrc( CspSource.SELF ).imgSrc( "data:" );
        assertThat( csp.build() ).isEqualTo( "img-src 'self' data:" );
    }

    @Test
    void defaultSrc_none()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().defaultSrc( CspSource.NONE );
        assertThat( csp.build() ).isEqualTo( "default-src 'none'" );
    }

    @Test
    void frameAncestors_none()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().frameAncestors( CspSource.NONE );
        assertThat( csp.build() ).isEqualTo( "frame-ancestors 'none'" );
    }

    @Test
    void baseUri_self()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().baseUri( CspSource.SELF );
        assertThat( csp.build() ).isEqualTo( "base-uri 'self'" );
    }

    @Test
    void formAction_self()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().formAction( CspSource.SELF );
        assertThat( csp.build() ).isEqualTo( "form-action 'self'" );
    }

    @Test
    void upgradeInsecureRequests_serializes_without_sources()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().upgradeInsecureRequests();
        assertThat( csp.build() ).isEqualTo( "upgrade-insecure-requests" );
    }

    @Test
    void sandbox_unions_flags_unquoted()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().sandbox( SandboxFlag.ALLOW_SCRIPTS, SandboxFlag.ALLOW_SAME_ORIGIN );
        assertThat( csp.build() ).isEqualTo( "sandbox allow-scripts allow-same-origin" );
    }

    @Test
    void sandbox_no_flags_emits_directive_only()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().sandbox();
        assertThat( csp.build() ).isEqualTo( "sandbox" );
    }

    @Test
    void addScriptSrcSha_bytes_encodes_sha256_into_script_src()
        throws Exception
    {
        final byte[] content = "alert('hi');".getBytes();
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().addScriptSrcSha( content );

        final String expectedDigest = Base64.getEncoder().encodeToString( MessageDigest.getInstance( "SHA-256" ).digest( content ) );
        assertThat( csp.build() ).isEqualTo( "script-src 'sha256-" + expectedDigest + "'" );
    }

    @Test
    void addScriptSrcSha_precomputed_emits_algo_and_value()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().addScriptSrcSha( HashAlgo.SHA384, "AbC" );
        assertThat( csp.build() ).isEqualTo( "script-src 'sha384-AbC'" );
    }

    @Test
    void addStyleSrcSha_bytes_encodes_sha256_into_style_src()
        throws Exception
    {
        final byte[] content = "body { color: red; }".getBytes();
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().addStyleSrcSha( content );

        final String expectedDigest = Base64.getEncoder().encodeToString( MessageDigest.getInstance( "SHA-256" ).digest( content ) );
        assertThat( csp.build() ).isEqualTo( "style-src 'sha256-" + expectedDigest + "'" );
    }

    @Test
    void addStyleSrcSha_precomputed_emits_algo_and_value()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().addStyleSrcSha( HashAlgo.SHA512, "XyZ" );
        assertThat( csp.build() ).isEqualTo( "style-src 'sha512-XyZ'" );
    }

    @Test
    void typed_directive_methods_are_chainable()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().defaultSrc( CspSource.NONE )
            .scriptSrc( CspSource.SELF )
            .styleSrc( CspSource.SELF )
            .upgradeInsecureRequests();
        assertThat( csp.build() ).isEqualTo(
            "default-src 'none'; script-src 'self'; style-src 'self'; upgrade-insecure-requests" );
    }

    @Test
    void typed_scriptSrc_null_source_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().scriptSrc( (CspSource) null ) ).isInstanceOf(
            NullPointerException.class );
    }

    @Test
    void sandbox_null_flag_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().sandbox( (SandboxFlag) null ) ).isInstanceOf( NullPointerException.class );
    }

    @Test
    void strict_seeds_deny_all_baseline()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().strict();
        assertThat( csp.build() ).isEqualTo( "base-uri 'none'; default-src 'none'; frame-ancestors 'none'" );
    }

    @Test
    void strict_then_open_specific_directives()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().strict().scriptSrc( CspSource.SELF ).styleSrc( CspSource.SELF );
        assertThat( csp.build() ).isEqualTo(
            "base-uri 'none'; default-src 'none'; frame-ancestors 'none'; script-src 'self'; style-src 'self'" );
    }

    @Test
    void strictDynamic_seeds_nonce_based_baseline()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().strictDynamic();
        final String nonce = csp.nonceScriptSrc();
        assertThat( csp.build() ).isEqualTo(
            "base-uri 'none'; object-src 'none'; script-src 'nonce-" + nonce + "' 'strict-dynamic' https: 'unsafe-inline'" );
    }

    @Test
    void strictDynamic_nonce_is_retrievable_and_stable()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().strictDynamic();
        assertThat( csp.nonceScriptSrc() ).isEqualTo( csp.nonceScriptSrc() );
        assertThat( csp.build() ).contains( "'nonce-" + csp.nonceScriptSrc() + "'" );
    }
}
