package com.enonic.xp.portal.impl.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.PortalHandler2;
import com.enonic.xp.web.HttpMethod;

@Component(immediate = true, service = PortalHandler2.class)
public final class AttachmentHandler
    extends EndpointHandler
{
    private final static Pattern PATTERN = Pattern.compile( "([^/]+)/([^/]+)/([^/]+)" );

    private ContentService contentService;

    public AttachmentHandler()
    {
        super( "attachment" );
        setMethodsAllowed( HttpMethod.GET, HttpMethod.HEAD );
    }

    @Override
    protected PortalResponse doHandle( final PortalRequest req )
        throws Exception
    {
        final String restPath = findRestPath( req );
        final Matcher matcher = PATTERN.matcher( restPath );

        if ( !matcher.find() )
        {
            throw notFound( "Not a valid attachment url pattern" );
        }

        final boolean download = "download".equals( matcher.group( 1 ) );
        final ContentId id = ContentId.from( matcher.group( 2 ) );
        final String name = matcher.group( 3 );

        final Content content = getContent( id );
        final Attachment attachment = resolveAttachment( content, name );
        final ByteSource binary = resolveBinary( id, attachment );

        final PortalResponse.Builder response = PortalResponse.create();
        response.status( 200 );
        response.contentType( attachment.getMimeType() );
        response.body( binary );

        if ( download )
        {
            response.header( "Content-Disposition", "attachment; filename=" + attachment.getName() );
        }

        return response.build();
    }

    private Content getContent( final ContentId contentId )
    {
        final Content content = this.contentService.getById( contentId );
        if ( content == null )
        {
            throw notFound( "Content with id [%s] not found", contentId.toString() );
        }

        return content;
    }

    private ByteSource resolveBinary( final ContentId id, final Attachment attachment )
    {
        final ByteSource binary = this.contentService.getBinary( id, attachment.getBinaryReference() );
        if ( binary == null )
        {
            throw notFound( "Binary [%s] not found for [%s]", attachment.getBinaryReference(), id );
        }

        return binary;
    }

    private Attachment resolveAttachment( final Content content, final String name )
    {
        final Attachments attachments = content.getAttachments();
        final Attachment attachment = attachments.byName( name );
        if ( attachment != null )
        {
            return attachment;
        }

        throw notFound( "Attachment [%s] not found for [%s]", name, content.getPath() );
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
