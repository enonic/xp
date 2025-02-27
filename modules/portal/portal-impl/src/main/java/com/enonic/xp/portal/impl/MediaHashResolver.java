package com.enonic.xp.portal.impl;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Objects;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Media;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.media.ImageOrientation;

public final class MediaHashResolver
{
    public static String resolveLegacyImageHash( final Media media, final String hash )
    {
        return Hashing.sha1()
            .newHasher()
            .putString( String.valueOf( hash ), StandardCharsets.UTF_8 )
            .putString( String.valueOf( media.getFocalPoint() ), StandardCharsets.UTF_8 )
            .putString( String.valueOf( media.getCropping() ), StandardCharsets.UTF_8 )
            .putString( String.valueOf( media.getOrientation() ), StandardCharsets.UTF_8 )
            .hash()
            .toString();
    }

    public static String resolveImageHash( final Media media, final String hash )
    {
        if ( hash == null )
        {
            return null;
        }

        final Hasher hasher = Hashing.sha512().newHasher();

        hasher.putBytes( HashCode.fromString( hash ).asBytes() );

        final FocalPoint focalPoint = Objects.requireNonNullElse( media.getFocalPoint(), FocalPoint.DEFAULT );

        hasher.putDouble( focalPoint.xOffset() );
        hasher.putDouble( focalPoint.yOffset() );

        final Cropping cropping = Objects.requireNonNullElse( media.getCropping(), Cropping.DEFAULT );

        hasher.putDouble( cropping.top() );
        hasher.putDouble( cropping.left() );
        hasher.putDouble( cropping.bottom() );
        hasher.putDouble( cropping.right() );
        hasher.putDouble( cropping.zoom() );

        final ImageOrientation orientation = Objects.requireNonNullElse( media.getOrientation(), ImageOrientation.DEFAULT );
        hasher.putInt( orientation.ordinal() );

        return HexFormat.of().formatHex( hasher.hash().asBytes(), 0, 16 );
    }

    public static String resolveImageHash( final Media media )
    {
        final Attachment attachment = media.getMediaAttachment();

        if ( attachment == null || attachment.getSha512() == null )
        {
            return null;
        }

        return resolveImageHash( media, resolveAttachmentHash( attachment ) );
    }

    public static String resolveAttachmentHash( final Attachment attachment )
    {
        return attachment == null || attachment.getSha512() == null ? null : attachment.getSha512().substring( 0, 32 );
    }
}
