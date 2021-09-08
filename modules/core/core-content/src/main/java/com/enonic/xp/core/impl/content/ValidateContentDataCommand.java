package com.enonic.xp.core.impl.content;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.content.validate.ContentValidator;
import com.enonic.xp.content.validate.ContentValidatorParams;
import com.enonic.xp.core.impl.content.validate.InputValidator;
import com.enonic.xp.core.impl.content.validate.OccurrenceValidator;
import com.enonic.xp.core.impl.content.validate.SiteConfigValidationError;
import com.enonic.xp.form.Form;
import com.enonic.xp.inputtype.InputTypes;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

import static com.google.common.base.Strings.nullToEmpty;

final class ValidateContentDataCommand
{
    private static final Logger LOG = LoggerFactory.getLogger( ValidateContentDataCommand.class );

    private final ContentTypeService contentTypeService;

    private final XDataService xDataService;

    private final SiteService siteService;

    private final List<ContentValidator> contentValidators;

    private final ContentValidatorParams contentValidatorParams;

    private final ValidationErrors.Builder resultBuilder;

    private ValidateContentDataCommand( Builder builder )
    {
        contentTypeService = builder.contentTypeService;
        xDataService = builder.xDataService;
        siteService = builder.siteService;
        contentValidators = builder.contentValidators;
        contentValidatorParams = builder.contentValidatorParams;
        resultBuilder = ValidationErrors.create();
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
        validateSiteConfigs( contentType );
        validateName();
        for ( ContentValidator contentValidator : contentValidators )
        {
            if (contentValidator.supports( contentType ))
            {
                final ValidationErrors validationErrors = contentValidator.validate( contentValidatorParams );
                resultBuilder.addAll( validationErrors );
            }
        }

        return this.resultBuilder.build();
    }

    private void validateName()
    {
        if ( contentValidatorParams.getName() == null || contentValidatorParams.getName().isUnnamed() )
        {
            this.resultBuilder.add( new ValidationError( "name is required" ) );
        }
        if ( nullToEmpty( contentValidatorParams.getDisplayName() ).isBlank() )
        {
            this.resultBuilder.add( new ValidationError( "displayName is required" ) );
        }
    }

    private void validateSiteConfigs( final ContentType contentType )
    {
        if ( contentType != null )
        {

            if ( contentType.getName().isSite() )
            {

                final SiteConfigs siteConfigs = new SiteConfigsDataSerializer().fromProperties( this.contentValidatorParams.getData().getRoot() ).build();

                for ( final SiteConfig siteConfig : siteConfigs )
                {
                    final ApplicationKey applicationKey = siteConfig.getApplicationKey();

                    if ( siteService != null )
                    {
                        final SiteDescriptor siteDescriptor = siteService.getDescriptor( applicationKey );

                        if ( siteDescriptor != null )
                        {
                            this.resultBuilder.addAll(
                                new OccurrenceValidator( siteDescriptor.getForm() ).validate( siteConfig.getConfig().getRoot() ) );

                            validateSiteForm( siteDescriptor.getForm(), siteConfig );
                        }

                    }
                }
            }
        }
    }

    private void validateSiteForm( final Form form, final SiteConfig siteConfig )
    {
        try
        {
            InputValidator.create().form( form ).inputTypeResolver( InputTypes.BUILTIN ).build().validate( siteConfig.getConfig() );
        }
        catch ( final Exception e )
        {
            this.resultBuilder.add( new SiteConfigValidationError( siteConfig.getApplicationKey().getName() ) );
        }
    }

    private void validateContentTypeForm( final ContentType contentType )
    {
        if ( contentType != null )
        {
            this.resultBuilder.addAll( new OccurrenceValidator( contentType.getForm() ).validate( this.contentValidatorParams.getData().getRoot() ) );
        }
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
                    this.resultBuilder.addAll( new OccurrenceValidator( mixinForm ).validate( extraData.getData().getRoot() ) );
                }
            }
        }
    }


    public static final class Builder
    {
        private ContentTypeService contentTypeService;

        private XDataService xDataService;

        private SiteService siteService;

        private List<ContentValidator> contentValidators;

        private ContentValidatorParams contentValidatorParams;

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

        public Builder siteService( SiteService siteService )
        {
            this.siteService = siteService;
            return this;
        }

        public Builder contentValidators( List<ContentValidator> contentValidators )
        {
            this.contentValidators = contentValidators;
            return this;
        }

        public Builder contentValidatorParams( ContentValidatorParams contentValidatorParams) {
            this.contentValidatorParams = contentValidatorParams;
            return this;
        }

        public ValidateContentDataCommand build()
        {
            return new ValidateContentDataCommand( this );
        }
    }
}
