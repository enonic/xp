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
        assertThat( new ContentSecurityPolicy().serialize() ).isEmpty();
    }

    @Test
    void add_single_source()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().add( "script-src", "'self'" );
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self'" );
    }

    @Test
    void add_then_add_unions_and_dedupes()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().add( "script-src", "'self'" )
            .add( "script-src", "'self'", "https://cdn.example.com" );
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self' https://cdn.example.com" );
    }

    @Test
    void cspDirective_constants_drop_into_the_string_methods()
    {
        assertThat( CspDirective.SCRIPT_SRC ).isEqualTo( "script-src" );

        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        csp.add( CspDirective.CONNECT_SRC, "'self'" );
        assertThat( csp.directive( CspDirective.CONNECT_SRC ) ).hasValueSatisfying(
            s -> assertThat( s ).containsExactly( "'self'" ) );
        csp.reset( CspDirective.CONNECT_SRC );
        assertThat( csp.serialize() ).isEmpty();
    }

    @Test
    void merge_unions_into_existing_and_adds_new()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().connectSrc( CspSource.SELF );
        csp.merge( "connect-src https://api.example.com; img-src data:" );
        assertThat( csp.serialize() ).isEqualTo( "connect-src 'self' https://api.example.com; img-src data:" );
    }

    @Test
    void merge_is_lenient_and_drops_external_nonce_sources()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        csp.merge( "script-src 'self' 'nonce-static123'" );
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self'" );
    }

    @Test
    void merge_flattens_comma_separated_policies_into_one_set()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        csp.merge( "default-src 'self', connect-src https://api.example.com" );
        assertThat( csp.serialize() ).isEqualTo( "connect-src https://api.example.com; default-src 'self'" );
    }

    @Test
    void merge_null_adds_nothing()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        csp.merge( null );
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self'" );
    }

    @Test
    void merge_keeps_a_nonce_already_wired()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String nonce = csp.nonceScriptSrc();
        csp.merge( "connect-src https://api.example.com" );
        assertThat( csp.serialize() ).isEqualTo( "connect-src https://api.example.com; script-src 'nonce-" + nonce + "'" );
    }

    @Test
    void directive_reads_sources_and_drives_gap_fill()
    {
        // directive() reports what a contributor declared, so a baseline can gap-fill only what is
        // missing: add( "script-src", "'self'" ) would union 'self' and drop the 'none' identity,
        // loosening the page to allow same-origin scripts. Gating on directive().isEmpty() leaves
        // the declared script-src alone and fills only the missing connect-src.
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().add( "script-src", "'none'" );
        assertThat( csp.directive( "script-src" ) ).hasValueSatisfying( sources -> assertThat( sources ).containsExactly( "'none'" ) );
        assertThat( csp.directive( "connect-src" ) ).isEmpty();

        for ( final String directive : new String[]{"script-src", "connect-src"} )
        {
            if ( csp.directive( directive ).isEmpty() )
            {
                csp.add( directive, "'self'" );
            }
        }
        assertThat( csp.serialize() ).isEqualTo( "connect-src 'self'; script-src 'none'" );
    }

    @Test
    void override_after_add_replaces_directive()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().add( "script-src", "'self'", "https://cdn.example.com" )
            .override( "script-src", "'none'" );
        assertThat( csp.serialize() ).isEqualTo( "script-src 'none'" );
    }

    @Test
    void add_after_override_extends_without_freeze()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().override( "script-src", "'self'" ).add( "script-src", "https://cdn.example.com" );
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self' https://cdn.example.com" );
    }

    @Test
    void reset_with_no_args_removes_nothing()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        csp.reset();
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self'" );
    }

    @Test
    void reset_removes_named_directives()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().scriptSrc( CspSource.SELF ).add( "upgrade-insecure-requests" ).reset( "upgrade-insecure-requests" );
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self'" );
    }

    @Test
    void resetTo_preserves_nonce_so_it_stays_stable()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String nonce = csp.nonceScriptSrc();
        csp.resetTo( null );
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
    void add_external_nonce_source_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().add( "script-src", "'nonce-evil'" ) ).isInstanceOf(
            IllegalArgumentException.class );
        assertThatThrownBy( () -> new ContentSecurityPolicy().add( "style-src", "'NoNcE-evil'" ) ).isInstanceOf(
            IllegalArgumentException.class );
        assertThatThrownBy( () -> new ContentSecurityPolicy().add( "script-src", "'nonce-unterminated" ) ).isInstanceOf(
            IllegalArgumentException.class );
    }

    @Test
    void override_external_nonce_source_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().override( "script-src", "'nonce-evil'" ) ).isInstanceOf(
            IllegalArgumentException.class );
    }

    @Test
    void directive_method_external_nonce_source_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().scriptSrc( "'nonce-evil'" ) ).isInstanceOf(
            IllegalArgumentException.class );
        assertThatThrownBy( () -> new ContentSecurityPolicy().styleSrc( "'nonce-evil'" ) ).isInstanceOf(
            IllegalArgumentException.class );
    }

    @Test
    void unquoted_nonce_lookalike_is_a_plain_host_source()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( "nonce-abc" );
        assertThat( csp.serialize() ).isEqualTo( "script-src nonce-abc" );
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

        assertThat( csp.serialize() ).isEqualTo( "script-src 'nonce-" + first + "'" );
    }

    @Test
    void nonceScriptSrc_targets_script_src_only()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String nonce = csp.nonceScriptSrc();
        assertThat( csp.serialize() ).isEqualTo( "script-src 'nonce-" + nonce + "'" );
    }

    @Test
    void nonceStyleSrc_targets_style_src_only()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String nonce = csp.nonceStyleSrc();
        assertThat( csp.serialize() ).isEqualTo( "style-src 'nonce-" + nonce + "'" );
    }

    @Test
    void nonce_value_stable_across_methods()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String fromScript = csp.nonceScriptSrc();
        assertThat( csp.nonceStyleSrc() ).isEqualTo( fromScript );
        assertThat( csp.serialize() ).isEqualTo(
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
        assertThat( csp.serialize() ).doesNotContain( "nonce-" );
    }

    @Test
    void boolean_directive_serializes_without_sources()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().add( "upgrade-insecure-requests" );
        assertThat( csp.serialize() ).isEqualTo( "upgrade-insecure-requests" );
    }

    @Test
    void frame_ancestors_unions_sources()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().add( "frame-ancestors", "'self'" ).add( "frame-ancestors", "https://example.com" );
        assertThat( csp.serialize() ).isEqualTo( "frame-ancestors 'self' https://example.com" );
    }

    @Test
    void directives_are_emitted_in_alphabetical_order()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().add( "script-src", "'self'" ).add( "img-src", "data:" ).add( "default-src", "'none'" );
        assertThat( csp.serialize() ).isEqualTo( "default-src 'none'; img-src data:; script-src 'self'" );
    }

    @Test
    void unsafe_inline_and_nonce_both_emitted()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().add( "script-src", "'unsafe-inline'" );
        final String nonce = csp.nonceScriptSrc();
        assertThat( csp.serialize() ).isEqualTo( "script-src 'unsafe-inline' 'nonce-" + nonce + "'" );
    }

    @Test
    void unsafe_inline_and_hash_both_emitted()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().add( "style-src", "'unsafe-inline'" ).shaStyleSrc( HashAlgo.SHA256, "AbC" );
        assertThat( csp.serialize() ).isEqualTo( "style-src 'unsafe-inline' 'sha256-AbC'" );
    }

    @Test
    void sources_emitted_in_insertion_order()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String nonce = csp.nonceScriptSrc();
        csp.add( "script-src", "'unsafe-inline'" );
        assertThat( csp.serialize() ).isEqualTo( "script-src 'nonce-" + nonce + "' 'unsafe-inline'" );
    }

    @Test
    void union_keeps_every_source_unresolved()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().scriptSrc( CspSource.SELF, CspSource.UNSAFE_INLINE, CspSource.STRICT_DYNAMIC )
                .scriptSrc( "https:" );
        final String nonce = csp.nonceScriptSrc();
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self' 'unsafe-inline' 'strict-dynamic' https: 'nonce-" + nonce + "'" );
    }

    @Test
    void nonce_kept_without_unsafe_inline()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        final String nonce = csp.nonceScriptSrc();
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self' 'nonce-" + nonce + "'" );
    }

    @Test
    void strict_dynamic_kept_without_unsafe_inline()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.STRICT_DYNAMIC );
        final String nonce = csp.nonceScriptSrc();
        assertThat( csp.serialize() ).isEqualTo( "script-src 'strict-dynamic' 'nonce-" + nonce + "'" );
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
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self'; style-src 'self' 'unsafe-inline'" );
    }

    @Test
    void redundant_none_dropped_when_real_source_present()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().add( "script-src", "'none'" ).add( "script-src", "'self'" );
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self'" );
    }

    @Test
    void none_kept_when_sole_source()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().add( "script-src", "'none'" );
        assertThat( csp.serialize() ).isEqualTo( "script-src 'none'" );
    }

    @Test
    void late_mutation_lands_in_subsequent_build()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().add( "script-src", "'self'" );
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self'" );
        csp.add( "img-src", "data:" );
        assertThat( csp.serialize() ).isEqualTo( "img-src data:; script-src 'self'" );
    }

    @Test
    void override_with_no_sources_creates_empty_directive()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().override( "upgrade-insecure-requests" );
        assertThat( csp.serialize() ).isEqualTo( "upgrade-insecure-requests" );
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
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self'" );
    }

    @Test
    void scriptSrc_multiple_typed_sources()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF, CspSource.UNSAFE_INLINE );
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self' 'unsafe-inline'" );
    }

    @Test
    void scriptSrc_raw_host_emitted_unquoted()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( "https://cdn.example.com" );
        assertThat( csp.serialize() ).isEqualTo( "script-src https://cdn.example.com" );
    }

    @Test
    void scriptSrc_typed_and_raw_are_unioned()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().scriptSrc( CspSource.SELF ).scriptSrc( "https://cdn.example.com" );
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self' https://cdn.example.com" );
    }

    @Test
    void styleSrc_typed_source()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().styleSrc( CspSource.SELF );
        assertThat( csp.serialize() ).isEqualTo( "style-src 'self'" );
    }

    @Test
    void imgSrc_mixed_typed_and_raw()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().imgSrc( CspSource.SELF ).imgSrc( "data:" );
        assertThat( csp.serialize() ).isEqualTo( "img-src 'self' data:" );
    }

    @Test
    void scheme_sources_emitted_unquoted()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().imgSrc( CspSource.SELF, CspSource.DATA, CspSource.BLOB );
        assertThat( csp.serialize() ).isEqualTo( "img-src 'self' data: blob:" );
    }

    @Test
    void wildcard_emitted_unquoted()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().imgSrc( CspSource.WILDCARD, CspSource.DATA );
        assertThat( csp.serialize() ).isEqualTo( "img-src * data:" );
    }

    @Test
    void defaultSrc_none()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().defaultSrc( CspSource.NONE );
        assertThat( csp.serialize() ).isEqualTo( "default-src 'none'" );
    }

    @Test
    void frameAncestors_none()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().frameAncestors( CspSource.NONE );
        assertThat( csp.serialize() ).isEqualTo( "frame-ancestors 'none'" );
    }

    @Test
    void baseUri_self()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().baseUri( CspSource.SELF );
        assertThat( csp.serialize() ).isEqualTo( "base-uri 'self'" );
    }

    @Test
    void formAction_self()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().formAction( CspSource.SELF );
        assertThat( csp.serialize() ).isEqualTo( "form-action 'self'" );
    }

    @Test
    void boolean_directive_via_add_serializes_without_sources()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().add( "upgrade-insecure-requests" );
        assertThat( csp.serialize() ).isEqualTo( "upgrade-insecure-requests" );
    }

    @Test
    void sandbox_unions_flags_unquoted()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().sandbox( SandboxFlag.ALLOW_SCRIPTS, SandboxFlag.ALLOW_SAME_ORIGIN );
        assertThat( csp.serialize() ).isEqualTo( "sandbox allow-scripts allow-same-origin" );
    }

    @Test
    void sandbox_no_flags_emits_directive_only()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().sandbox();
        assertThat( csp.serialize() ).isEqualTo( "sandbox" );
    }

    @Test
    void shaScriptSrc_bytes_encodes_sha256_into_script_src()
        throws Exception
    {
        final byte[] content = "alert('hi');".getBytes();
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().shaScriptSrc( content );

        final String expectedDigest = Base64.getEncoder().encodeToString( MessageDigest.getInstance( "SHA-256" ).digest( content ) );
        assertThat( csp.serialize() ).isEqualTo( "script-src 'sha256-" + expectedDigest + "'" );
    }

    @Test
    void shaScriptSrc_bytes_encodes_chosen_algo_into_script_src()
        throws Exception
    {
        final byte[] content = "alert('hi');".getBytes();
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().shaScriptSrc( HashAlgo.SHA384, content );

        final String expectedDigest = Base64.getEncoder().encodeToString( MessageDigest.getInstance( "SHA-384" ).digest( content ) );
        assertThat( csp.serialize() ).isEqualTo( "script-src 'sha384-" + expectedDigest + "'" );
    }

    @Test
    void shaScriptSrc_precomputed_emits_algo_and_value()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().shaScriptSrc( HashAlgo.SHA384, "AbC" );
        assertThat( csp.serialize() ).isEqualTo( "script-src 'sha384-AbC'" );
    }

    @Test
    void shaStyleSrc_bytes_encodes_sha256_into_style_src()
        throws Exception
    {
        final byte[] content = "body { color: red; }".getBytes();
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().shaStyleSrc( content );

        final String expectedDigest = Base64.getEncoder().encodeToString( MessageDigest.getInstance( "SHA-256" ).digest( content ) );
        assertThat( csp.serialize() ).isEqualTo( "style-src 'sha256-" + expectedDigest + "'" );
    }

    @Test
    void shaStyleSrc_precomputed_emits_algo_and_value()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().shaStyleSrc( HashAlgo.SHA512, "XyZ" );
        assertThat( csp.serialize() ).isEqualTo( "style-src 'sha512-XyZ'" );
    }

    @Test
    void typed_directive_methods_are_chainable()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().defaultSrc( CspSource.NONE )
            .scriptSrc( CspSource.SELF )
            .styleSrc( CspSource.SELF )
            .add( "upgrade-insecure-requests" );
        assertThat( csp.serialize() ).isEqualTo(
            "default-src 'none'; script-src 'self'; style-src 'self'; upgrade-insecure-requests" );
    }

    @Test
    void scriptSrc_null_source_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().scriptSrc( (String) null ) ).isInstanceOf(
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
        assertThat( csp.serialize() ).isEqualTo( "base-uri 'none'; default-src 'none'; frame-ancestors 'none'" );
    }

    @Test
    void strict_then_open_specific_directives()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().strict().scriptSrc( CspSource.SELF ).styleSrc( CspSource.SELF );
        assertThat( csp.serialize() ).isEqualTo(
            "base-uri 'none'; default-src 'none'; frame-ancestors 'none'; script-src 'self'; style-src 'self'" );
    }

    @Test
    void granular_script_and_style_directives()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrcElem( CspSource.SELF )
            .scriptSrcAttr( CspSource.NONE )
            .styleSrcElem( CspSource.SELF )
            .styleSrcAttr( CspSource.NONE );
        assertThat( csp.serialize() ).isEqualTo(
            "script-src-attr 'none'; script-src-elem 'self'; style-src-attr 'none'; style-src-elem 'self'" );
    }

    @Test
    void reportTo_adds_directive()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().reportTo( "csp-endpoint" );
        assertThat( csp.serialize() ).isEqualTo( "report-to csp-endpoint" );
    }

    @Test
    void requireTrustedTypesForScript_adds_script()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().requireTrustedTypesForScript();
        assertThat( csp.serialize() ).isEqualTo( "require-trusted-types-for 'script'" );
    }

    @Test
    void trustedTypes_policy_names_and_keywords()
    {
        final ContentSecurityPolicy csp =
            new ContentSecurityPolicy().trustedTypes( "my-policy" ).trustedTypes( TrustedTypesKeyword.ALLOW_DUPLICATES );
        assertThat( csp.serialize() ).isEqualTo( "trusted-types my-policy 'allow-duplicates'" );
    }

    @Test
    void trustedTypes_keyword_tokens()
    {
        assertThat( new ContentSecurityPolicy().trustedTypes( TrustedTypesKeyword.NONE ).serialize() ).isEqualTo( "trusted-types 'none'" );
        assertThat( new ContentSecurityPolicy().trustedTypes( TrustedTypesKeyword.WILDCARD ).serialize() ).isEqualTo( "trusted-types *" );
    }

    @Test
    void every_fetch_directive_method_targets_its_directive()
    {
        assertThat( new ContentSecurityPolicy().fontSrc( CspSource.SELF ).serialize() ).isEqualTo( "font-src 'self'" );
        assertThat( new ContentSecurityPolicy().connectSrc( CspSource.SELF ).serialize() ).isEqualTo( "connect-src 'self'" );
        assertThat( new ContentSecurityPolicy().mediaSrc( CspSource.SELF ).serialize() ).isEqualTo( "media-src 'self'" );
        assertThat( new ContentSecurityPolicy().objectSrc( CspSource.NONE ).serialize() ).isEqualTo( "object-src 'none'" );
        assertThat( new ContentSecurityPolicy().frameSrc( CspSource.SELF ).serialize() ).isEqualTo( "frame-src 'self'" );
        assertThat( new ContentSecurityPolicy().workerSrc( CspSource.BLOB ).serialize() ).isEqualTo( "worker-src blob:" );
        assertThat( new ContentSecurityPolicy().manifestSrc( CspSource.SELF ).serialize() ).isEqualTo( "manifest-src 'self'" );
        assertThat( new ContentSecurityPolicy().childSrc( CspSource.SELF ).serialize() ).isEqualTo( "child-src 'self'" );
    }

    @Test
    void every_directive_method_accepts_raw_string_sources()
    {
        assertThat( new ContentSecurityPolicy().defaultSrc( "'self'" ).serialize() ).isEqualTo( "default-src 'self'" );
        assertThat( new ContentSecurityPolicy().styleSrc( "https://cdn.example.com" ).serialize() ).isEqualTo(
            "style-src https://cdn.example.com" );
        assertThat( new ContentSecurityPolicy().fontSrc( "data:" ).serialize() ).isEqualTo( "font-src data:" );
        assertThat( new ContentSecurityPolicy().connectSrc( "wss://example.com" ).serialize() ).isEqualTo( "connect-src wss://example.com" );
        assertThat( new ContentSecurityPolicy().mediaSrc( "'self'" ).serialize() ).isEqualTo( "media-src 'self'" );
        assertThat( new ContentSecurityPolicy().objectSrc( "'none'" ).serialize() ).isEqualTo( "object-src 'none'" );
        assertThat( new ContentSecurityPolicy().frameSrc( "https://example.com" ).serialize() ).isEqualTo( "frame-src https://example.com" );
        assertThat( new ContentSecurityPolicy().workerSrc( "blob:" ).serialize() ).isEqualTo( "worker-src blob:" );
        assertThat( new ContentSecurityPolicy().manifestSrc( "'self'" ).serialize() ).isEqualTo( "manifest-src 'self'" );
        assertThat( new ContentSecurityPolicy().childSrc( "'self'" ).serialize() ).isEqualTo( "child-src 'self'" );
        assertThat( new ContentSecurityPolicy().frameAncestors( "https://example.com" ).serialize() ).isEqualTo(
            "frame-ancestors https://example.com" );
        assertThat( new ContentSecurityPolicy().baseUri( "'none'" ).serialize() ).isEqualTo( "base-uri 'none'" );
        assertThat( new ContentSecurityPolicy().formAction( "'self'" ).serialize() ).isEqualTo( "form-action 'self'" );
        assertThat( new ContentSecurityPolicy().scriptSrcElem( "'self'" ).serialize() ).isEqualTo( "script-src-elem 'self'" );
        assertThat( new ContentSecurityPolicy().scriptSrcAttr( "'none'" ).serialize() ).isEqualTo( "script-src-attr 'none'" );
        assertThat( new ContentSecurityPolicy().styleSrcElem( "'self'" ).serialize() ).isEqualTo( "style-src-elem 'self'" );
        assertThat( new ContentSecurityPolicy().styleSrcAttr( "'none'" ).serialize() ).isEqualTo( "style-src-attr 'none'" );
    }

    @Test
    void shaStyleSrc_bytes_encodes_chosen_algo_into_style_src()
        throws Exception
    {
        final byte[] content = "body { color: red; }".getBytes();
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().shaStyleSrc( HashAlgo.SHA512, content );

        final String expectedDigest = Base64.getEncoder().encodeToString( MessageDigest.getInstance( "SHA-512" ).digest( content ) );
        assertThat( csp.serialize() ).isEqualTo( "style-src 'sha512-" + expectedDigest + "'" );
    }

    @Test
    void reset_null_directive_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().reset( (String) null ) ).isInstanceOf( NullPointerException.class );
    }

    @Test
    void trustedTypes_null_value_throws()
    {
        assertThatThrownBy( () -> new ContentSecurityPolicy().trustedTypes( (String) null ) ).isInstanceOf(
            NullPointerException.class );
    }

    @Test
    void reportOnly_is_an_independent_rule_set()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        csp.reportOnly().scriptSrc( CspSource.NONE ).imgSrc( CspSource.SELF );

        assertThat( csp.serialize() ).isEqualTo( "script-src 'self'" );
        assertThat( csp.reportOnly().serialize() ).isEqualTo( "img-src 'self'; script-src 'none'" );
    }

    @Test
    void reportOnly_returns_same_companion_and_is_idempotent()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final ContentSecurityPolicy companion = csp.reportOnly();
        assertThat( csp.reportOnly() ).isSameAs( companion );
        assertThatThrownBy( companion::reportOnly ).isInstanceOf( IllegalStateException.class );
        assertThat( companion ).isNotSameAs( csp );
    }

    @Test
    void reportOnly_left_untouched_builds_empty()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        assertThat( csp.reportOnly().serialize() ).isEmpty();
    }

    @Test
    void reportOnly_shares_the_request_nonce()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String nonce = csp.nonceScriptSrc();
        assertThat( csp.reportOnly().nonceScriptSrc() ).isEqualTo( nonce );
    }

    @Test
    void addPolicy_joins_comma_separated()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        csp.addPolicy().defaultSrc( CspSource.SELF ).objectSrc( CspSource.NONE );
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self', default-src 'self'; object-src 'none'" );
    }

    @Test
    void addPolicy_multiple_in_insertion_order()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        csp.addPolicy().objectSrc( CspSource.NONE );
        csp.addPolicy().baseUri( CspSource.SELF );
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self', object-src 'none', base-uri 'self'" );
    }

    @Test
    void addPolicy_left_empty_is_not_emitted()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        csp.addPolicy();
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self'" );
    }

    @Test
    void addPolicy_emitted_alone_when_main_policy_empty()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        csp.addPolicy().objectSrc( CspSource.NONE );
        assertThat( csp.serialize() ).isEqualTo( "object-src 'none'" );
    }

    @Test
    void addPolicy_shares_the_request_nonce()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String nonce = csp.nonceScriptSrc();
        assertThat( csp.addPolicy().nonceScriptSrc() ).isEqualTo( nonce );
    }

    @Test
    void addPolicy_survives_resetTo_on_the_main_policy()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        csp.addPolicy().objectSrc( CspSource.NONE );
        csp.resetTo( null );
        assertThat( csp.serialize() ).isEqualTo( "object-src 'none'" );
        csp.resetTo( "img-src data:" );
        assertThat( csp.serialize() ).isEqualTo( "img-src data:, object-src 'none'" );
    }

    @Test
    void addPolicy_seeded_from_header_value_and_nonced()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        final String nonce =
            csp.addPolicy().resetTo( "default-src 'self'; script-src 'self'; style-src * 'unsafe-inline'" ).nonceScriptSrc();
        assertThat( csp.serialize() ).isEqualTo(
            "script-src 'self', default-src 'self'; script-src 'self' 'nonce-" + nonce + "'; style-src * 'unsafe-inline'" );
    }

    @Test
    void addPolicy_is_not_part_of_report_only()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        csp.addPolicy().objectSrc( CspSource.NONE );
        assertThat( csp.reportOnly().serialize() ).isEmpty();
        assertThat( csp.serialize() ).isEqualTo( "object-src 'none'" );
    }

    @Test
    void reportOnly_can_carry_added_policies_of_its_own()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        csp.reportOnly().scriptSrc( CspSource.SELF ).addPolicy().objectSrc( CspSource.NONE );
        assertThat( csp.reportOnly().serialize() ).isEqualTo( "script-src 'self', object-src 'none'" );
        assertThat( csp.serialize() ).isEmpty();
    }

    @Test
    void reportOnly_throws_on_added_and_report_only_policies()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        // report-only is a single companion of the top-level enforced policy; reaching it from an
        // added policy or from the companion itself is a misuse and fails fast
        assertThatThrownBy( () -> csp.addPolicy().reportOnly() ).isInstanceOf( IllegalStateException.class );
        assertThatThrownBy( () -> csp.reportOnly().reportOnly() ).isInstanceOf( IllegalStateException.class );
        assertThatThrownBy( () -> csp.reportOnly().addPolicy().reportOnly() ).isInstanceOf( IllegalStateException.class );
    }

    @Test
    void clearAdditionalPolicies_drops_appended_policies_keeps_directives()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        csp.addPolicy().objectSrc( CspSource.NONE );
        csp.clearAdditionalPolicies();
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self'" );
    }

    @Test
    void clearAdditionalPolicies_drops_all_added_policies()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().defaultSrc( CspSource.NONE );
        csp.addPolicy().scriptSrc( CspSource.NONE );
        csp.addPolicy().objectSrc( CspSource.NONE );
        csp.clearAdditionalPolicies();
        assertThat( csp.serialize() ).isEqualTo( "default-src 'none'" );
    }

    @Test
    void clearAdditionalPolicies_leaves_report_only_untouched()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        csp.addPolicy().objectSrc( CspSource.NONE );
        csp.reportOnly().scriptSrc( CspSource.NONE );
        csp.clearAdditionalPolicies();
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self'" );
        assertThat( csp.reportOnly().serialize() ).isEqualTo( "script-src 'none'" );
    }

    @Test
    void report_only_emptied_via_resetTo_and_clearAdditionalPolicies()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        csp.reportOnly().scriptSrc( CspSource.NONE ).addPolicy().objectSrc( CspSource.NONE );
        // how a render host empties the report-only set: clear its directives and drop its added policies
        csp.reportOnly().resetTo( null ).clearAdditionalPolicies();
        assertThat( csp.reportOnly().serialize() ).isEmpty();
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self'" );
    }

    @Test
    void resetTo_replaces_policy_with_header_rules()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF ).imgSrc( CspSource.SELF );
        csp.resetTo( "default-src 'none'; script-src 'self' https://cdn.example.com" );
        assertThat( csp.serialize() ).isEqualTo( "default-src 'none'; script-src 'self' https://cdn.example.com" );
    }

    @Test
    void resetTo_then_contributions_apply_on_top()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        csp.resetTo( "script-src 'self'" );
        csp.scriptSrc( "https://cdn.example.com" ).imgSrc( CspSource.DATA );
        assertThat( csp.serialize() ).isEqualTo( "img-src data:; script-src 'self' https://cdn.example.com" );
    }

    @Test
    void resetTo_keeps_boolean_directives()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        csp.resetTo( "upgrade-insecure-requests; script-src 'self'" );
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self'; upgrade-insecure-requests" );
    }

    @Test
    void resetTo_is_lenient_and_skips_invalid_tokens()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        csp.resetTo( "scr!pt-src oops; script-src 'self' bad\u0001token; ; img-src data:" );
        assertThat( csp.serialize() ).isEqualTo( "img-src data:; script-src 'self'" );
    }

    @Test
    void resetTo_passes_unknown_but_well_formed_directives_through()
    {
        // like the browser: unknown directive names are kept (and warned about there), not dropped
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        csp.resetTo( "script src; img-src data:" );
        assertThat( csp.serialize() ).isEqualTo( "img-src data:; script src" );
    }

    @Test
    void resetTo_drops_external_nonce_sources()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        csp.resetTo( "script-src 'self' 'nonce-static123'; style-src 'NONCE-x' 'unsafe-inline'" );
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self'; style-src 'unsafe-inline'" );

        final String nonce = csp.nonceScriptSrc();
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self' 'nonce-" + nonce + "'; style-src 'unsafe-inline'" );
    }

    @Test
    void nonceScriptSrcElem_wires_the_request_nonce_into_script_src_elem()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String nonce = csp.nonceScriptSrcElem();
        assertThat( csp.serialize() ).isEqualTo( "script-src-elem 'nonce-" + nonce + "'" );
        // shares the one request nonce with the other nonce* methods
        assertThat( csp.nonceScriptSrc() ).isEqualTo( nonce );
    }

    @Test
    void nonceStyleSrcElem_wires_the_request_nonce_into_style_src_elem()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        final String nonce = csp.nonceStyleSrcElem();
        assertThat( csp.serialize() ).isEqualTo( "style-src-elem 'nonce-" + nonce + "'" );
        assertThat( csp.nonceStyleSrc() ).isEqualTo( nonce );
    }

    @Test
    void resetTo_ignores_policies_after_the_first_comma()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        csp.resetTo( "default-src 'none', script-src 'self'" );
        // only the first policy is applied; the comma and everything after it is ignored
        assertThat( csp.serialize() ).isEqualTo( "default-src 'none'" );
    }

    @Test
    void resetTo_again_replaces_the_previous_directives()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        csp.resetTo( "default-src 'none'; object-src 'none'" );
        csp.resetTo( "img-src data:" );
        assertThat( csp.serialize() ).isEqualTo( "img-src data:" );
    }

    @Test
    void resetTo_ignores_its_comma_policies_but_keeps_added_policies()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        csp.addPolicy().objectSrc( CspSource.NONE );
        csp.resetTo( "script-src 'self', img-src data:" );
        // 'img-src data:' (after the comma) is dropped; the explicitly added policy is untouched
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self', object-src 'none'" );
    }

    @Test
    void resetTo_first_occurrence_of_repeated_directive_wins()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy();
        csp.resetTo( "script-src 'self'; script-src 'unsafe-inline'" );
        assertThat( csp.serialize() ).isEqualTo( "script-src 'self'" );
    }

    @Test
    void resetTo_empty_value_clears_policy()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        csp.resetTo( "" );
        assertThat( csp.serialize() ).isEmpty();
    }

    @Test
    void resetTo_blank_value_clears_policy()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        csp.resetTo( " \t ; ; " );
        assertThat( csp.serialize() ).isEmpty();
    }

    @Test
    void resetTo_null_clears_policy()
    {
        final ContentSecurityPolicy csp = new ContentSecurityPolicy().scriptSrc( CspSource.SELF );
        csp.resetTo( null );
        assertThat( csp.serialize() ).isEmpty();
    }
}
