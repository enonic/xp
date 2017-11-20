package com.enonic.xp.core.impl.content.index;

import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.core.impl.content.index.processor.AttachmentConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.BaseConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.ContentIndexConfigProcessors;
import com.enonic.xp.core.impl.content.index.processor.DataConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.PageRegionsConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.SiteConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.XDataConfigProcessor;
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
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.mixin.Mixins;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteService;

public class ContentIndexConfigFactory
{
    private final ContentIndexConfigProcessors indexConfigProcessors = new ContentIndexConfigProcessors();

    public ContentIndexConfigFactory( final Builder builder )
    {
        indexConfigProcessors.add( new BaseConfigProcessor() );

        indexConfigProcessors.add( new AttachmentConfigProcessor( builder.contentTypeName ) );

        indexConfigProcessors.add( new DataConfigProcessor( getDataForm( builder.contentTypeService, builder.contentTypeName ) ) );

        indexConfigProcessors.add( new XDataConfigProcessor( getMixins( builder.mixinService, builder.extraDatas ) ) );

        indexConfigProcessors.add( new PageConfigProcessor( getPageConfigForm( builder.pageDescriptorService, builder.page ) ) );

        indexConfigProcessors.add( new SiteConfigProcessor( getSiteConfigForms( builder.siteService, builder.siteConfigs ) ) );

        indexConfigProcessors.add(
            new PageRegionsConfigProcessor( builder.page, builder.partDescriptorService, builder.layoutDescriptorService ) );
    }

    private Form getDataForm( final ContentTypeService contentTypeService, final ContentTypeName contentTypeName )
    {
        if ( contentTypeName == null || contentTypeService == null )
        {
            return null;
        }
        return contentTypeService.getByName( new GetContentTypeParams().
            inlineMixinsToFormItems( true ).
            contentTypeName( contentTypeName ) ).
            getForm();
    }

    private Mixins getMixins( final MixinService mixinService, final ExtraDatas extraDatas )
    {
        if ( mixinService == null || extraDatas == null )
        {
            return null;
        }
        return mixinService.getByNames( extraDatas.getNames() );
    }

    private Form getPageConfigForm( final PageDescriptorService pageDescriptorService, final Page page )
    {
        if ( pageDescriptorService == null || page == null || page.getController() == null )
        {
            return null;
        }
        return pageDescriptorService.getByKey( page.getController() ).getConfig();
    }

    private Collection<Form> getSiteConfigForms( final SiteService siteService, final SiteConfigs siteConfigs )
    {
        if ( siteService == null || siteConfigs == null )
        {
            return null;
        }
        return siteConfigs.stream().
            map( siteConfig -> siteService.getDescriptor( siteConfig.getApplicationKey() ) ).
            filter( siteDescriptor -> siteDescriptor != null && siteDescriptor.getForm() != null ).
            map( siteDescriptor -> siteDescriptor.getForm() ).
            collect( Collectors.toList() );
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

        private SiteService siteService;

        private ContentTypeName contentTypeName;

        private Page page;

        private SiteConfigs siteConfigs;

        private ExtraDatas extraDatas;

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

        public Builder siteService( final SiteService value )
        {
            this.siteService = value;
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

        public Builder extraDatas( final ExtraDatas value )
        {
            this.extraDatas = value;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( contentTypeService );
        }

        public ContentIndexConfigFactory build()
        {
            validate();
            return new ContentIndexConfigFactory( this );
        }
    }
}
