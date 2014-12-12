package com.enonic.wem.core.content;

import com.google.common.base.Strings;

import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.data.PropertySet;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.util.BinaryReference;

import static com.enonic.wem.api.node.AttachmentPropertyNames.BLOB;
import static com.enonic.wem.api.node.AttachmentPropertyNames.LABEL;
import static com.enonic.wem.api.node.AttachmentPropertyNames.MIMETYPE;
import static com.enonic.wem.api.node.AttachmentPropertyNames.SIZE;

public class ContentAttachmentsNodeTranslator2
{
    // TODO: Where should these be? Needed in repo also I guess


    public static PropertySet translate( final PropertyTree propertyTree, final Attachments attachments )
    {
        PropertySet set = propertyTree.newSet();

        if ( attachments.isEmpty() )
        {
            return set;
        }

        for ( final Attachment attachment : attachments )
        {
            addAttachment( set, attachment );
        }

        return set;
    }

    private static void addAttachment( final PropertySet attachmentsSet, final Attachment attachment )
    {
        final PropertySet attachmentSet = attachmentsSet.addSet( createAttachmentSetName( attachment.getName() ) );

        attachmentSet.addBinaryReference( BLOB,
                                          BinaryReference.from( attachment.getName(), attachment.getMimeType(), attachment.getBlobKey() ) );
        attachmentSet.addString( MIMETYPE, attachment.getMimeType() );
        attachmentSet.addLong( SIZE, attachment.getSize() );

        if ( !Strings.isNullOrEmpty( attachment.getLabel() ) )
        {
            attachmentSet.addString( LABEL, attachment.getLabel() );
        }
    }

    private static String createAttachmentSetName( final String attachmentName )
    {
        String attachmentSetName = attachmentName.replace( ".", "_" );
        attachmentSetName = attachmentSetName.replace( "[", "_" );
        attachmentSetName = attachmentSetName.replace( "]", "_" );

        return attachmentSetName;
    }

}
