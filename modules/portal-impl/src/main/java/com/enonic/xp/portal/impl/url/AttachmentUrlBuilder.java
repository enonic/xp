package com.enonic.xp.portal.impl.url;

import com.google.common.collect.Multimap;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.xp.portal.url.AttachmentUrlParams;

final class AttachmentUrlBuilder
    extends PortalUrlBuilder<AttachmentUrlParams>
{
    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );
        appendPart( url, this.context.getContentPath().toString() );
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
        appendPart( url, id.toString() );

        final String name = resolveAttachmentName( id );
        if ( name != null )
        {
            appendPart( url, name );
        }
    }

    private ContentId resolveId()
    {
        return new ContentIdResolver().
            context( this.context ).
            contentService( this.contentService ).
            id( this.params.getId() ).
            path( this.params.getPath() ).
            resolve();
    }

    private String resolveAttachmentName( final ContentId id )
    {
        if ( this.params.getName() != null )
        {
            return this.params.getName();
        }

        final Content content = this.contentService.getById( id );
        final Attachments attachments = content.getAttachments();

        final String label = this.params.getLabel() != null ? this.params.getLabel() : "source";
        final Attachment attachment = attachments.byLabel( label );

        if ( attachment == null )
        {
            throw new IllegalArgumentException( "Could not find attachment with label [" + label + "] on content [" + id + "]" );
        }

        return attachment.getName();
    }
}
