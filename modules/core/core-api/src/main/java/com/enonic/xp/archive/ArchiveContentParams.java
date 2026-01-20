package com.enonic.xp.archive;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentId;

@PublicApi
public final class ArchiveContentParams
{
    private final ContentId contentId;

    private final ArchiveContentListener archiveContentListener;

    private final String message;

    private ArchiveContentParams( final Builder builder )
    {
        this.contentId = builder.contentId;
        this.archiveContentListener = builder.archiveContentListener;
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

    public String getMessage()
    {
        return message;
    }

    public static final class Builder
    {
        private ContentId contentId;

        private ArchiveContentListener archiveContentListener;

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

        public Builder message( final String message )
        {
            this.message = message;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( contentId, "contentId is required" );
        }

        public ArchiveContentParams build()
        {
            validate();
            return new ArchiveContentParams( this );
        }
    }
}
