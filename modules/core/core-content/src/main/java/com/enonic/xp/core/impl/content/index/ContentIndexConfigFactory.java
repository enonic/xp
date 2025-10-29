package com.enonic.xp.core.impl.content.index;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import com.enonic.xp.content.Mixins;
import com.enonic.xp.core.impl.content.index.processor.AttachmentConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.BaseConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.CmsConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.ContentIndexConfigProcessors;
import com.enonic.xp.core.impl.content.index.processor.DataConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.LanguageConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.MixinConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.PageRegionsConfigProcessor;
import com.enonic.xp.form.Form;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.MixinDescriptors;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.CmsService;
import com.enonic.xp.site.SiteConfigs;

public class ContentIndexConfigFactory
{
    private final ContentIndexConfigProcessors indexConfigProcessors = new ContentIndexConfigProcessors();

    public ContentIndexConfigFactory( final Builder builder )
    {
        indexConfigProcessors.add( new BaseConfigProcessor() );

        indexConfigProcessors.add( new AttachmentConfigProcessor( builder.contentTypeName ) );

        indexConfigProcessors.add( new DataConfigProcessor( getDataForm( builder.contentTypeService, builder.contentTypeName ) ) );

        indexConfigProcessors.add( new MixinConfigProcessor( getMixinDescriptors( builder.mixinService, builder.extraDatas ) ) );

        indexConfigProcessors.add(
            new PageConfigProcessor( builder.page, getPageConfigForm( builder.pageDescriptorService, builder.page ) ) );

        indexConfigProcessors.add( new CmsConfigProcessor( getSiteConfigForms( builder.cmsService, builder.siteConfigs ) ) );

        indexConfigProcessors.add(
            new PageRegionsConfigProcessor( builder.page, builder.partDescriptorService, builder.layoutDescriptorService ) );

        indexConfigProcessors.add( new LanguageConfigProcessor( builder.language ) );
    }

    private Form getDataForm( final ContentTypeService contentTypeService, final ContentTypeName contentTypeName )
    {
        if ( contentTypeName == null || contentTypeService == null )
        {
            return null;
        }
        return contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) ).getForm();
    }

    private MixinDescriptors getMixinDescriptors( final MixinService mixinService, final Mixins mixins )
    {
        if ( mixinService == null || mixins == null )
        {
            return null;
        }
        return mixinService.getByNames( mixins.getNames() );
    }

    private Form getPageConfigForm( final PageDescriptorService pageDescriptorService, final Page page )
    {
        if ( pageDescriptorService == null || page == null || page.getDescriptor() == null )
        {
            return null;
        }
        return pageDescriptorService.getByKey( page.getDescriptor() ).getConfig();
    }

    private Collection<Form> getSiteConfigForms( final CmsService siteService, final SiteConfigs siteConfigs )
    {
        if ( siteService == null || siteConfigs == null )
        {
            return null;
        }
        return siteConfigs.stream()
            .map( siteConfig -> siteService.getDescriptor( siteConfig.getApplicationKey() ) )
            .filter( cmsDescriptor -> cmsDescriptor != null && cmsDescriptor.getForm() != null )
            .map( CmsDescriptor::getForm )
            .collect( Collectors.toList() );
    }

    public IndexConfigDocument produce()
    {
        final PatternIndexConfigDocument.Builder configDocumentBuilder = PatternIndexConfigDocument.create();

        indexConfigProcessors.forEach(
            contentIndexConfigProcessor -> contentIndexConfigProcessor.processDocument( configDocumentBuilder ) );

        return configDocumentBuilder.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ContentTypeService contentTypeService;

        private MixinService mixinService;

        private PageDescriptorService pageDescriptorService;

        private PartDescriptorService partDescriptorService;

        private LayoutDescriptorService layoutDescriptorService;

        private CmsService cmsService;

        private ContentTypeName contentTypeName;

        private Page page;

        private SiteConfigs siteConfigs;

        private Mixins extraDatas;

        private String language;

        public Builder contentTypeService( final ContentTypeService value )
        {
            this.contentTypeService = value;
            return this;
        }

        public Builder mixinService( final MixinService value )
        {
            this.mixinService = value;
            return this;
        }

        public Builder pageDescriptorService( final PageDescriptorService value )
        {
            this.pageDescriptorService = value;
            return this;
        }

        public Builder partDescriptorService( final PartDescriptorService value )
        {
            this.partDescriptorService = value;
            return this;
        }

        public Builder layoutDescriptorService( final LayoutDescriptorService value )
        {
            this.layoutDescriptorService = value;
            return this;
        }

        public Builder cmsService( final CmsService value )
        {
            this.cmsService = value;
            return this;
        }

        public Builder contentTypeName( final ContentTypeName value )
        {
            this.contentTypeName = value;
            return this;
        }

        public Builder page( final Page value )
        {
            this.page = value;
            return this;
        }

        public Builder siteConfigs( final SiteConfigs value )
        {
            this.siteConfigs = value;
            return this;
        }

        public Builder extraDatas( final Mixins value )
        {
            this.extraDatas = value;
            return this;
        }

        public Builder language( final String value )
        {
            this.language = value;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( contentTypeService );
        }

        public ContentIndexConfigFactory build()
        {
            validate();
            return new ContentIndexConfigFactory( this );
        }
    }
}
