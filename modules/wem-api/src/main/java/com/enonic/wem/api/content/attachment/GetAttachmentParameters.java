package com.enonic.wem.api.content.attachment;

import com.enonic.wem.api.content.ContentId;

public class GetAttachmentParameters
{
    private final ContentId contentId;

    private final String attachmentName;

    private GetAttachmentParameters( Builder builder )
    {
        contentId = builder.contentId;
        attachmentName = builder.attachmentName;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public String getAttachmentName()
    {
        return attachmentName;
    }

    public static final class Builder
    {
        private ContentId contentId;

        private String attachmentName;

        private Builder()
        {
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder attachmentName( String attachmentName )
        {
            this.attachmentName = attachmentName;
            return this;
        }

        public GetAttachmentParameters build()
        {
            return new GetAttachmentParameters( this );
        }
    }
}
