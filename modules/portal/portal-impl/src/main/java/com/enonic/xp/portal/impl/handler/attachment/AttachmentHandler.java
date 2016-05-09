package com.enonic.xp.portal.impl.handler.attachment;

import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalWebRequest;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.handler.EndpointHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.handler.WebRequest;
import com.enonic.xp.web.handler.WebResponse;

@Component(immediate = true, service = WebHandler.class)
public final class AttachmentHandler
    extends EndpointHandler
{
    private final static Pattern PATTERN = Pattern.compile( "([^/]+)/([^/^:]+)(:[^/]+)?/([^/]+)" );

    private ContentService contentService;

    public AttachmentHandler()
    {
        super( EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS ), "attachment" );
    }

    @Override
    public boolean canHandle( final WebRequest webRequest )
    {
        return super.canHandle( webRequest ) && webRequest instanceof PortalWebRequest;
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
    {
        final String endpointSubPath = getEndpointSubPath( webRequest );
        final Matcher matcher = PATTERN.matcher( endpointSubPath );
        if ( !matcher.find() )
        {
            throw notFound( "Not a valid attachment url pattern" );
        }

        return AttachmentWebHandlerWorker.create().
            webRequest( (PortalWebRequest) webRequest ).
            webResponse( webResponse ).
            contentService( contentService ).
            download( "download".equals( matcher.group( 1 ) ) ).
            id( ContentId.from( matcher.group( 2 ) ) ).
            cacheable( matcher.group( 3 ) != null ).
            name( matcher.group( 4 ) ).
            build().
            execute();
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
