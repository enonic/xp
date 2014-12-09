package com.enonic.wem.core.content;

import com.google.common.base.Strings;

import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.data.PropertySet;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.util.Binary;

public class ContentAttachmentsNodeTranslator2
{
    // TODO: Where should these be? Needed in repo also I guess
    public static final String BLOB = "blob";

    public static final String MIMETYPE = "mimeType";

    public static final String SIZE = "size";

    public static final String LABEL = "label";

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
        final PropertySet attachmentSet = attachmentsSet.addSet( attachment.getName() );

        attachmentSet.addBinary( BLOB, Binary.from( attachment.getName(), attachment.getMimeType(), attachment.getBlobKey() ) );
        attachmentSet.addString( MIMETYPE, attachment.getMimeType() );
        attachmentSet.addLong( SIZE, attachment.getSize() );

        if ( !Strings.isNullOrEmpty( attachment.getLabel() ) )
        {
            attachmentSet.addString( LABEL, attachment.getLabel() );
        }
    }

}
