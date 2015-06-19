package com.enonic.xp.attachment;

import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.Sets;

import com.enonic.xp.content.ContentId;

@Beta
public class UpdateAttachmentsParams
{
    private final ContentId contentId;

    private final Attachments attachments;

    private UpdateAttachmentsParams( final Builder builder )
    {
        this.contentId = builder.contentId;
        this.attachments = Attachments.from( builder.attachments );
    }

    public Attachments getAttachments()
    {
        return attachments;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public static Builder create( final ContentId contentId )
    {
        return new Builder( contentId );
    }

    public static class Builder
    {
        private ContentId contentId;

        private Set<Attachment> attachments = Sets.newHashSet();

        public Builder( final ContentId contentId )
        {
            this.contentId = contentId;
        }

        public Builder addAttachments( final Attachment attachment )
        {
            this.attachments.add( attachment );
            return this;
        }

        public UpdateAttachmentsParams build()
        {
            return new UpdateAttachmentsParams( this );
        }
    }
}
