package com.enonic.xp.core.impl.content;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.core.impl.content.validate.InputValidator;
import com.enonic.xp.core.impl.content.validate.OccurrenceValidator;
import com.enonic.xp.core.impl.content.validate.SiteConfigValidationError;
import com.enonic.xp.core.impl.content.validate.ValidationError;
import com.enonic.xp.core.impl.content.validate.ValidationErrors;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.inputtype.InputTypes;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

final class ValidateContentDataCommand
{
    private final static Logger LOG = LoggerFactory.getLogger( ValidateContentDataCommand.class );

    private final ContentTypeService contentTypeService;

    private final MixinService mixinService;

    private final SiteService siteService;

    private final PropertyTree contentData;

    private final ExtraDatas extraDatas;

    private final ContentTypeName contentType;

    private final ContentName name;

    private final String displayName;

    private final ValidationErrors.Builder resultBuilder;

    private ValidateContentDataCommand( Builder builder )
    {
        contentTypeService = builder.contentTypeService;
        mixinService = builder.mixinService;
        siteService = builder.siteService;
        contentData = builder.contentData;
        extraDatas = builder.extraDatas;
        contentType = builder.contentType;
        name = builder.name;
        displayName = builder.displayName;
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
        final PropertyTree contentData = this.contentData;
        final ContentTypeName contentTypeName = this.contentType;
        final ContentType contentType =
            contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ).inlineMixinsToFormItems( true ) );

        Preconditions.checkArgument( contentType != null, "ContentType [%s] not found", contentTypeName );

        validateContentTypeForm( contentData, contentType );
        validateMetadata();
        validateSiteConfigs( contentType );

        validateName( name, displayName );

        return this.resultBuilder.build();
    }

    private void validateName( final ContentName name, final String displayName )
    {

        if ( name == null || name.isUnnamed() )
        {
            this.resultBuilder.add( new ValidationError( "name is required" ) );
        }
        if ( StringUtils.isBlank( displayName ) )
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

                final SiteConfigs siteConfigs = new SiteConfigsDataSerializer().fromProperties( this.contentData.getRoot() ).build();

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
            InputValidator.
                create().
                form( form ).
                inputTypeResolver( InputTypes.BUILTIN ).
                build().
                validate( siteConfig.getConfig() );
        }
        catch ( final Exception e )
        {
            this.resultBuilder.add( new SiteConfigValidationError( siteConfig.getApplicationKey().getName() ) );
        }
    }

    private void validateContentTypeForm( final PropertyTree contentData, final ContentType contentType )
    {
        if ( contentType != null )
        {
            this.resultBuilder.addAll( new OccurrenceValidator( contentType.getForm() ).validate( contentData.getRoot() ) );
        }
    }

    private void validateMetadata()
    {
        if ( this.extraDatas != null && this.extraDatas.isNotEmpty() )
        {
            for ( final ExtraData extraData : this.extraDatas )
            {
                final MixinName name = extraData.getName();

                final Mixin mixin = this.mixinService.getByName( name );
                if ( mixin == null )
                {
                    LOG.warn( "Mixin not found: '" + name );
                    continue;
                }

                final Form mixinForm = mixin.getForm();
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

        private MixinService mixinService;

        private SiteService siteService;

        private PropertyTree contentData;

        private ExtraDatas extraDatas;

        private ContentTypeName contentType;

        private ContentName name;

        private String displayName;

        private Builder()
        {
        }

        public Builder contentTypeService( ContentTypeService contentTypeService )
        {
            this.contentTypeService = contentTypeService;
            return this;
        }

        public Builder mixinService( MixinService mixinService )
        {
            this.mixinService = mixinService;
            return this;
        }

        public Builder siteService( SiteService siteService )
        {
            this.siteService = siteService;
            return this;
        }

        public Builder contentData( PropertyTree contentData )
        {
            this.contentData = contentData;
            return this;
        }

        public Builder extradatas( ExtraDatas extraDatas )
        {
            this.extraDatas = extraDatas;
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

        public ValidateContentDataCommand build()
        {
            return new ValidateContentDataCommand( this );
        }
    }
}
