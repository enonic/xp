package com.enonic.xp.portal.impl.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.enonic.xp.repository.RepositoryUtils;

final class AttachmentUrlBuilder
    extends PortalUrlBuilder<AttachmentUrlParams>
{
    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );

        boolean isSlashAPI = portalRequest.getRawPath().startsWith( "/api/" );

        if ( isSlashAPI )
        {
            url.setLength( 0 );
            appendPart( url, "attachment" );
            appendPart( url, RepositoryUtils.getContentRepoName( this.portalRequest.getRepositoryId() ) );
            appendPart( url, this.portalRequest.getBranch().toString() );
            if ( this.params.isDownload() )
            {
                params.put( "download", null );
            }
        }
        else
        {
            appendPart( url, this.portalRequest.getContentPath().toString() );
            appendPart( url, "_" );
            appendPart( url, "attachment" );
        }

        if ( !isSlashAPI )
        {
            appendPart( url, this.params.isDownload() ? "download" : "inline" );
        }

        final Content content = resolveContent();
        Attachment attachment = resolveAttachment( content );
        String hash = resolveHash( content, attachment );

        appendPart( url, content.getId().toString() + ":" + hash );
        appendPart( url, attachment.getName() );
    }

    @Override
    protected String getBaseUrl()
    {
        return UrlContextHelper.getMediaServiceBaseUrl();
    }

    @Override
    protected String getTargetUriPrefix()
    {
        return "/api/media";
    }

    private Content resolveContent()
    {
        final ContentResolver contentResolver = new ContentResolver().portalRequest( this.portalRequest )
            .contentService( this.contentService )
            .id( this.params.getId() )
            .path( this.params.getPath() );

        return contentResolver.resolve();
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
