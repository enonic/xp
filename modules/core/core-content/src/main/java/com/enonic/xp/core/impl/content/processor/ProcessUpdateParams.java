package com.enonic.xp.core.impl.content.processor;

import com.enonic.xp.content.Content;
import com.enonic.xp.media.MediaInfo;

public final class ProcessUpdateParams
{
    private final MediaInfo mediaInfo;

    private final Content content;

    private ProcessUpdateParams( final Builder builder )
    {
        mediaInfo = builder.mediaInfo;
        content = builder.content;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public MediaInfo getMediaInfo()
    {
        return mediaInfo;
    }

    public Content getContent()
    {
        return content;
    }

    public static final class Builder
    {
        private MediaInfo mediaInfo;

        private Content content;

        private Builder()
        {
        }

        public Builder mediaInfo( final MediaInfo val )
        {
            mediaInfo = val;
            return this;
        }

        public Builder content( final Content content )
        {
            this.content = content;
            return this;
        }

        public ProcessUpdateParams build()
        {
            return new ProcessUpdateParams( this );
        }
    }
}
