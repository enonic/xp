package com.enonic.xp.core.impl.content.index.processor;

import com.enonic.xp.core.impl.content.index.IndexConfigVisitor;
import com.enonic.xp.form.Form;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.page.Page;

import static com.enonic.xp.content.ContentPropertyNames.PAGE;
import static com.enonic.xp.data.PropertyPath.ELEMENT_DIVIDER;

public class PageConfigProcessor
    implements ContentIndexConfigProcessor
{
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
        if ( page == null )
        {
            return builder;
        }

        builder.add( COMPONENTS, IndexConfig.NONE ).
            add( String.join( ELEMENT_DIVIDER, COMPONENTS, PAGE, DESCRIPTOR ), IndexConfig.MINIMAL ).
            add( String.join( ELEMENT_DIVIDER, COMPONENTS, PAGE, TEMPLATE ), IndexConfig.MINIMAL ).
            add( String.join( ELEMENT_DIVIDER, COMPONENTS, PAGE, CUSTOMIZED ), IndexConfig.MINIMAL );

        applyConfigProcessors( builder );

        return builder;
    }

    static String getSanitizedAppName( final DescriptorKey descriptor )
    {
        return descriptor.getApplicationKey().toString().replace( DOT, DASH );
    }

    static String getSanitizedComponentName( final DescriptorKey descriptor )
    {
        return descriptor.getName().replace( DOT, DASH );
    }

    private void applyConfigProcessors( final PatternIndexConfigDocument.Builder builder )
    {
        if ( this.pageConfigForm == null || this.pageConfigForm.size() == 0 )
        {
            return;
        }

        final String appNameAsString = getSanitizedAppName( page.getDescriptor() );
        final String componentNameAsString = getSanitizedComponentName( page.getDescriptor() );

        final IndexConfigVisitor indexConfigVisitor =
            new IndexConfigVisitor( String.join( ELEMENT_DIVIDER, COMPONENTS, PAGE, CONFIG, appNameAsString, componentNameAsString ),
                                    builder );
        indexConfigVisitor.traverse( pageConfigForm );

        builder.add( String.join( ELEMENT_DIVIDER, COMPONENTS, PAGE, CONFIG, appNameAsString, componentNameAsString ), IndexConfig.BY_TYPE );
    }
}
