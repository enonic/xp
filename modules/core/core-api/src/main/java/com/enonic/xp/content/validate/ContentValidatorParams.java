package com.enonic.xp.content.validate;

import java.util.Objects;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.util.BinaryReferences;

public final class ContentValidatorParams
{
    private final ContentId contentId;

    private final ContentTypeName contentType;

    private final PropertyTree data;

    private final ExtraDatas extraDatas;

    private final ContentName name;

    private final String displayName;

    private final CreateAttachments createAttachments;

    private final BinaryReferences removeAttachments;

    private final boolean clearAttachments;

    private final ValidationErrors currentValidationErrors;

    private ContentValidatorParams( Builder builder )
    {
        contentId = builder.contentId;
        data = builder.data;
        extraDatas = Objects.requireNonNullElse( builder.extraDatas, ExtraDatas.empty() );
        contentType = builder.contentType;
        name = builder.name;
        displayName = builder.displayName;
        createAttachments = Objects.requireNonNullElse( builder.createAttachments, CreateAttachments.empty() );
        removeAttachments = Objects.requireNonNullElse( builder.removeAttachments, BinaryReferences.empty() );
        clearAttachments = builder.clearAttachments;
        currentValidationErrors = Objects.requireNonNullElse( builder.currentValidationErrors, ValidationErrors.empty() );
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public ContentTypeName getContentType()
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

    public BinaryReferences getRemoveAttachments()
    {
        return removeAttachments;
    }

    public boolean isClearAttachments()
    {
        return clearAttachments;
    }

    public ValidationErrors getCurrentValidationErrors()
    {
        return currentValidationErrors;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ContentId contentId;

        private ContentTypeName contentType;

        private ContentName name;

        private String displayName;

        private PropertyTree data;

        private ExtraDatas extraDatas;

        private CreateAttachments createAttachments;

        private BinaryReferences removeAttachments;

        private boolean clearAttachments;

        private ValidationErrors currentValidationErrors;

        private Builder()
        {
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder contentType( ContentTypeName contentType )
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

        public Builder removeAttachments( final BinaryReferences removeAttachments )
        {
            this.removeAttachments = removeAttachments;
            return this;
        }

        public Builder clearAttachments( final boolean clearAttachments )
        {
            this.clearAttachments = clearAttachments;
            return this;
        }

        public Builder currentValidationErrors( final ValidationErrors currentValidationErrors )
        {
            this.currentValidationErrors = currentValidationErrors;
            return this;
        }

        public ContentValidatorParams build()
        {
            return new ContentValidatorParams( this );
        }
    }

}
