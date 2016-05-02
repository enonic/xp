package com.enonic.xp.core.impl.content.processor;

import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.content.ContentType;

public class ProcessUpdateParams
{
    private final CreateAttachments createAttachments;

    private final MediaInfo mediaInfo;

    private final ContentType contentType;

    private ProcessUpdateParams( final Builder builder )
    {
        this.createAttachments = builder.createAttachments;
        contentType = builder.contentType;
        mediaInfo = builder.mediaInfo;
    }


    public MediaInfo getMediaInfo()
    {
        return mediaInfo;
    }

    public ContentType getContentType()
    {
        return contentType;
    }

    public CreateAttachments getCreateAttachments()
    {
        return createAttachments;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private ContentType contentType;

        private MediaInfo mediaInfo;

        private CreateAttachments createAttachments;

        private Builder()
        {
        }

        public Builder contentType( final ContentType val )
        {
            contentType = val;
            return this;
        }

        public Builder mediaInfo( final MediaInfo val )
        {
            mediaInfo = val;
            return this;
        }

        public Builder createAttachments( final CreateAttachments createAttachments )
        {
            this.createAttachments = createAttachments;
            return this;
        }

        public ProcessUpdateParams build()
        {
            return new ProcessUpdateParams( this );
        }
    }
}
