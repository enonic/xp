package com.enonic.xp.portal.impl.handler.attachment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.PortalHandler;
import com.enonic.xp.portal.impl.handler.EndpointHandler;
import com.enonic.xp.portal.impl.handler.PortalHandlerWorker;
import com.enonic.xp.web.HttpMethod;

@Component(immediate = true, service = PortalHandler.class)
public final class AttachmentHandler
    extends EndpointHandler
{
    private final static Pattern PATTERN = Pattern.compile( "([^/]+)/([^/^:]+)(:[^/]+)?/([^/]+)" );

    private ContentService contentService;

    public AttachmentHandler()
    {
        super( "attachment" );
        setMethodsAllowed( HttpMethod.GET, HttpMethod.HEAD );
    }

    @Override
    protected PortalHandlerWorker newWorker( final PortalRequest req )
        throws Exception
    {
        final String restPath = findRestPath( req );
        final Matcher matcher = PATTERN.matcher( restPath );

        if ( !matcher.find() )
        {
            throw notFound( "Not a valid attachment url pattern" );
        }

        final AttachmentHandlerWorker worker = new AttachmentHandlerWorker();
        worker.contentService = this.contentService;
        worker.download = "download".equals( matcher.group( 1 ) );
        worker.id = ContentId.from( matcher.group( 2 ) );
        worker.cacheable = matcher.group( 3 ) != null;
        worker.name = matcher.group( 4 );

        return worker;
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
