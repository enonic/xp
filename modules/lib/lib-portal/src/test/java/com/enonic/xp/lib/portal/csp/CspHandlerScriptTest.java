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
        runFunction( "/test/getCsp-test.js", "returnsObject" );
    }

    @Test
    void addSources()
    {
        runFunction( "/test/getCsp-test.js", "addSources" );
    }

    @Test
    void setResets()
    {
        runFunction( "/test/getCsp-test.js", "setResets" );
    }

    @Test
    void addAfterSet()
    {
        runFunction( "/test/getCsp-test.js", "addAfterSet" );
    }

    @Test
    void addShaContent()
    {
        runFunction( "/test/getCsp-test.js", "addShaContent" );
    }

    @Test
    void addShaDigest()
    {
        runFunction( "/test/getCsp-test.js", "addShaDigest" );
    }

    @Test
    void unsupportedAlgo()
    {
        runFunction( "/test/getCsp-test.js", "unsupportedAlgo" );
    }

    @Test
    void nonceLazyAndStable()
    {
        runFunction( "/test/getCsp-test.js", "nonceLazyAndStable" );
    }

    @Test
    void manualNonceInStyleSrc()
    {
        runFunction( "/test/getCsp-test.js", "manualNonceInStyleSrc" );
    }

    public String policyBuild()
    {
        return this.portalRequest.getContentSecurityPolicy().build();
    }

    public String sha256Base64( final String content )
    {
        try
        {
            return Base64.getEncoder()
                .encodeToString( MessageDigest.getInstance( "SHA-256" ).digest( content.getBytes( StandardCharsets.UTF_8 ) ) );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new IllegalStateException( e );
        }
    }
}
