package com.enonic.xp.attachment;

import java.io.IOException;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.util.Exceptions;

public class AttachmentSerializer
{
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
            attachmentSet.addLong( ContentPropertyNames.ATTACHMENT_SIZE, attachmentSize( createAttachment ) );
            attachmentSet.addString( ContentPropertyNames.ATTACHMENT_TEXT, createAttachment.getTextContent() );
        }
    }

    private static long attachmentSize( final CreateAttachment createAttachment )
    {
        try
        {
            return createAttachment.getByteSource().size();
        }
        catch ( IOException e )
        {
            throw Exceptions.unchecked( e );
        }
    }


}
