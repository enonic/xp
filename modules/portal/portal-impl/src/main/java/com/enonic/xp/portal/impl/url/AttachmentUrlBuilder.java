package com.enonic.xp.portal.impl.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.portal.impl.ContentResolverResult;
import com.enonic.xp.portal.impl.VirtualHostContextHelper;
import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.enonic.xp.repository.RepositoryUtils;

final class AttachmentUrlBuilder
    extends PortalUrlBuilder<AttachmentUrlParams>
{
    private boolean legacyAttachmentServiceEnabled;

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        final boolean isSlashAPI = portalRequest.getRawPath().startsWith( "/api/" );
        final String projectName = RepositoryUtils.getContentRepoName( this.portalRequest.getRepositoryId() );
        final Branch branch = this.portalRequest.getBranch();

        if ( isSlashAPI )
        {
            url.setLength( 0 );
            appendPart( url, "attachment" );
            appendPart( url, branch == ContentConstants.BRANCH_DRAFT ? projectName + ":" + branch.getValue() : projectName );
            if ( this.params.isDownload() )
            {
                params.put( "download", null );
            }
        }
        else
        {
            super.buildUrl( url, params );

            if ( legacyAttachmentServiceEnabled )
            {
                appendPart( url, this.portalRequest.getContentPath().toString() );
                appendPart( url, "_" );
                appendPart( url, "attachment" );
                appendPart( url, this.params.isDownload() ? "download" : "inline" );
            }
            else
            {
                final ContentResolverResult contentResolverResult =
                    new com.enonic.xp.portal.impl.ContentResolver( contentService ).resolve( portalRequest );

                if ( contentResolverResult.getNearestSite() != null )
                {
                    appendPart( url, contentResolverResult.getNearestSite().getPath().toString() );
                }
                else
                {
                    url.setLength( 0 );
                    appendPart( url, "site" );
                    appendPart( url, projectName );
                    appendPart( url, branch.getValue() );
                }

                appendPart( url, "_" );
                appendPart( url, "media" );
                appendPart( url, "attachment" );
                appendPart( url, branch == ContentConstants.BRANCH_DRAFT ? projectName + ":" + branch.getValue() : projectName );
                if ( this.params.isDownload() )
                {
                    params.put( "download", null );
                }
            }
        }

        final Content content = resolveContent();
        final Attachment attachment = resolveAttachment( content );
        final String hash = resolveHash( content, attachment );

        appendPart( url, content.getId().toString() + ( hash != null ? ":" + hash : "" ) );
        appendPart( url, attachment.getName() );
    }

    @Override
    protected String getBaseUrl()
    {
        return VirtualHostContextHelper.getMediaServiceBaseUrl();
    }

    @Override
    protected String getTargetUriPrefix()
    {
        return "/api/media";
    }

    public void setLegacyAttachmentServiceEnabled( final boolean legacyAttachmentServiceEnabled )
    {
        this.legacyAttachmentServiceEnabled = legacyAttachmentServiceEnabled;
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
        if ( legacyAttachmentServiceEnabled )
        {
            return this.contentService.getBinaryKey( content.getId(), attachment.getBinaryReference() );
        }
        else
        {
            return attachment.getSha512() != null ? attachment.getSha512().substring( 0, 32 ) : null;
        }
    }
}
