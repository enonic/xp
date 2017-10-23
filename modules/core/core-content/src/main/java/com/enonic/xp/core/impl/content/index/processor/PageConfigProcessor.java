package com.enonic.xp.core.impl.content.index.processor;

import com.google.common.base.Preconditions;

import com.enonic.xp.core.impl.content.index.IndexConfigVisitor;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.form.Form;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexValueProcessors;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptorService;

import static com.enonic.xp.content.ContentPropertyNames.PAGE;
import static com.enonic.xp.content.ContentPropertyNames.PAGE_CONFIG;
import static com.enonic.xp.content.ContentPropertyNames.PAGE_TEXT_COMPONENT_PROPERTY_PATH_PATTERN;

public class PageConfigProcessor implements ContentIndexConfigProcessor
{
    private PageDescriptorService pageDescriptorService;

    private DescriptorKey descriptorKey;

    public final static IndexConfig TEXT_COMPONENT_INDEX_CONFIG = IndexConfig.create( IndexConfig.FULLTEXT ).
        addIndexValueProcessor( IndexValueProcessors.HTML_STRIPPER ).
        build();

    public PageConfigProcessor(final Builder builder) {
        this.pageDescriptorService = builder.pageDescriptorService;
        this.descriptorKey = builder.descriptorKey;
    }

    @Override
    public PatternIndexConfigDocument.Builder processDocument( final PatternIndexConfigDocument.Builder builder )
    {
        builder.add( PAGE, IndexConfig.NONE ).
            add( PropertyPath.from( PAGE, "controller"), IndexConfig.MINIMAL ).
                add( PropertyPath.from( PAGE_CONFIG, "*"), IndexConfig.BY_TYPE ).
            add( PAGE_TEXT_COMPONENT_PROPERTY_PATH_PATTERN, TEXT_COMPONENT_INDEX_CONFIG ).
            add( PropertyPath.from( PAGE, "regions" ), IndexConfig.NONE );

        final Form configForm = getConfigForm( pageDescriptorService, descriptorKey );

        if(configForm != null && configForm.getFormItems().size() > 0)
        {
            final IndexConfigVisitor indexConfigVisitor = new IndexConfigVisitor( PAGE_CONFIG, builder );
            indexConfigVisitor.traverse( configForm );
        }

        return builder;
    }

    private Form getConfigForm( final PageDescriptorService pageDescriptorService, final DescriptorKey descriptorKey )
    {
        return pageDescriptorService.getByKey( descriptorKey ).getConfig();
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {

        private PageDescriptorService pageDescriptorService;

        private DescriptorKey descriptorKey;

        public Builder pageDescriptorService( final PageDescriptorService value )
        {
            this.pageDescriptorService = value;
            return this;
        }

        public Builder descriptorKey( final DescriptorKey value )
        {
            this.descriptorKey = value;
            return this;
        }

        void validate()
        {
            Preconditions.checkNotNull( pageDescriptorService );
            Preconditions.checkNotNull( descriptorKey );
        }

        public PageConfigProcessor build()
        {
            validate();
            return new PageConfigProcessor( this );
        }
    }
}
