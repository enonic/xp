package com.enonic.xp.core.impl.content.index.processor;

import com.enonic.xp.core.impl.content.index.IndexConfigVisitor;
import com.enonic.xp.form.Form;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;

import static com.enonic.xp.content.ContentPropertyNames.PAGE;
import static com.enonic.xp.core.impl.content.index.processor.PageRegionsConfigProcessor.ANY_PATH_PATTERN;
import static com.enonic.xp.core.impl.content.index.processor.PageRegionsConfigProcessor.COMPONENTS;
import static com.enonic.xp.core.impl.content.index.processor.PageRegionsConfigProcessor.CONFIG;
import static com.enonic.xp.data.PropertyPath.ELEMENT_DIVIDER;

public class PageConfigProcessor
    implements ContentIndexConfigProcessor
{
    public static final String PAGE_COMPONENT_PATH = String.join( ELEMENT_DIVIDER, COMPONENTS, ANY_PATH_PATTERN, PAGE );

    public static final String ALL_PATTERN = "*";

    private final Form pageConfigForm;

    private final Page page;

    public PageConfigProcessor( final Page page, final Form pageConfigForm )
    {
        this.page = page;
        this.pageConfigForm = pageConfigForm;
    }

    @Override
    public PatternIndexConfigDocument.Builder processDocument( final PatternIndexConfigDocument.Builder builder )
    {
        builder.add( COMPONENTS, IndexConfig.NONE ).
            add( String.join( ELEMENT_DIVIDER, PAGE_COMPONENT_PATH, "descriptor" ), IndexConfig.MINIMAL ).
            add( String.join( ELEMENT_DIVIDER, PAGE_COMPONENT_PATH, "template" ), IndexConfig.MINIMAL );

        if ( this.pageConfigForm != null && this.pageConfigForm.getFormItems().size() > 0 )
        {
            final String appKeyAsString = appNameToConfigPropertyName( page.getDescriptor() );

            final IndexConfigVisitor indexConfigVisitor =
                new IndexConfigVisitor( String.join( ELEMENT_DIVIDER, PAGE_COMPONENT_PATH, CONFIG, appKeyAsString ), builder );
            indexConfigVisitor.traverse( pageConfigForm );

            builder.add( String.join( ELEMENT_DIVIDER, PAGE_COMPONENT_PATH, CONFIG, appKeyAsString, ALL_PATTERN ), IndexConfig.BY_TYPE );
        }

        return builder;
    }

    static String appNameToConfigPropertyName( final DescriptorKey descriptor )
    {
        return descriptor.getApplicationKey().toString().replace( ".", "-" );
    }
}
