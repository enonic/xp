package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.Media;
import com.enonic.xp.portal.impl.MediaHashResolver;
import com.enonic.xp.project.ProjectName;

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
        final Media media = params.getMediaSupplier().get();
        final ProjectName project = params.getProjectName();
        final Branch branch = params.getBranch();

        final StringBuilder url = new StringBuilder();

        appendPart( url, project + ( ContentConstants.BRANCH_MASTER.equals( branch ) ? "" : ":" + branch ) );

        final Attachment attachment = resolveAttachment( media );
        final String hash = MediaHashResolver.resolveAttachmentHash( attachment );

        appendPart( url, media.getId().toString() + ( hash != null ? ":" + hash : "" ) );
        appendPart( url, attachment.getName() );

        return url.toString();
    }

    private Attachment resolveAttachment( final Media media )
    {
        final Attachments attachments = media.getAttachments();

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
            String.format( "Could not find attachment with name/label [%s] on content [%s]", attachmentNameOrLabel, media.getId() ) );
    }
}
