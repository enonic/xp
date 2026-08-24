package com.enonic.xp.content;

import static java.util.Objects.requireNonNull;


public final class DeleteContentParams
{
    private final ContentPath contentPath;

    private final DeleteContentListener deleteContentListener;

    private final PushContentListener unpublishListener;

    private DeleteContentParams( Builder builder )
    {
        contentPath = builder.contentPath;
        deleteContentListener = builder.deleteContentListener;
        unpublishListener = builder.unpublishListener;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentPath getContentPath()
    {
        return contentPath;
    }

    public DeleteContentListener getDeleteContentListener()
    {
        return deleteContentListener;
    }

    public PushContentListener getUnpublishListener()
    {
        return unpublishListener;
    }

    public static final class Builder
    {
        private ContentPath contentPath;

        private DeleteContentListener deleteContentListener;

        private PushContentListener unpublishListener;

        private Builder()
        {
        }

        public Builder contentPath( ContentPath contentPath )
        {
            this.contentPath = contentPath;
            return this;
        }

        public Builder deleteContentListener( final DeleteContentListener deleteContentListener )
        {
            this.deleteContentListener = deleteContentListener;
            return this;
        }

        /**
         * Sets the listener of the unpublishing a delete does before it deletes anything: deleting a published content takes it offline
         * first, and this listener hears that part of the work, where {@link #deleteContentListener(DeleteContentListener)} hears the
         * deletion itself. Contents that were not published are passed over silently.
         *
         * @since 8.1.0
         */
        public Builder unpublishListener( final PushContentListener unpublishListener )
        {
            this.unpublishListener = unpublishListener;
            return this;
        }

        public DeleteContentParams build()
        {
            requireNonNull( this.contentPath, "contentPath is required" );
            return new DeleteContentParams( this );
        }
    }
}
