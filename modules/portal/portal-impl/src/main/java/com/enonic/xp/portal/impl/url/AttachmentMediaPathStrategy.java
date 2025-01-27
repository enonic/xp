package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.Media;
import com.enonic.xp.project.ProjectName;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendParams;
import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;

final class AttachmentMediaPathStrategy
    implements PathStrategy
{
    private final AttachmentMediaPathStrategyParams params;

    AttachmentMediaPathStrategy( final AttachmentMediaPathStrategyParams params )
    {
        this.params = params;
    }

    @Override
    public String generatePath()
    {
        final Media media = params.getMedia();
        final ProjectName project = params.getProjectName();
        final Branch branch = params.getBranch();

        final StringBuilder url = new StringBuilder();

        appendPart( url, "media" );
        appendPart( url, "attachment" );
        appendPart( url, project + ( ContentConstants.BRANCH_MASTER.equals( branch ) ? "" : ":" + branch ) );

        final Attachment attachment = resolveAttachment();
        final String hash = resolveHash( attachment );

        appendPart( url, media.getId().toString() + ( hash != null ? ":" + hash : "" ) );
        appendPart( url, attachment.getName() );

        final Multimap<String, String> queryParams = resolveQueryParams();
        appendParams( url, queryParams.entries() );

        return url.toString();
    }

    private Multimap<String, String> resolveQueryParams()
    {
        final Multimap<String, String> queryParams = LinkedListMultimap.create();
        if ( this.params.isDownload() )
        {
            queryParams.put( "download", null );
        }
        if ( params.getQueryParams() != null )
        {
            queryParams.putAll( params.getQueryParams() );
        }

        return queryParams;
    }

    private Attachment resolveAttachment()
    {
        final Attachments attachments = params.getMedia().getAttachments();

        final String attachmentNameOrLabel =
            Objects.requireNonNullElseGet( params.getName(), () -> Objects.requireNonNullElse( params.getLabel(), "source" ) );

        Attachment attachment = attachments.byName( attachmentNameOrLabel );
        if ( attachment == null )
        {
            attachment = attachments.byLabel( attachmentNameOrLabel );
        }

        if ( attachment != null )
        {
            return attachment;
        }

        throw new IllegalArgumentException(
            String.format( "Could not find attachment with name/label [%s] on content [%s]", attachmentNameOrLabel,
                           params.getMedia().getId() ) );
    }

    private String resolveHash( final Attachment attachment )
    {
        return attachment.getSha512() != null ? attachment.getSha512().substring( 0, 32 ) : null;
    }
}
