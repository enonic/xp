package com.enonic.xp.core.impl.content.index.processor;

import java.util.Collection;

import com.enonic.xp.core.impl.content.index.IndexConfigVisitor;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.form.Form;
import com.enonic.xp.index.PatternIndexConfigDocument;

import static com.enonic.xp.content.ContentPropertyNames.DATA;
import static com.enonic.xp.content.ContentPropertyNames.SITECONFIG;

public class CmsConfigProcessor
    implements ContentIndexConfigProcessor
{
    private final Collection<Form> sizeConfigForms;

    public CmsConfigProcessor( final Collection<Form> sizeConfigForm )
    {
        this.sizeConfigForms = sizeConfigForm;
    }

    @Override
    public PatternIndexConfigDocument processDocument( final PatternIndexConfigDocument config )
    {
        if ( this.sizeConfigForms != null && !this.sizeConfigForms.isEmpty() )
        {
            final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create( config );
            sizeConfigForms.forEach( form -> {
                final IndexConfigVisitor indexConfigVisitor =
                    new IndexConfigVisitor( String.join( PropertyPath.ELEMENT_DIVIDER, DATA, SITECONFIG, "config" ), builder );
                indexConfigVisitor.traverse( form );
            } );
            return builder.build();
        }
        return config;
    }
}
