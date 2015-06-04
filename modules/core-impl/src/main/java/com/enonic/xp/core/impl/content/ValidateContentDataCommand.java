package com.enonic.xp.core.impl.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.site.SiteConfig;
import com.enonic.xp.content.site.SiteConfigs;
import com.enonic.xp.content.site.SiteConfigsDataSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.content.validator.DataValidationErrors;
import com.enonic.xp.schema.content.validator.OccurrenceValidator;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;

final class ValidateContentDataCommand
{
    private final static Logger LOG = LoggerFactory.getLogger( ValidateContentDataCommand.class );

    private final ContentTypeService contentTypeService;

    private final MixinService mixinService;

    private final ModuleService moduleService;

    private final PropertyTree contentData;

    private final ExtraDatas extraDatas;

    private final ContentTypeName contentType;

    private final DataValidationErrors.Builder resultBuilder;

    private ValidateContentDataCommand( Builder builder )
    {
        contentTypeService = builder.contentTypeService;
        mixinService = builder.mixinService;
        moduleService = builder.moduleService;
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
        validateSiteConfigs( contentType );

        return this.resultBuilder.build();
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
                    final ModuleKey moduleKey = siteConfig.getModule();

                    final Module module = moduleService.getModule( moduleKey );

                    this.resultBuilder.addAll( new OccurrenceValidator( module.getConfig() ).validate( siteConfig.getConfig().getRoot() ) );
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

        private ModuleService moduleService;

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

        public Builder moduleService( ModuleService moduleService )
        {
            this.moduleService = moduleService;
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
