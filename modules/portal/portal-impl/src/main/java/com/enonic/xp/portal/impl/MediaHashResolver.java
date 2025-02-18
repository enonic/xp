package com.enonic.xp.portal.impl;

import java.nio.charset.StandardCharsets;

import com.google.common.hash.Hashing;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Media;

public final class MediaHashResolver
{
    public static String resolveImageHash( final Media media )
    {
        final Attachment attachment = media.getMediaAttachment();

        if ( attachment.getSha512() == null )
        {
            return null;
        }

        return Hashing.sha1()
            .newHasher()
            .putString( attachment.getSha512().substring( 0, 32 ), StandardCharsets.UTF_8 )
            .putString( String.valueOf( media.getFocalPoint() ), StandardCharsets.UTF_8 )
            .putString( String.valueOf( media.getCropping() ), StandardCharsets.UTF_8 )
            .putString( String.valueOf( media.getOrientation() ), StandardCharsets.UTF_8 )
            .hash()
            .toString();
    }

    public static String resolveAttachmentHash( final Media media )
    {
        final Attachment attachment = media.getMediaAttachment();
        return attachment.getSha512() != null ? attachment.getSha512().substring( 0, 32 ) : null;
    }
}
