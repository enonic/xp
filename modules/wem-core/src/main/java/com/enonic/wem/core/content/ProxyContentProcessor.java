package com.enonic.wem.core.content;

import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.attachment.CreateAttachments;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.media.MediaInfo;

class ProxyContentProcessor
{
    private final ImageContentProcessor imageHandler;

    ProxyContentProcessor( final MediaInfo mediaInfo )
    {
        imageHandler = new ImageContentProcessor( mediaInfo );
    }

    CreateContentParams processCreateMedia( final CreateContentParams params )
    {
        if ( params.getType().isImageMedia() )
        {
            return imageHandler.processCreate( params );
        }
        else
        {
            return params;
        }
    }

    ProcessUpdateResult processEdit( final ContentTypeName contentTypeName, final UpdateContentParams updateContentParams,
                                     final CreateAttachments createAttachments )
    {
        if ( contentTypeName.isImageMedia() )
        {
            return imageHandler.processUpdate( updateContentParams, createAttachments );
        }
        else
        {
            return null;
        }
    }
}
