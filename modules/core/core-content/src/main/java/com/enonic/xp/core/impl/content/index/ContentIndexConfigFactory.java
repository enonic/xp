package com.enonic.xp.core.impl.content.index;

import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.core.impl.content.index.processor.AttachmentConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.BaseConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.ContentIndexConfigProcessors;
import com.enonic.xp.core.impl.content.index.processor.DataConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.PageConfigProcessor;
import com.enonic.xp.core.impl.content.index.processor.SiteConfigProcessor;
import com.enonic.xp.form.Form;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
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

        indexConfigProcessors.add( new PageConfigProcessor( getPageConfigForm( builder.pageDescriptorService, builder.descriptorKey ) ) );

        indexConfigProcessors.add( new SiteConfigProcessor( getSiteConfigForms( builder.siteService, builder.siteConfigs ) ) );
    }

    private Form getDataForm( final ContentTypeService contentTypeService, final ContentTypeName contentTypeName )
    {
        if ( contentTypeName == null || contentTypeService == null )
        {
            return null;
        }
        return contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) ).getForm();
    }

    private Form getPageConfigForm( final PageDescriptorService pageDescriptorService, final DescriptorKey descriptorKey )
    {
        if ( pageDescriptorService == null || descriptorKey == null )
        {
            return null;
        }
        return pageDescriptorService.getByKey( descriptorKey ).getConfig();
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

        private PageDescriptorService pageDescriptorService;

        private SiteService siteService;

        private ContentTypeName contentTypeName;

        private DescriptorKey descriptorKey;

        private SiteConfigs siteConfigs;

        public Builder contentTypeService( final ContentTypeService value )
        {
            this.contentTypeService = value;
            return this;
        }

        public Builder pageDescriptorService( final PageDescriptorService value )
        {
            this.pageDescriptorService = value;
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

        public Builder descriptorKey( final DescriptorKey value )
        {
            this.descriptorKey = value;
            return this;
        }

        public Builder siteConfigs( final SiteConfigs value )
        {
            this.siteConfigs = value;
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
