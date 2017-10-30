package com.enonic.xp.core.impl.content.index.processor;

import com.enonic.xp.core.impl.content.index.IndexConfigVisitor;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.form.Form;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexValueProcessors;
import com.enonic.xp.index.PatternIndexConfigDocument;

import static com.enonic.xp.content.ContentPropertyNames.PAGE;
import static com.enonic.xp.content.ContentPropertyNames.PAGE_CONFIG;
import static com.enonic.xp.content.ContentPropertyNames.PAGE_TEXT_COMPONENT_PROPERTY_PATH_PATTERN;

public class PageConfigProcessor implements ContentIndexConfigProcessor
{
    private Form pageConfigForm;

    public final static IndexConfig TEXT_COMPONENT_INDEX_CONFIG = IndexConfig.create( IndexConfig.FULLTEXT ).
        addIndexValueProcessor( IndexValueProcessors.HTML_STRIPPER ).
        build();

    public PageConfigProcessor(final Form pageConfigForm) {
        this.pageConfigForm = pageConfigForm;
    }

    @Override
    public PatternIndexConfigDocument.Builder processDocument( final PatternIndexConfigDocument.Builder builder )
    {
        builder.add( PAGE, IndexConfig.NONE ).
            add( PropertyPath.from( PAGE, "controller"), IndexConfig.MINIMAL ).
                add( PropertyPath.from( PAGE_CONFIG, "*"), IndexConfig.BY_TYPE ).
            add( PAGE_TEXT_COMPONENT_PROPERTY_PATH_PATTERN, TEXT_COMPONENT_INDEX_CONFIG ).
            add( PropertyPath.from( PAGE, "regions" ), IndexConfig.NONE );

        if(this.pageConfigForm != null && this.pageConfigForm.getFormItems().size() > 0)
        {
            final IndexConfigVisitor indexConfigVisitor = new IndexConfigVisitor( PAGE_CONFIG, builder );
            indexConfigVisitor.traverse( pageConfigForm );
        }

        return builder;
    }
}
