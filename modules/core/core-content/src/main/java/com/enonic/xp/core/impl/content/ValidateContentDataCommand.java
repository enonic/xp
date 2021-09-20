package com.enonic.xp.core.impl.content;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.content.validate.ContentValidator;
import com.enonic.xp.content.validate.ContentValidatorParams;
import com.enonic.xp.core.impl.content.validate.OccurrenceValidator;
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

    private final ContentValidatorParams contentValidatorParams;

    private final ValidationErrors.Builder resultBuilder;

    private ValidateContentDataCommand( Builder builder )
    {
        contentTypeService = builder.contentTypeService;
        xDataService = builder.xDataService;
        contentValidators = builder.contentValidators;
        contentValidatorParams = builder.contentValidatorParams;
        resultBuilder = Objects.requireNonNullElseGet( builder.validationErrorsBuilder, ValidationErrors::create );
    }

    public static Builder create()
    {
        return new Builder();
    }


    ValidationErrors execute()
    {
        return doExecute();
    }

    ValidationErrors doExecute()
    {
        final ContentTypeName contentTypeName = this.contentValidatorParams.getContentType();
        final ContentType contentType = contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) );

        Preconditions.checkArgument( contentType != null, "ContentType [%s] not found", contentTypeName );

        validateContentTypeForm( contentType );
        validateMetadata();
        for ( ContentValidator contentValidator : contentValidators )
        {
            if ( contentValidator.supports( contentTypeName ) )
            {
                contentValidator.validate( contentValidatorParams, resultBuilder );
            }
        }

        return this.resultBuilder.build();
    }

    private void validateContentTypeForm( final ContentType contentType )
    {
        this.resultBuilder.addAll(
            new OccurrenceValidator( contentType.getForm() ).validate( this.contentValidatorParams.getData().getRoot() ).getSet() );
    }

    private void validateMetadata()
    {
        if ( this.contentValidatorParams.getExtraDatas() != null )
        {
            for ( final ExtraData extraData : this.contentValidatorParams.getExtraDatas() )
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
                    this.resultBuilder.addAll( new OccurrenceValidator( mixinForm ).validate( extraData.getData().getRoot() ).getSet() );
                }
            }
        }
    }

    public static final class Builder
    {
        private ContentTypeService contentTypeService;

        private XDataService xDataService;

        private List<ContentValidator> contentValidators;

        private ContentValidatorParams contentValidatorParams;

        private ValidationErrors.Builder validationErrorsBuilder;

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

        public Builder contentValidatorParams( ContentValidatorParams contentValidatorParams )
        {
            this.contentValidatorParams = contentValidatorParams;
            return this;
        }

        public Builder validationErrorsBuilder( final ValidationErrors.Builder validationErrorsBuilder )
        {
            this.validationErrorsBuilder = validationErrorsBuilder;
            return this;
        }

        public ValidateContentDataCommand build()
        {
            return new ValidateContentDataCommand( this );
        }
    }
}
