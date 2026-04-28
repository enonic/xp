package com.enonic.xp.portal.impl;

import java.security.MessageDigest;
import java.util.HexFormat;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.Media;
import com.enonic.xp.content.MediaUtils;
import com.enonic.xp.core.internal.security.MessageDigests;
import com.enonic.xp.data.PropertySet;
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

    public static String resolveImageHash( final Media media )
    {
        final Attachment attachment = media.getAttachments().byLabel( "source" );

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
