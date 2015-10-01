package com.enonic.xp.portal.impl.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.portal.url.AttachmentUrlParams;

final class AttachmentUrlBuilder
    extends PortalUrlBuilder<AttachmentUrlParams>
{
    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );
        appendPart( url, this.portalRequest.getContentPath().toString() );
        appendPart( url, "_" );
        appendPart( url, "attachment" );

        if ( this.params.isDownload() )
        {
            appendPart( url, "download" );
        }
        else
        {
            appendPart( url, "inline" );
        }

        final ContentId id = resolveId();
        Attachment attachment = resolveAttachment( id );
        String hash = resolveHash( id, attachment );

        appendPart( url, id.toString() + ":" + hash );
        appendPart( url, attachment.getName() );
    }

    private ContentId resolveId()
    {
        return new ContentIdResolver().
            portalRequest( this.portalRequest ).
            contentService( this.contentService ).
            id( this.params.getId() ).
            path( this.params.getPath() ).
            resolve();
    }

    private Attachment resolveAttachment( final ContentId id )
    {
        final Content content = this.contentService.getById( id );
        final Attachments attachments = content.getAttachments();

        final Attachment attachment;
        if ( this.params.getName() != null )
        {
            attachment = attachments.byName( this.params.getName() );
            if ( attachment == null )
            {
                throw new IllegalArgumentException(
                    "Could not find attachment with name [" + this.params.getName() + "] on content [" + id + "]" );
            }
        }
        else
        {
            final String label = this.params.getLabel() != null ? this.params.getLabel() : "source";
            attachment = attachments.byLabel( label );
            if ( attachment == null )
            {
                throw new IllegalArgumentException( "Could not find attachment with label [" + label + "] on content [" + id + "]" );
            }
        }

        return attachment;
    }

    private String resolveHash( final ContentId id, final Attachment attachment )
    {
        return this.contentService.getBinaryKey( id, attachment.getBinaryReference() );
    }
}
