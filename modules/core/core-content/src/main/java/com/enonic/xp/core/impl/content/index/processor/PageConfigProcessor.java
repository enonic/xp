package com.enonic.xp.core.impl.content.index.processor;

import com.enonic.xp.core.impl.content.index.IndexConfigVisitor;
import com.enonic.xp.form.Form;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;

import static com.enonic.xp.content.ContentPropertyNames.PAGE;
import static com.enonic.xp.data.PropertyPath.ELEMENT_DIVIDER;

public class PageConfigProcessor
    implements ContentIndexConfigProcessor
{
    static final String ALL_PATTERN = "*";

    static final String COMPONENTS = "components";

    static final String CONFIG = "config";

    static final String DESCRIPTOR = "descriptor";

    static final String TEMPLATE = "template";

    static final String CUSTOMIZED = "customized";

    private static final String DOT = ".";

    private static final String DASH = "-";

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
            add( String.join( ELEMENT_DIVIDER, COMPONENTS, PAGE, DESCRIPTOR ), IndexConfig.MINIMAL ).
            add( String.join( ELEMENT_DIVIDER, COMPONENTS, PAGE, TEMPLATE ), IndexConfig.MINIMAL ).
            add( String.join( ELEMENT_DIVIDER, COMPONENTS, PAGE, CUSTOMIZED ), IndexConfig.MINIMAL );

        applyConfigProcessors( builder );

        return builder;
    }

    private void applyConfigProcessors( final PatternIndexConfigDocument.Builder builder )
    {
        if ( this.pageConfigForm == null || this.pageConfigForm.size() == 0 )
        {
            return;
        }

        final String appKeyAsString = appNameToConfigPropertyName( page.getDescriptor() );

        final IndexConfigVisitor indexConfigVisitor =
            new IndexConfigVisitor( String.join( ELEMENT_DIVIDER, COMPONENTS, PAGE, CONFIG, appKeyAsString ), builder );
        indexConfigVisitor.traverse( pageConfigForm );

        builder.add( String.join( ELEMENT_DIVIDER, COMPONENTS, PAGE, CONFIG, appKeyAsString, ALL_PATTERN ), IndexConfig.BY_TYPE );
    }

    static String appNameToConfigPropertyName( final DescriptorKey descriptor )
    {
        return descriptor.getApplicationKey().toString().replace( DOT, DASH );
    }
}
