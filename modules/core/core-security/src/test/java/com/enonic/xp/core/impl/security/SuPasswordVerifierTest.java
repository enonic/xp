package com.enonic.xp.core.impl.security;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.internal.security.MessageDigests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SuPasswordVerifierTest
{
    @Test
    void verify_sha256_correct_password()
    {
        final char[] password = "testpassword".toCharArray();
        final String hash = computeSha256Hash( password );

        final SuPasswordVerifier verifier = new SuPasswordVerifier( "{sha256}" + hash );

        assertTrue( verifier.verify( "testpassword".toCharArray(), null ) );
    }

    @Test
    void verify_sha256_wrong_password()
    {
        final char[] password = "testpassword".toCharArray();
        final String hash = computeSha256Hash( password );

        final SuPasswordVerifier verifier = new SuPasswordVerifier( "{sha256}" + hash );

        assertFalse( verifier.verify( "wrongpassword".toCharArray(), null ) );
    }

    @Test
    void verify_sha512_correct_password()
    {
        final char[] password = "testpassword".toCharArray();
        final String hash = computeSha512Hash( password );

        final SuPasswordVerifier verifier = new SuPasswordVerifier( "{sha512}" + hash );

        assertTrue( verifier.verify( "testpassword".toCharArray(), null ) );
    }

    @Test
    void verify_sha512_wrong_password()
    {
        final char[] password = "testpassword".toCharArray();
        final String hash = computeSha512Hash( password );

        final SuPasswordVerifier verifier = new SuPasswordVerifier( "{sha512}" + hash );

        assertFalse( verifier.verify( "wrongpassword".toCharArray(), null ) );
    }

    @Test
    void verify_empty_property_fails()
    {
        final SuPasswordVerifier verifier = new SuPasswordVerifier( "" );

        assertFalse( verifier.verify( "anypassword".toCharArray(), null ) );
    }

    @Test
    void verify_invalid_format_fails()
    {
        final SuPasswordVerifier verifier = new SuPasswordVerifier( "invalidformat" );

        assertFalse( verifier.verify( "anypassword".toCharArray(), null ) );
    }

    @Test
    void verify_password_case_sensitive()
    {
        final char[] password = "TestPassword".toCharArray();
        final String hash = computeSha256Hash( password );

        final SuPasswordVerifier verifier = new SuPasswordVerifier( "{sha256}" + hash );

        assertTrue( verifier.verify( "TestPassword".toCharArray(), null ) );
        assertFalse( verifier.verify( "testpassword".toCharArray(), null ) );
        assertFalse( verifier.verify( "TESTPASSWORD".toCharArray(), null ) );
    }

    private static String computeSha256Hash( final char[] password )
    {
        final MessageDigest digest = MessageDigests.sha256();
        digest.update( StandardCharsets.UTF_8.encode( CharBuffer.wrap( password ) ) );
        return HexFormat.of().formatHex( digest.digest() );
    }

    private static String computeSha512Hash( final char[] password )
    {
        final MessageDigest digest = MessageDigests.sha512();
        digest.update( StandardCharsets.UTF_8.encode( CharBuffer.wrap( password ) ) );
        return HexFormat.of().formatHex( digest.digest() );
    }
}
