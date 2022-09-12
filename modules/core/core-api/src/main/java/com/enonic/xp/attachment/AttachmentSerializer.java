package com.enonic.xp.attachment;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.DigestInputStream;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.core.internal.HexCoder;
import com.enonic.xp.core.internal.security.MessageDigests;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;

public final class AttachmentSerializer
{
    private AttachmentSerializer()
    {
    }

    public static void create( final PropertyTree propertyTree, final CreateAttachments createAttachments )
    {
        create( propertyTree.getRoot(), createAttachments, ContentPropertyNames.ATTACHMENT );
    }

    public static void create( final PropertySet propertySet, final CreateAttachments createAttachments, final String name )
    {
        for ( final CreateAttachment createAttachment : createAttachments )
        {
            final PropertySet attachmentSet = propertySet.addSet( name );
            attachmentSet.addString( ContentPropertyNames.ATTACHMENT_NAME, createAttachment.getName() );
            attachmentSet.addString( ContentPropertyNames.ATTACHMENT_LABEL, createAttachment.getLabel() );
            attachmentSet.addBinaryReference( ContentPropertyNames.ATTACHMENT_BINARY_REF, createAttachment.getBinaryReference() );
            attachmentSet.addString( ContentPropertyNames.ATTACHMENT_MIMETYPE, createAttachment.getMimeType() );
            attachmentSet.addString( ContentPropertyNames.ATTACHMENT_TEXT, createAttachment.getTextContent() );
            populateByteSourceProperties( createAttachment.getByteSource(), attachmentSet );
        }
    }

    private static void populateByteSourceProperties( final ByteSource byteSource, final PropertySet attachmentSet )
    {
        try
        {
            final InputStream inputStream = byteSource.openStream();
            final DigestInputStream digestInputStream = new DigestInputStream( inputStream, MessageDigests.sha512() );
            try (inputStream; digestInputStream)
            {
                final long size = ByteStreams.exhaust( digestInputStream );
                attachmentSet.addLong( ContentPropertyNames.ATTACHMENT_SIZE, size );
            }
            attachmentSet.addString( ContentPropertyNames.ATTACHMENT_SHA512,
                                     HexCoder.toHex( digestInputStream.getMessageDigest().digest() ) );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }
}
