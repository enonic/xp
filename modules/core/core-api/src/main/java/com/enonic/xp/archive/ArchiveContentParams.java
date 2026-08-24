package com.enonic.xp.archive;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.PushContentListener;

import static java.util.Objects.requireNonNull;


public final class ArchiveContentParams
{
    private final ContentId contentId;

    private final ArchiveContentListener archiveContentListener;

    private final PushContentListener unpublishListener;

    private final String message;

    private ArchiveContentParams( final Builder builder )
    {
        this.contentId = builder.contentId;
        this.archiveContentListener = builder.archiveContentListener;
        this.unpublishListener = builder.unpublishListener;
        this.message = builder.message;
    }

    public static ArchiveContentParams.Builder create()
    {
        return new ArchiveContentParams.Builder();
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public ArchiveContentListener getArchiveContentListener()
    {
        return archiveContentListener;
    }

    public PushContentListener getUnpublishListener()
    {
        return unpublishListener;
    }

    public String getMessage()
    {
        return message;
    }

    public static final class Builder
    {
        private ContentId contentId;

        private ArchiveContentListener archiveContentListener;

        private PushContentListener unpublishListener;

        private String message;

        private Builder()
        {
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder archiveContentListener( ArchiveContentListener archiveContentListener )
        {
            this.archiveContentListener = archiveContentListener;
            return this;
        }

        /**
         * Sets the listener of the unpublishing an archive does before it moves anything: archiving a content takes it offline, and this
         * listener hears that part of the work, where {@link #archiveContentListener(ArchiveContentListener)} hears the archiving
         * itself. Contents that were not published are passed over silently.
         *
         * @since 8.1.0
         */
        public Builder unpublishListener( final PushContentListener unpublishListener )
        {
            this.unpublishListener = unpublishListener;
            return this;
        }

        public Builder message( final String message )
        {
            this.message = message;
            return this;
        }

        private void validate()
        {
            requireNonNull( contentId, "contentId is required" );
        }

        public ArchiveContentParams build()
        {
            validate();
            return new ArchiveContentParams( this );
        }
    }
}
