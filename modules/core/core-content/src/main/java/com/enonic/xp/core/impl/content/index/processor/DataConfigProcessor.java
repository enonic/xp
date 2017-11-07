package com.enonic.xp.core.impl.content.index.processor;

import com.enonic.xp.core.impl.content.index.IndexConfigVisitor;
import com.enonic.xp.form.Form;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;

import static com.enonic.xp.content.ContentPropertyNames.DATA;

public class DataConfigProcessor
    implements ContentIndexConfigProcessor
{
    private final Form dataForm;

    public DataConfigProcessor( final Form dataFrom) {
        this.dataForm = dataFrom;
    }

    @Override
    public PatternIndexConfigDocument.Builder processDocument( final PatternIndexConfigDocument.Builder builder )
    {
        builder.add( DATA, IndexConfig.BY_TYPE );

        if(this.dataForm != null && this.dataForm.getFormItems().size() > 0)
        {
            final IndexConfigVisitor indexConfigVisitor = new IndexConfigVisitor( DATA, builder );
            indexConfigVisitor.traverse( this.dataForm );
        }

        return builder;
    }
}
