package com.enonic.wem.api.content.attachment;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.context.Context;

public class GetAttachmentParameters
{
    private final ContentId contentId;

    private final String attachmentName;

    private final Context context;

    private GetAttachmentParameters( Builder builder )
    {
        contentId = builder.contentId;
        attachmentName = builder.attachmentName;
        context = builder.context;
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

    public Context getContext()
    {
        return context;
    }


    public static final class Builder
    {
        private ContentId contentId;

        private String attachmentName;

        private Context context;

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

        public Builder context( Context context )
        {
            this.context = context;
            return this;
        }

        public GetAttachmentParameters build()
        {
            return new GetAttachmentParameters( this );
        }
    }
}
