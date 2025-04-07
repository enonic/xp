package com.enonic.xp.portal.impl.url;

import java.util.Objects;
import java.util.function.Supplier;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.portal.impl.MediaHashResolver;
import com.enonic.xp.project.ProjectName;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;

final class AttachmentMediaPathSupplier
    implements Supplier<String>
{
    private final Supplier<Content> contentSupplier;

    private final Supplier<ProjectName> projectNameSupplier;

    private final Supplier<Branch> branchSupplier;

    private final String name;

    private final String label;

    AttachmentMediaPathSupplier( final Builder builder )
    {
        this.contentSupplier = builder.contentSupplier;
        this.projectNameSupplier = builder.projectNameSupplier;
        this.branchSupplier = builder.branchSupplier;
        this.name = builder.name;
        this.label = builder.label;
    }

    @Override
    public String get()
    {
        final Content content = Objects.requireNonNull( contentSupplier.get() );
        final ProjectName project = Objects.requireNonNull( projectNameSupplier.get() );
        final Branch branch = Objects.requireNonNull( branchSupplier.get() );

        final StringBuilder url = new StringBuilder();

        appendPart( url, project + ( ContentConstants.BRANCH_MASTER.equals( branch ) ? "" : ":" + branch ) );

        final Attachment attachment = resolveAttachment( content );
        final String hash = MediaHashResolver.resolveAttachmentHash( attachment );

        appendPart( url, content.getId().toString() + ( hash != null ? ":" + hash : "" ) );
        appendPart( url, attachment.getName() );

        return url.toString();
    }

    private Attachment resolveAttachment( final Content content )
    {
        final Attachments attachments = content.getAttachments();

        final String attachmentNameOrLabel = Objects.requireNonNullElseGet( name, () -> Objects.requireNonNullElse( label, "source" ) );

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
            String.format( "Could not find attachment with name/label [%s] on content [%s]", attachmentNameOrLabel, content.getId() ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private Supplier<Content> contentSupplier;

        private Supplier<ProjectName> projectNameSupplier;

        private Supplier<Branch> branchSupplier;

        private String name;

        private String label;

        public Builder setContent( final Supplier<Content> contentSupplier )
        {
            this.contentSupplier = contentSupplier;
            return this;
        }

        public Builder setProjectName( final Supplier<ProjectName> projectNameSupplier )
        {
            this.projectNameSupplier = projectNameSupplier;
            return this;
        }

        public Builder setBranch( final Supplier<Branch> branchSupplier )
        {
            this.branchSupplier = branchSupplier;
            return this;
        }

        public Builder setName( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder setLabel( final String label )
        {
            this.label = label;
            return this;
        }

        public AttachmentMediaPathSupplier build()
        {
            return new AttachmentMediaPathSupplier( this );
        }
    }
}
