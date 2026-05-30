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
    void setResets()
    {
        runFunction( "/test/csp-test.js", "setResets" );
    }

    @Test
    void addAfterSet()
    {
        runFunction( "/test/csp-test.js", "addAfterSet" );
    }

    @Test
    void unsafeInlineDropsNonce()
    {
        runFunction( "/test/csp-test.js", "unsafeInlineDropsNonce" );
    }

    @Test
    void unsupportedAlgo()
    {
        runFunction( "/test/csp-test.js", "unsupportedAlgo" );
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
    void nonceStableAcrossMethods()
    {
        runFunction( "/test/csp-test.js", "nonceStableAcrossMethods" );
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
    void upgradeInsecureRequests()
    {
        runFunction( "/test/csp-test.js", "upgradeInsecureRequests" );
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
    void addScriptSrcShaContent()
    {
        runFunction( "/test/csp-test.js", "addScriptSrcShaContent" );
    }

    @Test
    void addScriptSrcShaContentWithAlgo()
    {
        runFunction( "/test/csp-test.js", "addScriptSrcShaContentWithAlgo" );
    }

    @Test
    void addScriptSrcShaDigest()
    {
        runFunction( "/test/csp-test.js", "addScriptSrcShaDigest" );
    }

    @Test
    void addStyleSrcShaContent()
    {
        runFunction( "/test/csp-test.js", "addStyleSrcShaContent" );
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

    @Test
    void strictDynamic()
    {
        runFunction( "/test/csp-test.js", "strictDynamic" );
    }

    @Test
    void strictDynamicBootstrappedWithNonce()
    {
        runFunction( "/test/csp-test.js", "strictDynamicBootstrappedWithNonce" );
    }

    public String policyBuild()
    {
        return this.portalRequest.getContentSecurityPolicy().build();
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
