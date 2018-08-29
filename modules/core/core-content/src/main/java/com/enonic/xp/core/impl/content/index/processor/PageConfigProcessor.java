package com.enonic.xp.core.impl.content.index.processor;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.core.impl.content.index.IndexConfigVisitor;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.form.Form;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;

public class PageConfigProcessor
    implements ContentIndexConfigProcessor
{
    private final Form pageConfigForm;

    public static final String ALL_PATTERN = "*";

    public PageConfigProcessor( final Form pageConfigForm )
    {
        this.pageConfigForm = pageConfigForm;
    }

    @Override
    public PatternIndexConfigDocument.Builder processDocument( final PatternIndexConfigDocument.Builder builder )
    {
        builder.add( ContentPropertyNames.PAGE, IndexConfig.NONE ).
            add( PropertyPath.from( ContentPropertyNames.PAGE_CONTROLLER ), IndexConfig.MINIMAL ).
            add( PropertyPath.from( ContentPropertyNames.PAGE_TEMPLATE ), IndexConfig.MINIMAL ).
            add( PropertyPath.from( ContentPropertyNames.PAGE_CONFIG, ALL_PATTERN ), IndexConfig.BY_TYPE ).
            add( PropertyPath.from( ContentPropertyNames.PAGE_REGIONS ), IndexConfig.NONE );

        if ( this.pageConfigForm != null && this.pageConfigForm.getFormItems().size() > 0 )
        {
            final IndexConfigVisitor indexConfigVisitor = new IndexConfigVisitor( ContentPropertyNames.PAGE_CONFIG, builder );
            indexConfigVisitor.traverse( pageConfigForm );
        }

        return builder;
    }
}
