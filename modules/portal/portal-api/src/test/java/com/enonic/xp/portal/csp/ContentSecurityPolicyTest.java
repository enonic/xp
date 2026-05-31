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
    void override_after_add_replaces_directive()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().add( "script-src", "'self'", "https://cdn.example.com" )
            .override( "script-src", "'none'" );
        assertThat( csp.build() ).isEqualTo( "script-src 'none'" );
    }

    @Test
    void add_after_override_extends_without_freeze()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().override( "script-src", "'self'" ).add( "script-src", "https://cdn.example.com" );
        assertThat( csp.build() ).isEqualTo( "script-src 'self' https://cdn.example.com" );
    }

    @Test
    void reset_removes_all_directives()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF ).imgSrc( CspSource.SELF );
        csp.reset();
        assertThat( csp.build() ).isEmpty();
    }

    @Test
    void reset_removes_named_directives()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().scriptSrc( CspSource.SELF ).upgradeInsecureRequests().reset( "upgrade-insecure-requests" );
        assertThat( csp.build() ).isEqualTo( "script-src 'self'" );
    }

    @Test
    void reset_preserves_nonce_so_it_stays_stable()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String nonce = csp.scriptSrcNonce();
        csp.reset();
        assertThat( csp.scriptSrcNonce() ).isEqualTo( nonce );
    }

    @Test
    void nonce_lazy_and_cached()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String first = csp.scriptSrcNonce();
        final String second = csp.scriptSrcNonce();
        assertThat( first ).isEqualTo( second );

        final byte[] decoded = Base64.getUrlDecoder().decode( first );
        assertThat( decoded.length ).isGreaterThanOrEqualTo( 16 );

        assertThat( csp.build() ).isEqualTo( "script-src 'nonce-" + first + "'" );
    }

    @Test
    void scriptSrcNonce_targets_script_src_only()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String nonce = csp.scriptSrcNonce();
        assertThat( csp.build() ).isEqualTo( "script-src 'nonce-" + nonce + "'" );
    }

    @Test
    void styleSrcNonce_targets_style_src_only()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String nonce = csp.styleSrcNonce();
        assertThat( csp.build() ).isEqualTo( "style-src 'nonce-" + nonce + "'" );
    }

    @Test
    void nonce_value_stable_across_methods()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String fromScript = csp.scriptSrcNonce();
        assertThat( csp.styleSrcNonce() ).isEqualTo( fromScript );
        assertThat( csp.build() ).isEqualTo(
            "script-src 'nonce-" + fromScript + "'; style-src 'nonce-" + fromScript + "'" );
    }

    @Test
    void nonce_uniqueness_across_instances()
    {
        final String first = new ContentSecurityPolicy().scriptSrcNonce();
        final String second = new ContentSecurityPolicy().scriptSrcNonce();
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
    void unsafe_inline_drops_nonce_in_output()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().add( "script-src", "'unsafe-inline'" );
        csp.scriptSrcNonce();
        assertThat( csp.build() ).isEqualTo( "script-src 'unsafe-inline'" );
    }

    @Test
    void unsafe_inline_drops_hash_in_output()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().add( "style-src", "'unsafe-inline'" ).styleSrcSha( HashAlgo.SHA256, "AbC" );
        assertThat( csp.build() ).isEqualTo( "style-src 'unsafe-inline'" );
    }

    @Test
    void unsafe_inline_drops_nonce_regardless_of_order()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        csp.scriptSrcNonce();
        csp.add( "script-src", "'unsafe-inline'" );
        assertThat( csp.build() ).isEqualTo( "script-src 'unsafe-inline'" );
    }

    @Test
    void unsafe_inline_keeps_host_and_self_sources()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().scriptSrc( CspSource.SELF, CspSource.UNSAFE_INLINE ).scriptSrc( "https://cdn.example.com" );
        csp.scriptSrcNonce();
        assertThat( csp.build() ).isEqualTo( "script-src 'self' 'unsafe-inline' https://cdn.example.com" );
    }

    @Test
    void nonce_kept_without_unsafe_inline()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        final String nonce = csp.scriptSrcNonce();
        assertThat( csp.build() ).isEqualTo( "script-src 'self' 'nonce-" + nonce + "'" );
    }

    @Test
    void unsafe_inline_drops_strict_dynamic_and_nonce()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.UNSAFE_INLINE, CspSource.STRICT_DYNAMIC );
        csp.scriptSrcNonce();
        assertThat( csp.build() ).isEqualTo( "script-src 'unsafe-inline'" );
    }

    @Test
    void strict_recipe_collapses_to_legacy_fallback()
    {
        // 'nonce' 'strict-dynamic' https: 'self' 'unsafe-inline' resolves to the permissive fallback
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().scriptSrc( CspSource.SELF, CspSource.UNSAFE_INLINE, CspSource.STRICT_DYNAMIC )
                .scriptSrc( "https:" );
        csp.scriptSrcNonce();
        assertThat( csp.build() ).isEqualTo( "script-src 'self' 'unsafe-inline' https:" );
    }

    @Test
    void strict_dynamic_kept_without_unsafe_inline()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.STRICT_DYNAMIC );
        final String nonce = csp.scriptSrcNonce();
        assertThat( csp.build() ).isEqualTo( "script-src 'strict-dynamic' 'nonce-" + nonce + "'" );
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
    void override_with_no_sources_creates_empty_directive()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().override( "upgrade-insecure-requests" );
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
    void override_null_directive_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().override( null, "'self'" ) ).isInstanceOf( NullPointerException.class );
    }

    @Test
    void scriptSrcSha_null_content_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().scriptSrcSha( (byte[]) null ) ).isInstanceOf(
            NullPointerException.class );
    }

    @Test
    void scriptSrcSha_null_algo_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().scriptSrcSha( null, "AAAA" ) ).isInstanceOf(
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
    void scheme_sources_emitted_unquoted()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().imgSrc( CspSource.SELF, CspSource.DATA, CspSource.BLOB );
        assertThat( csp.build() ).isEqualTo( "img-src 'self' data: blob:" );
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
    void scriptSrcSha_bytes_encodes_sha256_into_script_src()
        throws Exception
    {
        final byte[] content = "alert('hi');".getBytes();
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrcSha( content );

        final String expectedDigest = Base64.getEncoder().encodeToString( MessageDigest.getInstance( "SHA-256" ).digest( content ) );
        assertThat( csp.build() ).isEqualTo( "script-src 'sha256-" + expectedDigest + "'" );
    }

    @Test
    void scriptSrcSha_bytes_encodes_chosen_algo_into_script_src()
        throws Exception
    {
        final byte[] content = "alert('hi');".getBytes();
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrcSha( HashAlgo.SHA384, content );

        final String expectedDigest = Base64.getEncoder().encodeToString( MessageDigest.getInstance( "SHA-384" ).digest( content ) );
        assertThat( csp.build() ).isEqualTo( "script-src 'sha384-" + expectedDigest + "'" );
    }

    @Test
    void scriptSrcSha_precomputed_emits_algo_and_value()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrcSha( HashAlgo.SHA384, "AbC" );
        assertThat( csp.build() ).isEqualTo( "script-src 'sha384-AbC'" );
    }

    @Test
    void styleSrcSha_bytes_encodes_sha256_into_style_src()
        throws Exception
    {
        final byte[] content = "body { color: red; }".getBytes();
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().styleSrcSha( content );

        final String expectedDigest = Base64.getEncoder().encodeToString( MessageDigest.getInstance( "SHA-256" ).digest( content ) );
        assertThat( csp.build() ).isEqualTo( "style-src 'sha256-" + expectedDigest + "'" );
    }

    @Test
    void styleSrcSha_precomputed_emits_algo_and_value()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().styleSrcSha( HashAlgo.SHA512, "XyZ" );
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
    void granular_script_and_style_directives()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrcElem( CspSource.SELF )
            .scriptSrcAttr( CspSource.NONE )
            .styleSrcElem( CspSource.SELF )
            .styleSrcAttr( CspSource.NONE );
        assertThat( csp.build() ).isEqualTo(
            "script-src-attr 'none'; script-src-elem 'self'; style-src-attr 'none'; style-src-elem 'self'" );
    }

    @Test
    void reportTo_adds_directive()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().reportTo( "csp-endpoint" );
        assertThat( csp.build() ).isEqualTo( "report-to csp-endpoint" );
    }

    @Test
    void requireTrustedTypesForScript_adds_script()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().requireTrustedTypesForScript();
        assertThat( csp.build() ).isEqualTo( "require-trusted-types-for 'script'" );
    }

    @Test
    void trustedTypes_policy_names_and_keywords()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().trustedTypes( "my-policy" ).trustedTypes( TrustedTypesKeyword.ALLOW_DUPLICATES );
        assertThat( csp.build() ).isEqualTo( "trusted-types my-policy 'allow-duplicates'" );
    }

    @Test
    void trustedTypes_keyword_tokens()
    {
        assertThat( new ContentSecurityPolicy().trustedTypes( TrustedTypesKeyword.NONE ).build() ).isEqualTo( "trusted-types 'none'" );
        assertThat( new ContentSecurityPolicy().trustedTypes( TrustedTypesKeyword.WILDCARD ).build() ).isEqualTo( "trusted-types *" );
    }

    @Test
    void reportOnly_defaults_false_and_toggles()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        assertThat( csp.isReportOnly() ).isFalse();
        csp.reportOnly( true );
        assertThat( csp.isReportOnly() ).isTrue();
        csp.reportOnly( false );
        assertThat( csp.isReportOnly() ).isFalse();
    }
}
