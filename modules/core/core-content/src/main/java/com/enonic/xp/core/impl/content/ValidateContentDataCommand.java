package com.enonic.xp.core.impl.content;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.content.validate.ContentValidator;
import com.enonic.xp.content.validate.ContentValidatorParams;
import com.enonic.xp.core.impl.content.validate.OccurrenceValidator;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataService;

final class ValidateContentDataCommand
{
    private static final Logger LOG = LoggerFactory.getLogger( ValidateContentDataCommand.class );

    private final ContentTypeService contentTypeService;

    private final XDataService xDataService;

    private final List<ContentValidator> contentValidators;

    private final ContentValidatorParams.Builder contentValidatorParamsBuilder;

    private final ContentTypeName contentTypeName;

    private final ValidationErrors.Builder resultBuilder;

    private ValidateContentDataCommand( Builder builder )
    {
        contentTypeService = builder.contentTypeService;
        xDataService = builder.xDataService;
        contentValidators = builder.contentValidators;
        contentValidatorParamsBuilder = ContentValidatorParams.create()
            .contentId( builder.contentId )
            .name( builder.contentName )
            .displayName( builder.displayName )
            .data( builder.data )
            .extraDatas( builder.extraDatas )
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

        Preconditions.checkArgument( contentType != null, "ContentType [%s] not found", contentTypeName );
        final ContentValidatorParams validatorParams = contentValidatorParamsBuilder.contentType( contentType ).build();

        validateMetadata( validatorParams );
        for ( ContentValidator contentValidator : contentValidators )
        {
            if ( contentValidator.supports( contentTypeName ) )
            {
                contentValidator.validate( validatorParams, resultBuilder );
            }
        }

        return this.resultBuilder.build();
    }

    private void validateMetadata( final ContentValidatorParams validatorParams )
    {
        for ( final ExtraData extraData : validatorParams.getExtraDatas() )
        {
            final XDataName name = extraData.getName();

            final XData xData = this.xDataService.getByName( name );
            if ( xData == null )
            {
                LOG.warn( "Mixin not found: '" + name );
                continue;
            }

            final Form mixinForm = xData.getForm();
            if ( extraData.getData().getRoot().getPropertySize() > 0 )
            {
                OccurrenceValidator.validate( mixinForm, extraData.getData().getRoot(), this.resultBuilder );
            }
        }
    }

    public static final class Builder
    {
        private ContentTypeService contentTypeService;

        private XDataService xDataService;

        private List<ContentValidator> contentValidators;

        private ValidationErrors.Builder validationErrorsBuilder;

        private ContentId contentId;

        private ContentTypeName contentTypeName;

        private PropertyTree data;

        private ExtraDatas extraDatas;

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

        public Builder xDataService( XDataService xDataService )
        {
            this.xDataService = xDataService;
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

        public Builder extraDatas( final ExtraDatas extraDatas )
        {
            this.extraDatas = extraDatas;
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
