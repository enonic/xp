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

        final PortalResponse.Builder portalResponse = PortalResponse.create().
            contentType( MediaType.parse( attachment.getMimeType() ) ).
            body( binary );

        if ( this.download )
        {
            portalResponse.header( "Content-Disposition", "attachment; filename=" + attachment.getName() );
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

        return portalResponse.build();
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
}
