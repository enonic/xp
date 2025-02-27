package com.enonic.xp.portal.impl.handler.attachment;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.impl.MediaHashResolver;
import com.enonic.xp.portal.impl.handler.AbstractAttachmentHandlerWorker;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.web.WebRequest;

public final class AttachmentHandlerWorker
    extends AbstractAttachmentHandlerWorker<Content>
{
    public AttachmentHandlerWorker( final WebRequest request, final ContentService contentService )
    {
        super( request, contentService );
    }

    @Override
    protected String resolveHash( final Content content, final Attachment attachment, final BinaryReference binaryReference )
    {
        if ( legacyMode )
        {
            return contentService.getBinaryKey( content.getId(), binaryReference );
        }
        else
        {
            return MediaHashResolver.resolveAttachmentHash( attachment );
        }
    }

    @Override
    protected Content cast( final Content content )
    {
        return content;
    }

    @Override
    protected void addTrace( final Content media )
    {
        final Trace trace = Tracer.current();
        if ( trace != null )
        {
            trace.put( "contentPath", media.getPath() );
            trace.put( "type", "attachment" );
        }
    }
}
