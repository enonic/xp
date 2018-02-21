package com.enonic.xp.portal.impl.handler.attachment;

import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

import static com.enonic.xp.web.servlet.ServletRequestUrlHelper.contentDispositionAttachment;

final class AttachmentHandlerWorker
    extends PortalHandlerWorker<PortalRequest>
{
    protected ContentService contentService;

    protected ContentId id;

    protected String name;

    protected boolean download;

    protected boolean cacheable;

    public AttachmentHandlerWorker( final PortalRequest request )
    {
        super( request );
    }

    @Override
    public PortalResponse execute()
        throws Exception
    {
        final Content content = getContent( this.id );
        final Attachment attachment = resolveAttachment( content, this.name );
        final ByteSource binary = resolveBinary( this.id, attachment );

        if ( request.getMethod() == HttpMethod.OPTIONS )
        {
            // it will be handled by default OPTIONS handler in BaseWebHandler
            return PortalResponse.create().status( HttpStatus.METHOD_NOT_ALLOWED ).build();
        }

        final MediaType contentType = MediaType.parse( attachment.getMimeType() );
        final PortalResponse.Builder portalResponse = PortalResponse.create().
            contentType( contentType ).
            body( binary );

        if ( this.download )
        {
            portalResponse.header( "Content-Disposition", contentDispositionAttachment( attachment.getName() ) );
        }
        if ( this.name.endsWith( ".svgz" ) )
        {
            portalResponse.header( "Content-Encoding", "gzip" );
        }
        if ( this.cacheable )
        {
            final AccessControlEntry publicAccessControlEntry = content.getPermissions().getEntry( RoleKeys.EVERYONE );
            final boolean everyoneCanRead = publicAccessControlEntry != null && publicAccessControlEntry.isAllowed( Permission.READ );
            final boolean masterBranch = ContentConstants.BRANCH_MASTER.equals( request.getBranch() );
            setResponseCacheable( portalResponse, everyoneCanRead && masterBranch );
        }

        new RangeRequestHelper().handleRangeRequest( request, portalResponse, binary, contentType );

        return portalResponse.build();
    }

    private Content getContent( final ContentId contentId )
    {
        final Content content = getContentById( contentId );
        if ( content == null )
        {
            if ( this.contentService.contentExists( contentId ) )
            {
                throw forbidden( "You don't have permission to access [%s]", contentId );
            }
            else
            {
                throw notFound( "Content with id [%s] not found", contentId.toString() );
            }
        }

        return content;
    }

    private Content getContentById( final ContentId contentId )
    {
        try
        {
            return this.contentService.getById( contentId );
        }
        catch ( final Exception e )
        {
            return null;
        }
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
}
