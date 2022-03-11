package com.enonic.xp.portal.impl.handler.attachment;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.handler.AbstractAttachmentHandlerWorker;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;

final class AttachmentHandlerWorker
    extends AbstractAttachmentHandlerWorker<Content>
{
    AttachmentHandlerWorker( final PortalRequest request, final ContentService contentService )
    {
        super( request, contentService );
    }

    @Override
    protected Content cast( final Content content )
    {
        return content;
    }

    @Override
    protected void addTrace( final Content content )
    {
        final Trace trace = Tracer.current();
        if ( trace != null )
        {
            trace.put( "contentPath", content.getPath() );
            trace.put( "type", "attachment" );
        }
    }
}
