package com.enonic.xp.core.impl.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.site.ModuleConfig;
import com.enonic.xp.content.site.ModuleConfigs;
import com.enonic.xp.content.site.ModuleConfigsDataSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.content.validator.DataValidationErrors;
import com.enonic.xp.schema.content.validator.OccurrenceValidator;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;
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

    private final DataValidationErrors.Builder resultBuilder;

    private ValidateContentDataCommand( Builder builder )
    {
        contentTypeService = builder.contentTypeService;
        mixinService = builder.mixinService;
        siteService = builder.siteService;
        contentData = builder.contentData;
        extraDatas = builder.extraDatas;
        contentType = builder.contentType;
        resultBuilder = DataValidationErrors.create();
    }

    public static Builder create()
    {
        return new Builder();
    }


    DataValidationErrors execute()
    {
        return doExecute();
    }

    DataValidationErrors doExecute()
    {
        final PropertyTree contentData = this.contentData;
        final ContentTypeName contentTypeName = this.contentType;
        final ContentType contentType = contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) );

        Preconditions.checkArgument( contentType != null, "ContentType [%s] not found", contentTypeName );

        validateContentTypeForm( contentData, contentType );
        validateMetadata();
        validateSiteModuleConfigs( contentType );

        return this.resultBuilder.build();
    }

    private void validateSiteModuleConfigs( final ContentType contentType )
    {
        if ( contentType != null )
        {

            if ( contentType.getName().isSite() )
            {

                final ModuleConfigs moduleConfigs = new ModuleConfigsDataSerializer().fromProperties( this.contentData.getRoot() ).build();

                for ( final ModuleConfig moduleConfig : moduleConfigs )
                {
                    final ModuleKey moduleKey = moduleConfig.getModule();

                    final SiteDescriptor siteDescriptor = siteService.getDescriptor( moduleKey );

                    this.resultBuilder.addAll(
                        new OccurrenceValidator( siteDescriptor.getForm() ).validate( moduleConfig.getConfig().getRoot() ) );
                }
            }
        }
    }

    private void validateContentTypeForm( final PropertyTree contentData, final ContentType contentType )
    {
        if ( contentType != null )
        {
            this.resultBuilder.addAll( new OccurrenceValidator( contentType.form() ).validate( contentData.getRoot() ) );
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

                final Form mixinForm = Form.newForm().addFormItems( mixin.getFormItems() ).build();

                this.resultBuilder.addAll( new OccurrenceValidator( mixinForm ).validate( extraData.getData().getRoot() ) );
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

        public ValidateContentDataCommand build()
        {
            return new ValidateContentDataCommand( this );
        }
    }
}
