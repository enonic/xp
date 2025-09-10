package com.enonic.xp.content;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentType;

public final class ContentValidatorParams
{
    private final ContentId contentId;

    private final ContentType contentType;

    private final PropertyTree data;

    private final ExtraDatas extraDatas;

    private final ContentName name;

    private final String displayName;

    private final CreateAttachments createAttachments;

    private ContentValidatorParams( Builder builder )
    {
        contentId = builder.contentId;
        data = builder.data;
        extraDatas = Objects.requireNonNullElse( builder.extraDatas, ExtraDatas.empty() );
        contentType = builder.contentType;
        name = builder.name;
        displayName = builder.displayName;
        createAttachments = Objects.requireNonNullElse( builder.createAttachments, CreateAttachments.empty() );
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public ContentType getContentType()
    {
        return contentType;
    }

    public ContentName getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public PropertyTree getData()
    {
        return data;
    }

    public ExtraDatas getExtraDatas()
    {
        return extraDatas;
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
        private ContentId contentId;

        private ContentType contentType;

        private ContentName name;

        private String displayName;

        private PropertyTree data;

        private ExtraDatas extraDatas;

        private CreateAttachments createAttachments;

        private Builder()
        {
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder contentType( ContentType contentType )
        {
            this.contentType = contentType;
            return this;
        }

        public Builder name( ContentName name )
        {
            this.name = name;
            return this;
        }

        public Builder displayName( String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder data( PropertyTree contentData )
        {
            this.data = contentData;
            return this;
        }

        public Builder extraDatas( ExtraDatas extraDatas )
        {
            this.extraDatas = extraDatas;
            return this;
        }

        public Builder createAttachments( CreateAttachments createAttachments )
        {
            this.createAttachments = createAttachments;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( contentType, "contentType must be set." );
        }

        public ContentValidatorParams build()
        {
            validate();
            return new ContentValidatorParams( this );
        }
    }
}
