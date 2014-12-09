package com.enonic.wem.api.content;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.attachment.Attachment;

public class ImageMediaHelper
{
    /**
     * Returns the source Attachment for the Image.
     */
    public static Attachment getImageAttachment( final Content content )
    {
        Preconditions.checkState( content.getType().isImageMedia(), "Expected content to be of type image: " + content.getType() );

        final String imageAttachmentName = content.getData().getString( "image" );
        if ( imageAttachmentName == null )
        {
            return null;
        }

        return content.getAttachments().getAttachment( imageAttachmentName );
    }

}
