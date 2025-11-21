package com.enonic.xp.core.impl.content;

import java.util.List;
import java.util.Objects;

import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentValidator;
import com.enonic.xp.content.ContentValidatorParams;
import com.enonic.xp.content.Mixins;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;

final class ValidateContentDataCommand
{
    private final ContentTypeService contentTypeService;

    private final List<ContentValidator> contentValidators;

    private final ContentValidatorParams.Builder contentValidatorParamsBuilder;

    private final ContentTypeName contentTypeName;

    private final ValidationErrors.Builder resultBuilder;

    private ValidateContentDataCommand( Builder builder )
    {
        contentTypeService = builder.contentTypeService;
        contentValidators = builder.contentValidators;
        contentValidatorParamsBuilder = ContentValidatorParams.create()
            .contentId( builder.contentId )
            .name( builder.contentName )
            .displayName( builder.displayName )
            .data( builder.data )
            .mixins( builder.mixins )
            .createAttachments( builder.createAttachments );
        contentTypeName = builder.contentTypeName;
        resultBuilder = Objects.requireNonNullElseGet( builder.validationErrorsBuilder, ValidationErrors::create );
    }

    public static Builder create()
    {
        return new Builder();
    }

    ValidationErrors execute()
    {
        final ContentType contentType = contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) );

        final ContentValidatorParams validatorParams = contentValidatorParamsBuilder.contentType( contentType ).build();

        for ( ContentValidator contentValidator : contentValidators )
        {
            if ( contentValidator.supports( contentTypeName ) )
            {
                contentValidator.validate( validatorParams, resultBuilder );
            }
        }

        return this.resultBuilder.build();
    }

    public static final class Builder
    {
        private ContentTypeService contentTypeService;

        private List<ContentValidator> contentValidators;

        private ValidationErrors.Builder validationErrorsBuilder;

        private ContentId contentId;

        private ContentTypeName contentTypeName;

        private PropertyTree data;

        private Mixins mixins;

        private ContentName contentName;

        private String displayName;

        private CreateAttachments createAttachments;

        private Builder()
        {
        }

        public Builder contentTypeService( ContentTypeService contentTypeService )
        {
            this.contentTypeService = contentTypeService;
            return this;
        }

        public Builder contentValidators( List<ContentValidator> contentValidators )
        {
            this.contentValidators = contentValidators;
            return this;
        }

        public Builder validationErrorsBuilder( final ValidationErrors.Builder validationErrorsBuilder )
        {
            this.validationErrorsBuilder = validationErrorsBuilder;
            return this;
        }

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder contentTypeName( final ContentTypeName contentTypeName )
        {
            this.contentTypeName = contentTypeName;
            return this;
        }

        public Builder data( final PropertyTree data )
        {
            this.data = data;
            return this;
        }

        public Builder mixins( final Mixins mixins )
        {
            this.mixins = mixins;
            return this;
        }

        public Builder contentName( final ContentName contentName )
        {
            this.contentName = contentName;
            return this;
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder createAttachments( final CreateAttachments createAttachments )
        {
            this.createAttachments = createAttachments;
            return this;
        }

        public ValidateContentDataCommand build()
        {
            return new ValidateContentDataCommand( this );
        }
    }
}
