package com.enonic.xp.portal.impl;

import java.util.Base64;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.Media;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.ImageStyleSettings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MediaHashResolverTest
{
    private static final String SHA512 = "ec25d6e4126c7064f82aaab8b34693fc";

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

    @Test
    void mediaWithoutASourceChecksumIsStillHashed()
    {
        // Content imported from an export predating attachment checksums has no sha512.
        // It must still get a hash, or its URLs go unsigned and modern formats never render.
        assertEquals( "b96e20ad63279d9ad80c4c6724c3160d", MediaHashResolver.resolveImageHash( createMedia( "content-id", null ) ) );
    }

    @Test
    void checksumlessMediaAreHashedDistinctlyFromEachOther()
    {
        assertNotEquals( MediaHashResolver.resolveImageHash( createMedia( "one", null ) ),
                         MediaHashResolver.resolveImageHash( createMedia( "two", null ) ) );
    }

    @Test
    void checksumlessHashIsStableAcrossCalls()
    {
        assertEquals( MediaHashResolver.resolveImageHash( createMedia( "content-id", null ) ),
                      MediaHashResolver.resolveImageHash( createMedia( "content-id", null ) ) );
    }

    @Test
    void urlGeneratorAndHandlerAgreeOnAChecksumlessMedia()
    {
        // The generator calls the one-argument form, the handler the two-argument one.
        // They must produce the same hash or every request 404s on a fingerprint mismatch.
        final Media media = createMedia( "content-id", null );
        final Attachment source = media.getAttachments().byLabel( "source" );

        assertEquals( MediaHashResolver.resolveImageHash( media ),
                      MediaHashResolver.resolveImageHash( media, MediaHashResolver.resolveAttachmentHash( source ) ) );
    }

    @Test
    void checksumlessHashDiffersFromAChecksumBackedOne()
    {
        assertNotEquals( MediaHashResolver.resolveImageHash( createMedia( "content-id", null ) ),
                         MediaHashResolver.resolveImageHash( createMedia( "content-id", SHA512 ) ) );
    }

    @Test
    void checksumBackedHashIsUnaffected()
    {
        assertEquals( "0a350f43700951cdcca1574f448a7e22",
                      MediaHashResolver.resolveImageHash( createMedia( "content-id", SHA512 ) ) );
    }

    @Test
    void mediaWithoutASourceAttachmentIsNotHashed()
    {
        // Nothing to render: the handler rejects such content before it reaches a hash.
        assertNull( MediaHashResolver.resolveImageHash( createMedia( "content-id", null, false ) ) );
    }

    private static Media createMedia( final String id, final String sha512 )
    {
        return createMedia( id, sha512, true );
    }

    private static Media createMedia( final String id, final @Nullable String sha512, final boolean withSource )
    {
        final Attachment.Builder attachment = Attachment.create().name( "image.jpg" ).mimeType( "image/jpeg" );
        if ( withSource )
        {
            attachment.label( "source" );
        }
        if ( sha512 != null )
        {
            attachment.sha512( sha512 );
        }

        final PropertyTree data = new PropertyTree();
        data.addSet( "media" ).addString( "attachment", "image.jpg" );

        return Media.create()
            .id( ContentId.from( id ) )
            .path( "/mycontent" )
            .type( ContentTypeName.imageMedia() )
            .data( data )
            .attachments( Attachments.from( attachment.build() ) )
            .build();
    }
}
