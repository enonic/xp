package com.enonic.xp.web.csp;

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
    void resetAll_removes_all_directives()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF ).imgSrc( CspSource.SELF );
        csp.resetAll();
        assertThat( csp.build() ).isEmpty();
    }

    @Test
    void reset_with_no_args_removes_nothing()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        csp.reset();
        assertThat( csp.build() ).isEqualTo( "script-src 'self'" );
    }

    @Test
    void reset_removes_named_directives()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().scriptSrc( CspSource.SELF ).add( "upgrade-insecure-requests" ).reset( "upgrade-insecure-requests" );
        assertThat( csp.build() ).isEqualTo( "script-src 'self'" );
    }

    @Test
    void resetAll_preserves_nonce_so_it_stays_stable()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String nonce = csp.nonceScriptSrc();
        csp.resetAll();
        assertThat( csp.nonceScriptSrc() ).isEqualTo( nonce );
    }

    @Test
    void add_invalid_directive_name_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().add( "script src" ) ).isInstanceOf( IllegalArgumentException.class );
        assertThatThrownBy( () -> new ContentSecurityPolicy().add( "" ) ).isInstanceOf( IllegalArgumentException.class );
        assertThatThrownBy( () -> new ContentSecurityPolicy().add( "script-src; img-src" ) ).isInstanceOf(
            IllegalArgumentException.class );
    }

    @Test
    void add_source_with_policy_injection_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().add( "script-src", "x; script-src *" ) ).isInstanceOf(
            IllegalArgumentException.class );
        assertThatThrownBy( () -> new ContentSecurityPolicy().add( "script-src", "'self' 'unsafe-inline'" ) ).isInstanceOf(
            IllegalArgumentException.class );
        assertThatThrownBy( () -> new ContentSecurityPolicy().add( "script-src", "a,b" ) ).isInstanceOf(
            IllegalArgumentException.class );
        assertThatThrownBy( () -> new ContentSecurityPolicy().add( "script-src", "" ) ).isInstanceOf( IllegalArgumentException.class );
        assertThatThrownBy( () -> new ContentSecurityPolicy().add( "script-src", "a\nb" ) ).isInstanceOf(
            IllegalArgumentException.class );
    }

    @Test
    void override_source_with_policy_injection_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().override( "script-src", "x; script-src *" ) ).isInstanceOf(
            IllegalArgumentException.class );
    }

    @Test
    void nonce_lazy_and_cached()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String first = csp.nonceScriptSrc();
        final String second = csp.nonceScriptSrc();
        assertThat( first ).isEqualTo( second );

        final byte[] decoded = Base64.getUrlDecoder().decode( first );
        assertThat( decoded.length ).isGreaterThanOrEqualTo( 16 );

        assertThat( csp.build() ).isEqualTo( "script-src 'nonce-" + first + "'" );
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
        assertThat( csp.build() ).isEqualTo(
            "script-src 'nonce-" + fromScript + "'; style-src 'nonce-" + fromScript + "'" );
    }

    @Test
    void nonce_uniqueness_across_instances()
    {
        final String first = new ContentSecurityPolicy().nonceScriptSrc();
        final String second = new ContentSecurityPolicy().nonceScriptSrc();
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
    void unsafe_inline_and_nonce_both_emitted()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().add( "script-src", "'unsafe-inline'" );
        final String nonce = csp.nonceScriptSrc();
        assertThat( csp.build() ).isEqualTo( "script-src 'unsafe-inline' 'nonce-" + nonce + "'" );
    }

    @Test
    void unsafe_inline_and_hash_both_emitted()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().add( "style-src", "'unsafe-inline'" ).shaStyleSrc( HashAlgo.SHA256, "AbC" );
        assertThat( csp.build() ).isEqualTo( "style-src 'unsafe-inline' 'sha256-AbC'" );
    }

    @Test
    void sources_emitted_in_insertion_order()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String nonce = csp.nonceScriptSrc();
        csp.add( "script-src", "'unsafe-inline'" );
        assertThat( csp.build() ).isEqualTo( "script-src 'nonce-" + nonce + "' 'unsafe-inline'" );
    }

    @Test
    void union_keeps_every_source_unresolved()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().scriptSrc( CspSource.SELF, CspSource.UNSAFE_INLINE, CspSource.STRICT_DYNAMIC )
                .scriptSrc( "https:" );
        final String nonce = csp.nonceScriptSrc();
        assertThat( csp.build() ).isEqualTo( "script-src 'self' 'unsafe-inline' 'strict-dynamic' https: 'nonce-" + nonce + "'" );
    }

    @Test
    void nonce_kept_without_unsafe_inline()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        final String nonce = csp.nonceScriptSrc();
        assertThat( csp.build() ).isEqualTo( "script-src 'self' 'nonce-" + nonce + "'" );
    }

    @Test
    void strict_dynamic_kept_without_unsafe_inline()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.STRICT_DYNAMIC );
        final String nonce = csp.nonceScriptSrc();
        assertThat( csp.build() ).isEqualTo( "script-src 'strict-dynamic' 'nonce-" + nonce + "'" );
    }

    @Test
    void override_relaxes_by_replacing_a_strict_directive()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        csp.styleSrc( CspSource.SELF );
        csp.nonceStyleSrc();
        // an editor that needs inline styles relaxes style-src: override replaces it, dropping the
        // nonce that would otherwise make the browser ignore 'unsafe-inline'
        csp.override( "style-src", "'self'", "'unsafe-inline'" );
        assertThat( csp.build() ).isEqualTo( "script-src 'self'; style-src 'self' 'unsafe-inline'" );
    }

    @Test
    void redundant_none_dropped_when_real_source_present()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().add( "script-src", "'none'" ).add( "script-src", "'self'" );
        assertThat( csp.build() ).isEqualTo( "script-src 'self'" );
    }

    @Test
    void none_kept_when_sole_source()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().add( "script-src", "'none'" );
        assertThat( csp.build() ).isEqualTo( "script-src 'none'" );
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
    void shaScriptSrc_null_content_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().shaScriptSrc( (byte[]) null ) ).isInstanceOf(
            NullPointerException.class );
    }

    @Test
    void shaScriptSrc_null_algo_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().shaScriptSrc( null, "AAAA" ) ).isInstanceOf(
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
    void boolean_directive_via_add_serializes_without_sources()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().add( "upgrade-insecure-requests" );
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
    void shaScriptSrc_bytes_encodes_sha256_into_script_src()
        throws Exception
    {
        final byte[] content = "alert('hi');".getBytes();
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().shaScriptSrc( content );

        final String expectedDigest = Base64.getEncoder().encodeToString( MessageDigest.getInstance( "SHA-256" ).digest( content ) );
        assertThat( csp.build() ).isEqualTo( "script-src 'sha256-" + expectedDigest + "'" );
    }

    @Test
    void shaScriptSrc_bytes_encodes_chosen_algo_into_script_src()
        throws Exception
    {
        final byte[] content = "alert('hi');".getBytes();
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().shaScriptSrc( HashAlgo.SHA384, content );

        final String expectedDigest = Base64.getEncoder().encodeToString( MessageDigest.getInstance( "SHA-384" ).digest( content ) );
        assertThat( csp.build() ).isEqualTo( "script-src 'sha384-" + expectedDigest + "'" );
    }

    @Test
    void shaScriptSrc_precomputed_emits_algo_and_value()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().shaScriptSrc( HashAlgo.SHA384, "AbC" );
        assertThat( csp.build() ).isEqualTo( "script-src 'sha384-AbC'" );
    }

    @Test
    void shaStyleSrc_bytes_encodes_sha256_into_style_src()
        throws Exception
    {
        final byte[] content = "body { color: red; }".getBytes();
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().shaStyleSrc( content );

        final String expectedDigest = Base64.getEncoder().encodeToString( MessageDigest.getInstance( "SHA-256" ).digest( content ) );
        assertThat( csp.build() ).isEqualTo( "style-src 'sha256-" + expectedDigest + "'" );
    }

    @Test
    void shaStyleSrc_precomputed_emits_algo_and_value()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().shaStyleSrc( HashAlgo.SHA512, "XyZ" );
        assertThat( csp.build() ).isEqualTo( "style-src 'sha512-XyZ'" );
    }

    @Test
    void typed_directive_methods_are_chainable()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().defaultSrc( CspSource.NONE )
            .scriptSrc( CspSource.SELF )
            .styleSrc( CspSource.SELF )
            .add( "upgrade-insecure-requests" );
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
    void every_fetch_directive_method_targets_its_directive()
    {
        assertThat( new ContentSecurityPolicy().fontSrc( CspSource.SELF ).build() ).isEqualTo( "font-src 'self'" );
        assertThat( new ContentSecurityPolicy().connectSrc( CspSource.SELF ).build() ).isEqualTo( "connect-src 'self'" );
        assertThat( new ContentSecurityPolicy().mediaSrc( CspSource.SELF ).build() ).isEqualTo( "media-src 'self'" );
        assertThat( new ContentSecurityPolicy().objectSrc( CspSource.NONE ).build() ).isEqualTo( "object-src 'none'" );
        assertThat( new ContentSecurityPolicy().frameSrc( CspSource.SELF ).build() ).isEqualTo( "frame-src 'self'" );
        assertThat( new ContentSecurityPolicy().workerSrc( CspSource.BLOB ).build() ).isEqualTo( "worker-src blob:" );
        assertThat( new ContentSecurityPolicy().manifestSrc( CspSource.SELF ).build() ).isEqualTo( "manifest-src 'self'" );
        assertThat( new ContentSecurityPolicy().childSrc( CspSource.SELF ).build() ).isEqualTo( "child-src 'self'" );
    }

    @Test
    void every_directive_method_accepts_raw_string_sources()
    {
        assertThat( new ContentSecurityPolicy().defaultSrc( "'self'" ).build() ).isEqualTo( "default-src 'self'" );
        assertThat( new ContentSecurityPolicy().styleSrc( "https://cdn.example.com" ).build() ).isEqualTo(
            "style-src https://cdn.example.com" );
        assertThat( new ContentSecurityPolicy().fontSrc( "data:" ).build() ).isEqualTo( "font-src data:" );
        assertThat( new ContentSecurityPolicy().connectSrc( "wss://example.com" ).build() ).isEqualTo( "connect-src wss://example.com" );
        assertThat( new ContentSecurityPolicy().mediaSrc( "'self'" ).build() ).isEqualTo( "media-src 'self'" );
        assertThat( new ContentSecurityPolicy().objectSrc( "'none'" ).build() ).isEqualTo( "object-src 'none'" );
        assertThat( new ContentSecurityPolicy().frameSrc( "https://example.com" ).build() ).isEqualTo( "frame-src https://example.com" );
        assertThat( new ContentSecurityPolicy().workerSrc( "blob:" ).build() ).isEqualTo( "worker-src blob:" );
        assertThat( new ContentSecurityPolicy().manifestSrc( "'self'" ).build() ).isEqualTo( "manifest-src 'self'" );
        assertThat( new ContentSecurityPolicy().childSrc( "'self'" ).build() ).isEqualTo( "child-src 'self'" );
        assertThat( new ContentSecurityPolicy().frameAncestors( "https://example.com" ).build() ).isEqualTo(
            "frame-ancestors https://example.com" );
        assertThat( new ContentSecurityPolicy().baseUri( "'none'" ).build() ).isEqualTo( "base-uri 'none'" );
        assertThat( new ContentSecurityPolicy().formAction( "'self'" ).build() ).isEqualTo( "form-action 'self'" );
        assertThat( new ContentSecurityPolicy().scriptSrcElem( "'self'" ).build() ).isEqualTo( "script-src-elem 'self'" );
        assertThat( new ContentSecurityPolicy().scriptSrcAttr( "'none'" ).build() ).isEqualTo( "script-src-attr 'none'" );
        assertThat( new ContentSecurityPolicy().styleSrcElem( "'self'" ).build() ).isEqualTo( "style-src-elem 'self'" );
        assertThat( new ContentSecurityPolicy().styleSrcAttr( "'none'" ).build() ).isEqualTo( "style-src-attr 'none'" );
    }

    @Test
    void shaStyleSrc_bytes_encodes_chosen_algo_into_style_src()
        throws Exception
    {
        final byte[] content = "body { color: red; }".getBytes();
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().shaStyleSrc( HashAlgo.SHA512, content );

        final String expectedDigest = Base64.getEncoder().encodeToString( MessageDigest.getInstance( "SHA-512" ).digest( content ) );
        assertThat( csp.build() ).isEqualTo( "style-src 'sha512-" + expectedDigest + "'" );
    }

    @Test
    void reset_null_directive_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().reset( (String) null ) ).isInstanceOf( NullPointerException.class );
    }

    @Test
    void trustedTypes_null_keyword_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().trustedTypes( (TrustedTypesKeyword) null ) ).isInstanceOf(
            NullPointerException.class );
    }

    @Test
    void reportOnly_is_an_independent_rule_set()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        csp.reportOnly().scriptSrc( CspSource.NONE ).imgSrc( CspSource.SELF );

        assertThat( csp.build() ).isEqualTo( "script-src 'self'" );
        assertThat( csp.reportOnly().build() ).isEqualTo( "img-src 'self'; script-src 'none'" );
    }

    @Test
    void reportOnly_returns_same_companion_and_is_idempotent()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final ContentSecurityPolicy companion = csp.reportOnly();
        assertThat( csp.reportOnly() ).isSameAs( companion );
        assertThat( companion.reportOnly() ).isSameAs( companion );
        assertThat( companion ).isNotSameAs( csp );
    }

    @Test
    void reportOnly_left_untouched_builds_empty()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        assertThat( csp.reportOnly().build() ).isEmpty();
    }

    @Test
    void reportOnly_shares_the_request_nonce()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String nonce = csp.nonceScriptSrc();
        assertThat( csp.reportOnly().nonceScriptSrc() ).isEqualTo( nonce );
    }

    @Test
    void resetTo_replaces_policy_with_header_rules()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF ).imgSrc( CspSource.SELF );
        csp.resetTo( "default-src 'none'; script-src 'self' https://cdn.example.com" );
        assertThat( csp.build() ).isEqualTo( "default-src 'none'; script-src 'self' https://cdn.example.com" );
    }

    @Test
    void resetTo_then_contributions_apply_on_top()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        csp.resetTo( "script-src 'self'" );
        csp.scriptSrc( "https://cdn.example.com" ).imgSrc( CspSource.DATA );
        assertThat( csp.build() ).isEqualTo( "img-src data:; script-src 'self' https://cdn.example.com" );
    }

    @Test
    void resetTo_keeps_boolean_directives()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        csp.resetTo( "upgrade-insecure-requests; script-src 'self'" );
        assertThat( csp.build() ).isEqualTo( "script-src 'self'; upgrade-insecure-requests" );
    }

    @Test
    void resetTo_is_lenient_and_skips_invalid_tokens()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        csp.resetTo( "scr!pt-src oops; script-src 'self' bad,token; ; img-src data:" );
        assertThat( csp.build() ).isEqualTo( "img-src data:; script-src 'self'" );
    }

    @Test
    void resetTo_passes_unknown_but_well_formed_directives_through()
    {
        // like the browser: unknown directive names are kept (and warned about there), not dropped
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        csp.resetTo( "script src; img-src data:" );
        assertThat( csp.build() ).isEqualTo( "img-src data:; script src" );
    }

    @Test
    void resetTo_first_occurrence_of_repeated_directive_wins()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        csp.resetTo( "script-src 'self'; script-src 'unsafe-inline'" );
        assertThat( csp.build() ).isEqualTo( "script-src 'self'" );
    }

    @Test
    void resetTo_empty_value_clears_policy()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        csp.resetTo( "" );
        assertThat( csp.build() ).isEmpty();
    }

    @Test
    void resetTo_null_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().resetTo( null ) ).isInstanceOf( NullPointerException.class );
    }
}
