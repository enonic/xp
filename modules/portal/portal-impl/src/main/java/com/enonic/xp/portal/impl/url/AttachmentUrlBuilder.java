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

        final Content content = resolveContent();
        Attachment attachment = resolveAttachment( content );
        String hash = resolveHash( content, attachment );

        appendPart( url, content.getId().toString() + ":" + hash );
        appendPart( url, attachment.getName() );
    }

    private Content resolveContent()
    {
        final ContentId contentId = new ContentIdResolver().
            portalRequest( this.portalRequest ).
            contentService( this.contentService ).
            id( this.params.getId() ).
            path( this.params.getPath() ).
            resolve();

        return this.contentService.getById( contentId );
    }

    private Attachment resolveAttachment( final Content content )
    {
        final Attachments attachments = content.getAttachments();

        final Attachment attachment;
        if ( this.params.getName() != null )
        {
            attachment = attachments.byName( this.params.getName() );
            if ( attachment == null )
            {
                throw new IllegalArgumentException(
                    "Could not find attachment with name [" + this.params.getName() + "] on content [" + content.getId() + "]" );
            }
        }
        else
        {
            final String label = this.params.getLabel() != null ? this.params.getLabel() : "source";
            attachment = attachments.byLabel( label );
            if ( attachment == null )
            {
                throw new IllegalArgumentException(
                    "Could not find attachment with label [" + label + "] on content [" + content.getId() + "]" );
            }
        }

        return attachment;
    }

    private String resolveHash( final Content content, final Attachment attachment )
    {
        return this.contentService.getBinaryKey( content.getId(), attachment.getBinaryReference() );
    }
}
