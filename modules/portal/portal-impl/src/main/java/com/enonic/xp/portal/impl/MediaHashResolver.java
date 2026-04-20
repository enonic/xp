package com.enonic.xp.portal.impl;

import java.security.MessageDigest;
import java.util.HexFormat;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Media;
import com.enonic.xp.core.internal.security.MessageDigests;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.media.ImageOrientation;

import static java.util.Objects.requireNonNullElse;

public final class MediaHashResolver
{
    public static String resolveImageHash( final Media media, final String hash )
    {
        if ( hash == null )
        {
            return null;
        }

        final MessageDigest digest = MessageDigests.sha512();

        digest.update( HexFormat.of().parseHex( hash ) );

        final FocalPoint focalPoint = requireNonNullElse( media.getFocalPoint(), FocalPoint.DEFAULT );
        MessageDigests.updateWithDoubleLE( digest, focalPoint.xOffset() );
        MessageDigests.updateWithDoubleLE( digest, focalPoint.yOffset() );

        final Cropping cropping = requireNonNullElse( media.getCropping(), Cropping.DEFAULT );
        MessageDigests.updateWithDoubleLE( digest, cropping.top() );
        MessageDigests.updateWithDoubleLE( digest, cropping.left() );
        MessageDigests.updateWithDoubleLE( digest, cropping.bottom() );
        MessageDigests.updateWithDoubleLE( digest, cropping.right() );
        MessageDigests.updateWithDoubleLE( digest, cropping.zoom() );

        final ImageOrientation orientation = requireNonNullElse( media.getOrientation(), ImageOrientation.DEFAULT );
        MessageDigests.updateWithIntLE( digest, orientation.ordinal() );

        return HexFormat.of().formatHex( digest.digest(), 0, 16 );
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
