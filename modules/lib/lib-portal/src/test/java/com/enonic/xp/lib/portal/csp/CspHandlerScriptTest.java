package com.enonic.xp.lib.portal.csp;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.junit.jupiter.api.Test;

import com.enonic.xp.testing.ScriptTestSupport;

public class CspHandlerScriptTest
    extends ScriptTestSupport
{
    @Test
    void returnsObject()
    {
        runFunction( "/test/csp-test.js", "returnsObject" );
    }

    @Test
    void addSources()
    {
        runFunction( "/test/csp-test.js", "addSources" );
    }

    @Test
    void overrideReplaces()
    {
        runFunction( "/test/csp-test.js", "overrideReplaces" );
    }

    @Test
    void addAfterOverride()
    {
        runFunction( "/test/csp-test.js", "addAfterOverride" );
    }

    @Test
    void resetWithNoArgsRemovesNothing()
    {
        runFunction( "/test/csp-test.js", "resetWithNoArgsRemovesNothing" );
    }

    @Test
    void resetToReplacesPolicyAndLaterContributionsApply()
    {
        runFunction( "/test/csp-test.js", "resetToReplacesPolicyAndLaterContributionsApply" );
    }

    @Test
    void resetToEmptyClearsPolicy()
    {
        runFunction( "/test/csp-test.js", "resetToEmptyClearsPolicy" );
    }

    @Test
    void resetToIgnoresPoliciesAfterFirstComma()
    {
        runFunction( "/test/csp-test.js", "resetToIgnoresPoliciesAfterFirstComma" );
    }

    @Test
    void resetToBlankClearsPolicy()
    {
        runFunction( "/test/csp-test.js", "resetToBlankClearsPolicy" );
    }

    @Test
    void resetToUndefinedThrows()
    {
        runFunction( "/test/csp-test.js", "resetToUndefinedThrows" );
    }

    @Test
    void mergeUndefinedThrows()
    {
        runFunction( "/test/csp-test.js", "mergeUndefinedThrows" );
    }

    @Test
    void invalidSourceThrows()
    {
        runFunction( "/test/csp-test.js", "invalidSourceThrows" );
    }

    @Test
    void externalNonceSourceThrows()
    {
        runFunction( "/test/csp-test.js", "externalNonceSourceThrows" );
    }

    @Test
    void resetToDropsExternalNonceSources()
    {
        runFunction( "/test/csp-test.js", "resetToDropsExternalNonceSources" );
    }

    @Test
    void resetRemovesNamedDirectives()
    {
        runFunction( "/test/csp-test.js", "resetRemovesNamedDirectives" );
    }

    @Test
    void unsafeInlineAndNonceBothEmitted()
    {
        runFunction( "/test/csp-test.js", "unsafeInlineAndNonceBothEmitted" );
    }

    @Test
    void unsupportedAlgo()
    {
        runFunction( "/test/csp-test.js", "unsupportedAlgo" );
    }

    @Test
    void fetchDirectiveForwarders()
    {
        runFunction( "/test/csp-test.js", "fetchDirectiveForwarders" );
    }

    @Test
    void shaStyleSrcDigest()
    {
        runFunction( "/test/csp-test.js", "shaStyleSrcDigest" );
    }

    @Test
    void shaScriptSrcContentSha512()
    {
        runFunction( "/test/csp-test.js", "shaScriptSrcContentSha512" );
    }

    @Test
    void example()
    {
        runScript( "/lib/xp/examples/portal/csp.js" );
    }

    @Test
    void nonceScriptSrc()
    {
        runFunction( "/test/csp-test.js", "nonceScriptSrc" );
    }

    @Test
    void nonceStyleSrc()
    {
        runFunction( "/test/csp-test.js", "nonceStyleSrc" );
    }

    @Test
    void nonceScriptSrcElem()
    {
        runFunction( "/test/csp-test.js", "nonceScriptSrcElem" );
    }

    @Test
    void nonceStyleSrcElem()
    {
        runFunction( "/test/csp-test.js", "nonceStyleSrcElem" );
    }

    @Test
    void nonceStableAcrossMethods()
    {
        runFunction( "/test/csp-test.js", "nonceStableAcrossMethods" );
    }

    @Test
    void mergeUnionsIntoExistingAndAddsNew()
    {
        runFunction( "/test/csp-test.js", "mergeUnionsIntoExistingAndAddsNew" );
    }

    @Test
    void mergeDropsExternalNonceSources()
    {
        runFunction( "/test/csp-test.js", "mergeDropsExternalNonceSources" );
    }

    @Test
    void mergeKeepsAWiredNonce()
    {
        runFunction( "/test/csp-test.js", "mergeKeepsAWiredNonce" );
    }

    @Test
    void directiveReads()
    {
        runFunction( "/test/csp-test.js", "directiveReads" );
    }

    @Test
    void reportOnlyIsSeparateRuleSet()
    {
        runFunction( "/test/csp-test.js", "reportOnlyIsSeparateRuleSet" );
    }

    @Test
    void reportOnlySharesNonce()
    {
        runFunction( "/test/csp-test.js", "reportOnlySharesNonce" );
    }

    @Test
    void scriptSrcTypedAndRaw()
    {
        runFunction( "/test/csp-test.js", "scriptSrcTypedAndRaw" );
    }

    @Test
    void scriptSrcAndAddUnion()
    {
        runFunction( "/test/csp-test.js", "scriptSrcAndAddUnion" );
    }

    @Test
    void booleanDirectiveViaAdd()
    {
        runFunction( "/test/csp-test.js", "booleanDirectiveViaAdd" );
    }

    @Test
    void sandboxSingleFlag()
    {
        runFunction( "/test/csp-test.js", "sandboxSingleFlag" );
    }

    @Test
    void sandboxMultipleFlags()
    {
        runFunction( "/test/csp-test.js", "sandboxMultipleFlags" );
    }

    @Test
    void shaScriptSrcContent()
    {
        runFunction( "/test/csp-test.js", "shaScriptSrcContent" );
    }

    @Test
    void shaScriptSrcContentWithAlgo()
    {
        runFunction( "/test/csp-test.js", "shaScriptSrcContentWithAlgo" );
    }

    @Test
    void shaScriptSrcDigest()
    {
        runFunction( "/test/csp-test.js", "shaScriptSrcDigest" );
    }

    @Test
    void shaStyleSrcContent()
    {
        runFunction( "/test/csp-test.js", "shaStyleSrcContent" );
    }

    @Test
    void granularDirectives()
    {
        runFunction( "/test/csp-test.js", "granularDirectives" );
    }

    @Test
    void reportToDirective()
    {
        runFunction( "/test/csp-test.js", "reportToDirective" );
    }

    @Test
    void trustedTypesDirectives()
    {
        runFunction( "/test/csp-test.js", "trustedTypesDirectives" );
    }

    @Test
    void trustedTypesKeywordTokens()
    {
        runFunction( "/test/csp-test.js", "trustedTypesKeywordTokens" );
    }

    @Test
    void cspSourceTokens()
    {
        runFunction( "/test/csp-test.js", "cspSourceTokens" );
    }

    @Test
    void schemeSourcesTyped()
    {
        runFunction( "/test/csp-test.js", "schemeSourcesTyped" );
    }

    @Test
    void sandboxFlagTokens()
    {
        runFunction( "/test/csp-test.js", "sandboxFlagTokens" );
    }

    @Test
    void restrictiveDirectivesTyped()
    {
        runFunction( "/test/csp-test.js", "restrictiveDirectivesTyped" );
    }

    @Test
    void strict()
    {
        runFunction( "/test/csp-test.js", "strict" );
    }

    @Test
    void strictThenOpenUp()
    {
        runFunction( "/test/csp-test.js", "strictThenOpenUp" );
    }

    public String policyBuild()
    {
        return this.portalRequest.getContentSecurityPolicy().serialize();
    }

    public String reportOnlyBuild()
    {
        return this.portalRequest.getContentSecurityPolicy().reportOnly().serialize();
    }

    public String shaBase64( final String content, final String algorithm )
    {
        try
        {
            return Base64.getEncoder()
                .encodeToString( MessageDigest.getInstance( algorithm ).digest( content.getBytes( StandardCharsets.UTF_8 ) ) );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new IllegalStateException( e );
        }
    }
}
