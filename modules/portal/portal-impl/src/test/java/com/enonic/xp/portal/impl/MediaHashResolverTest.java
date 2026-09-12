package com.enonic.xp.portal.impl;

import java.util.Base64;

import org.junit.jupiter.api.Test;

import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.style.ImageStyle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MediaHashResolverTest
{
    @Test
    void styledFingerprintRequiresSharedSecretAndIsSeparatedFromRedirectChecksums()
    {
        final HmacService hmac = HmacTestHelper.createHmacService();
        final ImageStyle style = ImageStyle.create().name( "card" ).build();
        final ScaleParams scale = new ScaleParams( "width", new Object[]{640} );
        final String imageHash = "0a350f43700951cdcca1574f448a7e22";
        final String unsigned = "f4774dff7b6ef5d0fc1f077cbec55899";
        final String fingerprint = MediaHashResolver.resolveStyledImageHash( imageHash, style, scale, hmac );
        assertEquals( "b70c37373c28d80778129544e9b504a6a0560ed0", fingerprint );
        assertFalse( MediaHashResolver.matchesFingerprint( fingerprint, unsigned ) );
        assertFalse( MediaHashResolver.matchesFingerprint( fingerprint, hmac.generateChecksum( unsigned ) ) );
        assertFalse( MediaHashResolver.matchesFingerprint( fingerprint, null ) );
        assertTrue( MediaHashResolver.matchesFingerprint( fingerprint, fingerprint ) );
        final HmacService otherKey = HmacTestHelper.createHmacService( Base64.getEncoder().encodeToString( new byte[64] ) );
        assertNotEquals( fingerprint, MediaHashResolver.resolveStyledImageHash( imageHash, style, scale, otherKey ) );
        assertEquals( imageHash, MediaHashResolver.resolveStyledImageHash( imageHash, null, scale, hmac ) );
    }
}
