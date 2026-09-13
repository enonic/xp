package com.enonic.xp.portal.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.Media;
import com.enonic.xp.content.MediaUtils;
import com.enonic.xp.core.internal.security.MessageDigests;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.style.ImageStyleSettings;

import static java.util.Objects.requireNonNullElse;

@NullMarked
public final class MediaHashResolver
{
    public static @Nullable String resolveImageHash( final Media media, final @Nullable String hash )
    {
        if ( hash == null )
        {
            return null;
        }

        final MessageDigest digest = MessageDigests.sha512();

        digest.update( HexFormat.of().parseHex( hash ) );

        final PropertySet mediaData = media.getData().getSet( ContentPropertyNames.MEDIA );
        final FocalPoint focalPoint = requireNonNullElse( MediaUtils.readFocalPoint( mediaData ), FocalPoint.DEFAULT );
        MessageDigests.updateWithDoubleLE( digest, focalPoint.xOffset() );
        MessageDigests.updateWithDoubleLE( digest, focalPoint.yOffset() );

        final Cropping cropping = requireNonNullElse( MediaUtils.readCropping( mediaData ), Cropping.DEFAULT );
        MessageDigests.updateWithDoubleLE( digest, cropping.top() );
        MessageDigests.updateWithDoubleLE( digest, cropping.left() );
        MessageDigests.updateWithDoubleLE( digest, cropping.bottom() );
        MessageDigests.updateWithDoubleLE( digest, cropping.right() );

        final ImageOrientation orientation = requireNonNullElse(
            MediaUtils.readOrientation( mediaData ), ImageOrientation.DEFAULT );
        MessageDigests.updateWithIntLE( digest, orientation.ordinal() );

        return HexFormat.of().formatHex( digest.digest(), 0, 16 );
    }

    public static @Nullable String resolveImageHash( final Media media )
    {
        final Attachment attachment = media.getAttachments().byLabel( "source" );

        if ( attachment == null || attachment.getSha512() == null )
        {
            return null;
        }

        return resolveImageHash( media, resolveAttachmentHash( attachment ) );
    }

    public static @Nullable String resolveImageFingerprint( final @Nullable String imageHash, final ImageStyleSettings settings, final @Nullable ScaleParams scale,
                                                 final String mimeType, final HmacService hmacService )
    {
        if ( imageHash == null )
        {
            return imageHash;
        }
        final MessageDigest digest = MessageDigests.sha512();
        digest.update( HexFormat.of().parseHex( imageHash ) );
        // Length-prefix fields to keep the fingerprint independent of delimiters in filters.
        updateField( digest, scale == null ? null : scale.toString() );
        updateField( digest, mimeType );
        updateField( digest, settings.aspectRatio() );
        updateField( digest, settings.filter() );
        updateField( digest, Integer.toString( settings.quality() ) );
        updateField( digest, Integer.toHexString( settings.background() ) );
        // Domain separation prevents a redirect checksum from authorizing an image rendition.
        return hmacService.generateChecksum( "image-fingerprint-v2\0" + HexFormat.of().formatHex( digest.digest(), 0, 16 ) );
    }

    public static boolean matchesFingerprint( final @Nullable String expected, final @Nullable String supplied )
    {
        return expected != null && supplied != null && MessageDigest.isEqual(
            expected.getBytes( StandardCharsets.UTF_8 ), supplied.getBytes( StandardCharsets.UTF_8 ) );
    }

    private static void updateField( final MessageDigest digest, final @Nullable String value )
    {
        final byte[] bytes = value == null ? new byte[0] : value.getBytes( StandardCharsets.UTF_8 );
        MessageDigests.updateWithIntLE( digest, bytes.length );
        digest.update( bytes );
    }

    public static @Nullable String resolveAttachmentHash( final @Nullable Attachment attachment )
    {
        return attachment == null || attachment.getSha512() == null ? null : attachment.getSha512().substring( 0, 32 );
    }
}
