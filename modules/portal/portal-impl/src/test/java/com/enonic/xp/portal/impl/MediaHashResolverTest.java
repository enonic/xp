package com.enonic.xp.portal.impl;

import java.util.Base64;

import org.junit.jupiter.api.Test;

import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.ImageStyleSettings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MediaHashResolverTest
{
    @Test
    void equivalentStyleDefaultsHaveTheSameFingerprint()
    {
        final HmacService hmac = HmacTestHelper.createHmacService();
        final ScaleParams scale = new ScaleParams( "width", new Object[]{640} );
        final String imageHash = "0a350f43700951cdcca1574f448a7e22";
        final String defaultFingerprint = MediaHashResolver.resolveImageFingerprint(
            imageHash, ImageStyleSettings.from( ImageStyle.create().name( "card" ).build() ), scale, "image/png", hmac );
        for ( String background : new String[]{"ffffff", "FFFFFF", "0xffffff"} )
        {
            final ImageStyle explicit = ImageStyle.create().name( "alias" ).quality( 85 ).background( background ).build();
            assertEquals( defaultFingerprint, MediaHashResolver.resolveImageFingerprint( imageHash, ImageStyleSettings.from( explicit ), scale, "image/png", hmac ) );
        }
    }

    @Test
    void styledFingerprintRequiresSharedSecretAndIsSeparatedFromRedirectChecksums()
    {
        final HmacService hmac = HmacTestHelper.createHmacService();
        final ImageStyle style = ImageStyle.create().name( "card" ).build();
        final ScaleParams scale = new ScaleParams( "width", new Object[]{640} );
        final String imageHash = "0a350f43700951cdcca1574f448a7e22";
        final String unsigned = "f4774dff7b6ef5d0fc1f077cbec55899";
        final String fingerprint = MediaHashResolver.resolveImageFingerprint( imageHash, ImageStyleSettings.from( style ), scale, "image/png", hmac );
        assertEquals( "a0d08e08c2fb73604365a4460c9833a054631806", fingerprint );
        assertFalse( MediaHashResolver.matchesFingerprint( fingerprint, "8e4e644c301041bc3d6d3b58e5bfeb15efba6661" ) );
        assertFalse( MediaHashResolver.matchesFingerprint( fingerprint, unsigned ) );
        assertFalse( MediaHashResolver.matchesFingerprint( fingerprint, hmac.generateChecksum( unsigned ) ) );
        assertFalse( MediaHashResolver.matchesFingerprint( fingerprint, null ) );
        assertTrue( MediaHashResolver.matchesFingerprint( fingerprint, fingerprint ) );
        final HmacService otherKey = HmacTestHelper.createHmacService( Base64.getEncoder().encodeToString( new byte[64] ) );
        assertNotEquals( fingerprint, MediaHashResolver.resolveImageFingerprint( imageHash, ImageStyleSettings.from( style ), scale, "image/png", otherKey ) );
        assertNotEquals( fingerprint, MediaHashResolver.resolveImageFingerprint( imageHash, ImageStyleSettings.from( style ), scale, "image/avif", hmac ) );
    }
}
